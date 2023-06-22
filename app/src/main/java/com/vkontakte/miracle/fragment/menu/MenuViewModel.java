package com.vkontakte.miracle.fragment.menu;

import static com.miracle.engine.util.BitmapUtil.loadBitmapFromUrl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.vkontakte.miracle.memory.storage.UsersStorage;
import com.vkontakte.miracle.model.auth.User;
import com.vkontakte.miracle.response.auth.UnregisterDeviceResponse;
import com.vkontakte.miracle.response.users.UserResponse;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MenuViewModel extends ViewModel {

    private Disposable disposable;

    public final MutableLiveData<LoadingType> loadingType = new MutableLiveData<>(LoadingType.FIRST_TIME);
    public final MutableLiveData<ViewState> viewState = new MutableLiveData<>(new ViewState.Loading());

    private User user = null;
    private boolean loading = true;

    { loadUserFromStorage(); }

    private void loadUserFromStorage(){
        disposable = Single.fromCallable(() -> UsersStorage.get().requireCurrentUser())
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(loadedUser -> {
                    endLoading();
                    user = loadedUser;
                    viewState.setValue(new ViewState.Success(user));
                }, throwable -> {
                    endLoading();
                    viewState.setValue(new ViewState.Error(throwable.getMessage()));
                });
    }

    private void loadUserFromServerAndUpdate(String id, String accessToken){
        disposable = Single.fromCallable(() -> {
                    UserResponse userResponse = UserResponse.call(id, accessToken);
                    UsersStorage usersStorage = UsersStorage.get();
                    usersStorage.updateCurrentUser(userResponse.getUser());
                    Bitmap photo = loadBitmapFromUrl(user.getPhoto200());
                    usersStorage.saveBitmapForCurrentUser(photo, "userImage200.png");
                    return userResponse;
                }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(userResponse -> {
                    endLoading();
                    user = userResponse.getUser();
                    viewState.setValue(new ViewState.Success(user));
                }, throwable -> {
                    endLoading();
                    viewState.setValue(new ViewState.Error(throwable.getMessage()));
                });
    }

    public boolean tryRefresh(){
        if(checkAndStartLoading(LoadingType.REFRESHING)){
            if(user==null){
                loadUserFromStorage();
            } else {
                loadUserFromServerAndUpdate(user.getId(),user.getAccessToken());
            }
            return true;
        }
        return false;
    }

    public boolean tryExit(){
        if(checkAndStartLoading(LoadingType.EXIT_PROCESS)){
            exit();
            return true;
        }
        return false;
    }

    private void exit(){
        disposable = Single.fromCallable(() -> {
                    UsersStorage.get().setCurrentUserId(null);
                    UnregisterDeviceResponse unregisterDeviceResponse = UnregisterDeviceResponse.call();
                    return true;
                }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(userResponse -> {
                    endLoading();
                    viewState.setValue(new ViewState.Exit());
                }, throwable -> {
                    endLoading();
                    viewState.setValue(new ViewState.Error(throwable.getMessage()));
                });
    }

    private boolean checkAndStartLoading(LoadingType loadingType){
        if(loading) return false;
        loading = true;
        this.loadingType.setValue(loadingType);
        return true;
    }

    private void endLoading(){
        loading = false;
        loadingType.setValue(LoadingType.NO_LOADING);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if(disposable!=null&&!disposable.isDisposed()){
            disposable.dispose();
        }
    }

    enum LoadingType{
        FIRST_TIME,
        REFRESHING,
        EXIT_PROCESS,
        NO_LOADING
    }

    public static class ViewState {

        public static class Loading extends ViewState { }

        public static class Success extends ViewState {

            private final User user;

            public Success(User user) {
                this.user = user;
            }

            public User getUser() {
                return user;
            }
        }

        public static class Error extends ViewState {

            private final String errorMessage;

            public Error(String errorMessage) {
                this.errorMessage = errorMessage;
            }

            public String getErrorMessage() {
                return errorMessage;
            }
        }

        public static class Exit extends ViewState { }

    }

}
