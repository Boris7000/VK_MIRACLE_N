package com.vkontakte.miracle.activity.auth;

import static com.miracle.engine.util.BitmapUtil.loadBitmapFromUrl;
import static com.miracle.engine.util.StringsUtil.nonNullAndNonEmpty;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.vkontakte.miracle.MainApp;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.memory.storage.UsersStorage;
import com.vkontakte.miracle.model.auth.Captcha;
import com.vkontakte.miracle.model.auth.Validation;
import com.vkontakte.miracle.model.auth.User;
import com.vkontakte.miracle.response.auth.AuthResponse;
import com.vkontakte.miracle.response.auth.RegisterDeviceResponse;
import com.vkontakte.miracle.response.auth.ValidatePhoneResponse;
import com.vkontakte.miracle.response.users.UserResponse;
import com.vkontakte.miracle.throwable.auth.InvalidClientException;
import com.vkontakte.miracle.throwable.auth.NeedCaptchaException;
import com.vkontakte.miracle.throwable.auth.NeedValidationException;

import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AuthViewModel extends ViewModel {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private CountDownTimer delayTimer = null;

    public final MutableLiveData<List<User>> users = new MutableLiveData<>(); { loadUsers(); }

    public final MutableLiveData<ViewState> viewState = new MutableLiveData<>(new ViewState.NeedAuth());
    public final MutableLiveData<MessageState> messageSate = new MutableLiveData<>();
    public final MutableLiveData<LoadingType> loadingType = new MutableLiveData<>(LoadingType.NO_LOADING);
    public final MutableLiveData<Long> smsRequestDelay = new MutableLiveData<>(60L);


    public final MutableLiveData<String> login = new MutableLiveData<>("");
    public final MutableLiveData<String> password = new MutableLiveData<>("");
    public final MutableLiveData<String> appValidationCode = new MutableLiveData<>("");
    public final MutableLiveData<String> smsValidationCode = new MutableLiveData<>("");
    public final MutableLiveData<String> captchaKey = new MutableLiveData<>("");

    private Captcha captcha = null;
    private Validation validation = null;
    private boolean loading = false;

    private void loadUsers(){
        Disposable disposable = Single.fromCallable(() -> UsersStorage.get().loadUsers())
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(users::setValue, throwable -> sendErrorMessage(throwable.getMessage()));
        compositeDisposable.add(disposable);
    }

    ////////////////////////////////////////////////////////////////////////////

    public void tryAuth(){
        tryAuth(false);
    }

    public void tryAuth(boolean forceCode){
        if(checkAndStartLoading()){
            final HashMap<String,Object> authFields = new HashMap<>();
            final String login = AuthViewModel.this.login.getValue();
            final String password = AuthViewModel.this.password.getValue();

            if(forceCode){
                authFields.put("force_sms", 1);
                sendInfoMessage(stringFromApp(R.string.smsCodeRequest));
            } else if(validation!=null){
                String code = nonNullAndNonEmpty(appValidationCode.getValue())?
                        appValidationCode.getValue():nonNullAndNonEmpty(smsValidationCode.getValue())?
                        smsValidationCode.getValue():"";
                authFields.put("code", code);
                authFields.put("validation_sid", validation.getValidationSid());
                sendInfoMessage(stringFromApp(R.string.sendingCode));
            } else if(captcha!=null){
                authFields.put("captcha_key", captchaKey.getValue());
                authFields.put("captcha_sid", captcha.getCaptchaSid());
                sendInfoMessage(stringFromApp(R.string.sendingCode));
            } else {
                sendInfoMessage(stringFromApp(R.string.authorization));
            }

            Disposable disposable = Single.fromCallable(() -> AuthResponse.call(login,password,authFields))
                    .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(authResponse -> getAndSaveUser(authResponse.getUserId(), authResponse.getToken()),
                            throwable -> {
                                if (throwable instanceof NeedCaptchaException) {
                                    endLoading();
                                    clearMessage();
                                    validation = null;
                                    captcha = Captcha.fromException((NeedCaptchaException) throwable);
                                    viewState.setValue(new ViewState.NeedCaptcha(captcha.getCaptchaImg()));
                                } else if (throwable instanceof NeedValidationException) {
                                    captcha = null;
                                    validation = Validation.fromException((NeedValidationException) throwable);
                                    if (validation.getValidationType().equals("2fa_sms") ||
                                            validation.getValidationType().equals("2fa_libverify")) {
                                        viewState.setValue(new ViewState.NeedSmsValidation());
                                        validatePhone();
                                    } else if (validation.getValidationType().equals("2fa_app")) {
                                        endLoading();
                                        clearMessage();
                                        viewState.setValue(new ViewState.NeedAppValidation());
                                    }
                                } else {
                                    endLoading();
                                    sendErrorMessage(throwable.getMessage());
                                    if (throwable instanceof InvalidClientException) {
                                        if (!(viewState.getValue() instanceof ViewState.NeedAuth)) {
                                            captcha = null;
                                            validation = null;
                                            viewState.setValue(new ViewState.NeedAuth());
                                        }
                                    }
                                }
                            });
            compositeDisposable.add(disposable);
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    public void tryAuthFromUser(User user){
        if(checkAndStartLoading()){
            getAndSaveUser(user.getId(), user.getAccessToken());
        }
    }

    private void getAndSaveUser(String userId, String accessToken){
        sendInfoMessage(stringFromApp(R.string.userDataRequest));

        Disposable disposable = Single.fromCallable(() -> {
                    UserResponse userResponse = UserResponse.call(userId, accessToken);
                    User user = userResponse.getUser();
                    UsersStorage usersStorage = UsersStorage.get();
                    User oldUser = usersStorage.findUserById(userId);
                    if(oldUser==null){
                        usersStorage.insertUser(user);
                    } else {
                        usersStorage.updateUser(user);
                    }
                    Bitmap photo = loadBitmapFromUrl(user.getPhoto200());
                    usersStorage.saveBitmapForUser(photo, "userImage200.png", userId);
                    usersStorage.setCurrentUserId(userId);
                    RegisterDeviceResponse registerDeviceResponse = RegisterDeviceResponse.call();
                    return userResponse;
                }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(userResponse -> {
                    endLoading();
                    clearMessage();
                    viewState.setValue(new ViewState.AuthSuccess());
                }, throwable ->{
                    endLoading();
                    sendErrorMessage(throwable.getMessage());
                } );
        compositeDisposable.add(disposable);
    }

    ////////////////////////////////////////////////////////////////////////////

    public void tryValidatePhone(){
        if(checkAndStartLoading()){
            validatePhone();
        }
    }

    private void validatePhone(){
        sendInfoMessage(stringFromApp(R.string.smsCodeRequest));

        Disposable disposable = Single.fromCallable(() -> ValidatePhoneResponse.call(validation.getValidationSid()))
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(validatePhoneResponse -> {
                    String messageText = String.format(stringFromApp(
                            R.string.validationSMSDescription),
                            validation.getPhoneMask());
                    endLoading();
                    sendInfoMessage(messageText);
                    long newDelay = validatePhoneResponse.getDelay() * 1000L;
                    smsRequestDelay.setValue(newDelay);
                    startNewTimer(newDelay);
                }, throwable -> {
                    endLoading();
                    sendErrorMessage(throwable.getMessage());
                });

        compositeDisposable.add(disposable);
    }

    ////////////////////////////////////////////////////////////////////////////

    public void tryCancelCaptcha(){
        if(!loading){
            captcha = null;
            goToAuthState();
        }
    }

    public void tryCancelValidation(){
        if(!loading){
            cancelTimer();
            validation = null;
            goToAuthState();
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    private void startNewTimer(long newDelay){
        cancelTimer();
        delayTimer = new CountDownTimer(newDelay,1000L) {
            @Override public void onTick(long millisUntilFinished) {
                smsRequestDelay.setValue(millisUntilFinished);
            }
            @Override public void onFinish() {
                smsRequestDelay.setValue(0L);
                delayTimer = null;
            }
        };
        delayTimer.start();
    }

    private void cancelTimer(){
        if(delayTimer!=null){
            delayTimer.cancel();
            delayTimer = null;
        }
    }

    private void goToAuthState(){
        clearMessage();
        viewState.setValue(new ViewState.NeedAuth());
    }

    private String stringFromApp(int resource){
        return MainApp.get().getString(resource);
    }

    ////////////////////////////////////////////////////////////////////////////

    private boolean checkAndStartLoading(){
        if(loading) return false;
        loading = true;
        loadingType.setValue(LoadingType.LOADING);
        return true;
    }

    private void endLoading(){
        loading = false;
        loadingType.setValue(LoadingType.NO_LOADING);
    }

    private void sendErrorMessage(String message){
        messageSate.setValue(new MessageState.Error(message));
    }

    private void sendInfoMessage(String message){
        messageSate.setValue(new MessageState.Info(message));
    }

    private void clearMessage(){
        messageSate.setValue(null);
    }

    ////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCleared() {
        super.onCleared();
        cancelTimer();
        compositeDisposable.clear();
    }

    enum LoadingType{
        LOADING,
        NO_LOADING
    }

    public static class ViewState {

        public static class NeedAuth extends ViewState { }

        public static class NeedCaptcha extends ViewState {

            private final String captchaImg;

            public NeedCaptcha(String captchaImg) {
                this.captchaImg = captchaImg;
            }

            public String getCaptchaImg() {
                return captchaImg;
            }

        }

        public static class NeedAppValidation extends ViewState { }

        public static class NeedSmsValidation extends ViewState { }

        public static class AuthSuccess extends ViewState { }
    }

    public static class MessageState {
        private final String message;
        public MessageState(String message) {
            this.message = message;
        }
        public String getMessage() {
            return message;
        }
        public static class Info extends MessageState {
            public Info(String message) {
                super(message);
            }
        }
        public static class Error extends MessageState {
            public Error(String message) {
                super(message);
            }
        }
    }

}
