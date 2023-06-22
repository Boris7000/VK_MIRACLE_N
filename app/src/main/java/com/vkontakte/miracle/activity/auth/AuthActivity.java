package com.vkontakte.miracle.activity.auth;

import static com.miracle.engine.util.DimensionUtil.dpToPx;
import static com.miracle.engine.util.StringsUtil.nonNullAndNonEmpty;
import static com.miracle.engine.util.StringsUtil.trimEditable;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.transition.TransitionManager;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.view.WindowCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.miracle.engine.activity.MiracleActivity;
import com.miracle.engine.recyclerview.DynamicBindingAdapter;
import com.miracle.engine.recyclerview.TypedData;
import com.miracle.engine.recyclerview.viewholder.bundle.ViewHolderBundle;
import com.miracle.widget.ExtendedMaterialButton;
import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.activity.main.MainActivity;
import com.vkontakte.miracle.model.auth.User;
import com.vkontakte.miracle.util.TimeUtil;
import com.vkontakte.miracle.util.constants.TypedDataConstants;
import com.vkontakte.miracle.util.overrides.SimpleTextWatcher;
import com.vkontakte.miracle.viewholder.auth.AccountViewHolderBundle;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.util.ArrayList;

public class AuthActivity extends MiracleActivity {

    private AuthViewModel authViewModel;

    private AuthHelper authHelper;
    private CaptchaHelper captchaHelper;
    private AppValidationHelper appValidationHelper;
    private SMSValidationHelper smsValidationHelper;

    private ViewGroup contentList;
    private View visibleView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.auth_activity);

        contentList = findViewById(R.id.contentList);

        RecyclerView recyclerView = contentList.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));

        ArrayMap<Integer, ViewHolderBundle<RecyclerView.ViewHolder, Object>> bundles = new ArrayMap<>();
        AccountViewHolderBundle accountViewHolderBundle = new AccountViewHolderBundle() {
            @Override public void onClick(User user) {
                authViewModel.tryAuthFromUser(user);
            }
            @Override public void onLongClick(User user) {

            }
        };
        bundles.put(TypedDataConstants.TYPE_USER, accountViewHolderBundle);
        DynamicBindingAdapter adapter = new DynamicBindingAdapter(bundles);
        recyclerView.setAdapter(adapter);
        authViewModel.users.observe(this, users -> {
            ArrayList<TypedData> items = new ArrayList<>(users);
            adapter.setItems(items);
        });

        TextView infoMessageText = contentList.findViewById(R.id.infoMessage);
        TextView errorMessageText = contentList.findViewById(R.id.errorMessage);

        authViewModel.messageSate.observe(this, messageState -> {
            if(messageState==null){
                infoMessageText.setVisibility(View.GONE);
                errorMessageText.setVisibility(View.GONE);
            } else if (messageState instanceof AuthViewModel.MessageState.Info){
                infoMessageText.setVisibility(View.VISIBLE);
                errorMessageText.setVisibility(View.GONE);
                infoMessageText.setText(messageState.getMessage());
            } else if(messageState instanceof AuthViewModel.MessageState.Error){
                infoMessageText.setVisibility(View.GONE);
                errorMessageText.setVisibility(View.VISIBLE);
                errorMessageText.setText(messageState.getMessage());
            }
        });

        ProgressBar loginProgress = contentList.findViewById(R.id.loginProgress);

        authViewModel.loadingType.observe(this, loadingType -> {
            switch (loadingType){
                case LOADING:{
                    loginProgress.setVisibility(View.VISIBLE);
                    break;
                }
                case NO_LOADING:{
                    loginProgress.setVisibility(View.GONE);
                    break;
                }
            }
        });

        ViewGroup framesRoot = contentList.findViewById(R.id.framesRoot);

        authHelper = new AuthHelper(framesRoot.findViewById(R.id.fragmentAuthVS));

        captchaHelper = new CaptchaHelper(framesRoot.findViewById(R.id.fragmentCaptchaVS));

        appValidationHelper = new AppValidationHelper(framesRoot.findViewById(R.id.fragmentAppValidationVS));

        smsValidationHelper = new SMSValidationHelper( framesRoot.findViewById(R.id.fragmentSMSValidationVS));

        authViewModel.viewState.observe(this, viewState -> {
            if(viewState instanceof AuthViewModel.ViewState.NeedAuth){
                captchaHelper.clear();
                appValidationHelper.clear();
                smsValidationHelper.clear();
                View authRoot = authHelper.getAuthRoot();
                authHelper.bind();
                switchFragments(authRoot,visibleView!=null);
            } else if(viewState instanceof AuthViewModel.ViewState.NeedCaptcha){
                authHelper.clear();
                appValidationHelper.clear();
                smsValidationHelper.clear();
                View captchaRoot = captchaHelper.getCaptchaRoot();
                captchaHelper.bind(((AuthViewModel.ViewState.NeedCaptcha) viewState).getCaptchaImg());
                switchFragments(captchaRoot,visibleView!=null);
            } else if(viewState instanceof AuthViewModel.ViewState.NeedAppValidation){
                authHelper.clear();
                captchaHelper.clear();
                smsValidationHelper.clear();
                View appValidationRoot = appValidationHelper.getAppValidationRoot();
                appValidationHelper.bind();
                switchFragments(appValidationRoot,visibleView!=null);
            } else if(viewState instanceof AuthViewModel.ViewState.NeedSmsValidation){
                authHelper.clear();
                captchaHelper.clear();
                appValidationHelper.clear();
                View smsValidationRoot = smsValidationHelper.getSMSValidationRoot();
                smsValidationHelper.bind();
                switchFragments(smsValidationRoot,visibleView!=null);
            } else if(viewState instanceof AuthViewModel.ViewState.AuthSuccess){
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
            }
        });

        ImageView logo = contentList.findViewById(R.id.logo);
        KeyboardVisibilityEvent.setEventListener(this, isOpen -> {
            if(isOpen) {
                recyclerView.setVisibility(View.GONE);
                new Handler(getMainLooper()).postDelayed(() ->
                        scaleViewHeight(48, logo),200);
            } else{
                recyclerView.setVisibility(View.VISIBLE);
                scaleViewHeight(150, logo);
            }
        });
    }

    private void scaleViewHeight(int toScaleDp, View view){
        float newHeight = dpToPx(this, toScaleDp);
        if(view.getHeight()!=newHeight) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
            ValueAnimator va = ValueAnimator.ofInt(layoutParams.height, (int) newHeight);
            va.setDuration(300);
            va.setInterpolator(new DecelerateInterpolator());
            va.addUpdateListener(animation -> {
                layoutParams.height = (int) animation.getAnimatedValue();
                view.setLayoutParams(layoutParams);
            });
            va.start();
        }
    }

    private void switchFragments(View show, boolean animate){
        if(show!=visibleView) {
            if (animate) {
                TransitionManager.beginDelayedTransition(contentList);
            }
            show.setVisibility(View.VISIBLE);
            if (visibleView != null) {
                visibleView.setVisibility(View.GONE);
            }
            visibleView = show;
        }
    }

    private class AuthHelper {

        private final ViewStub authRootVS;

        private View authRoot;
        private EditText loginEditText;
        private EditText passwordEditText;

        private AuthHelper(ViewStub authRootVS) {
            this.authRootVS = authRootVS;
        }

        public View getAuthRoot() {
            if(authRoot==null){
                initAuthFragment();
            }
            return authRoot;
        }

        private void initAuthFragment(){
            authRoot = authRootVS.inflate();

            loginEditText = authRoot.findViewById(R.id.loginField);
            loginEditText.addTextChangedListener(new SimpleTextWatcher() {
                @Override public void afterTextChanged(Editable editable) {
                    authViewModel.login.setValue(trimEditable(editable));
                }
            });

            passwordEditText = authRoot.findViewById(R.id.passField);
            passwordEditText.addTextChangedListener(new SimpleTextWatcher() {
                @Override public void afterTextChanged(Editable editable) {
                    authViewModel.password.setValue(trimEditable(editable));
                }
            });

            ExtendedMaterialButton authButton = authRoot.findViewById(R.id.loginButton);
            authButton.setOnClickListener(v -> authViewModel.tryAuth());
            authViewModel.password.observe(AuthActivity.this, s -> {
                authButton.setToggled(true);
                authButton.setEnabled(nonNullAndNonEmpty(s)&&
                        nonNullAndNonEmpty(authViewModel.login.getValue()));
            });
            authViewModel.login.observe(AuthActivity.this, s -> {
                authButton.setToggled(true);
                authButton.setEnabled(nonNullAndNonEmpty(s)&&
                        nonNullAndNonEmpty(authViewModel.password.getValue()));
            });
        }

        public void bind(){
            if(loginEditText!=null) {
                loginEditText.requestFocus();
            }
        }
        public void clear(){
            if(authRoot!=null) {
                loginEditText.clearFocus();
                passwordEditText.clearFocus();
            }
        }
    }

    private class CaptchaHelper {

        private final ViewStub captchaRootVS;

        private View captchaRoot;
        private EditText captchaEditText;
        private ImageView captchaImage;

        private CaptchaHelper(ViewStub captchaRootVS) {
            this.captchaRootVS = captchaRootVS;
        }

        public View getCaptchaRoot(){
            if(captchaRoot==null){
                initCaptchaFragment();
            }
            return captchaRoot;
        }

        private void initCaptchaFragment(){
            captchaRoot = captchaRootVS.inflate();

            captchaEditText = captchaRoot.findViewById(R.id.captchaKeyField);
            captchaEditText.addTextChangedListener(new SimpleTextWatcher() {
                @Override public void afterTextChanged(Editable editable) {
                    authViewModel.captchaKey.setValue(trimEditable(editable));
                }
            });

            ExtendedMaterialButton captchaSendButton = captchaRoot.findViewById(R.id.captchaSendButton);
            captchaSendButton.setOnClickListener(v -> authViewModel.tryAuth());
            authViewModel.captchaKey.observe(AuthActivity.this, s -> {
                captchaSendButton.setToggled(true);
                captchaSendButton.setEnabled(nonNullAndNonEmpty(s));
            });

            captchaImage = captchaRoot.findViewById(R.id.captchaImage);

            MaterialButton captchaCancelButton = captchaRoot.findViewById(R.id.captchaCancelButton);
            captchaCancelButton.setOnClickListener(v -> authViewModel.tryCancelCaptcha());
        }

        public void bind(String captchaImg){
            captchaEditText.requestFocus();
            Picasso.get().load(captchaImg).into(captchaImage);
        }

        public void clear(){
            if(captchaRoot!=null) {
                Picasso.get().cancelRequest(captchaImage);
                if(captchaImage.getDrawable()!=null) {
                    captchaImage.setImageBitmap(null);
                }
                captchaEditText.clearFocus();
                captchaEditText.setText("");
            }
        }
    }

    private class AppValidationHelper {

        private final ViewStub appValidationRootVS;

        private View appValidationRoot;
        private EditText appValidationEditText;

        private AppValidationHelper(ViewStub appValidationRootVS) {
            this.appValidationRootVS = appValidationRootVS;
        }

        public View getAppValidationRoot() {
            if(appValidationRoot==null){
                initAppValidationFragment();
            }
            return appValidationRoot;
        }

        private void initAppValidationFragment(){
            appValidationRoot = appValidationRootVS.inflate();

            appValidationEditText = appValidationRoot.findViewById(R.id.appValidationCodeField);
            appValidationEditText.addTextChangedListener(new SimpleTextWatcher() {
                @Override public void afterTextChanged(Editable editable) {
                    authViewModel.appValidationCode.setValue(trimEditable(editable));
                }
            });

            ExtendedMaterialButton appValidationSendButton = appValidationRoot.findViewById(R.id.appValidationSendButton);
            appValidationSendButton.setOnClickListener(v -> authViewModel.tryAuth());
            authViewModel.appValidationCode.observe(AuthActivity.this, s -> {
                appValidationSendButton.setToggled(true);
                appValidationSendButton.setEnabled(nonNullAndNonEmpty(s));
            });

            MaterialButton appValidationCancelButton = appValidationRoot.findViewById(R.id.appValidationCancelButton);
            appValidationCancelButton.setOnClickListener(v -> authViewModel.tryCancelValidation());

            ExtendedMaterialButton appValidationCodeForceButton = appValidationRoot.findViewById(R.id.appValidationCodeForceButton);
            appValidationCodeForceButton.setOnClickListener(v -> authViewModel.tryAuth(true));
        }

        public void bind(){
            appValidationEditText.requestFocus();
        }

        public void clear(){
            if(appValidationRoot!=null) {
                appValidationEditText.clearFocus();
                appValidationEditText.setText("");
            }
        }
    }

    private class SMSValidationHelper {

        private final ViewStub smsValidationRootVS;

        private View smsValidationRoot;
        private EditText smsValidationEditText;
        private TextView timerText;
        private ExtendedMaterialButton smsValidationCodeForceButton;

        private SMSValidationHelper(ViewStub smsValidationRootVS) {
            this.smsValidationRootVS = smsValidationRootVS;
        }

        public View getSMSValidationRoot() {
            if(smsValidationRoot==null){
                initSMSValidationFragment();
            }
            return smsValidationRoot;
        }

        private void initSMSValidationFragment(){
            smsValidationRoot = smsValidationRootVS.inflate();

            smsValidationEditText = smsValidationRoot.findViewById(R.id.smsValidationCodeField);
            smsValidationEditText.addTextChangedListener(new SimpleTextWatcher() {
                @Override public void afterTextChanged(Editable editable) {
                    authViewModel.smsValidationCode.setValue(trimEditable(editable));
                }
            });

            ExtendedMaterialButton smsValidationSendButton = smsValidationRoot.findViewById(R.id.smsValidationSendButton);
            smsValidationSendButton.setOnClickListener(v -> authViewModel.tryAuth());
            authViewModel.smsValidationCode.observe(AuthActivity.this, s -> {
                smsValidationSendButton.setToggled(true);
                smsValidationSendButton.setEnabled(nonNullAndNonEmpty(s));
            });

            MaterialButton smsValidationCancelButton = smsValidationRoot.findViewById(R.id.smsValidationCancelButton);
            smsValidationCancelButton.setOnClickListener(v -> authViewModel.tryCancelValidation());

            timerText = smsValidationRoot.findViewById(R.id.smsValidationCodeForceTimer);
            smsValidationCodeForceButton = smsValidationRoot.findViewById(R.id.smsValidationCodeForceButton);
            authViewModel.smsRequestDelay.observe(AuthActivity.this, delay -> {
                if(delay>0){
                    if(timerText.getVisibility()!=View.VISIBLE){
                        timerText.setVisibility(View.VISIBLE);
                    }
                    timerText.setText(TimeUtil.getDurationStringMills(delay));
                    if(smsValidationCodeForceButton.isEnabled()) {
                        smsValidationCodeForceButton.setToggled(true);
                        smsValidationCodeForceButton.setEnabled(false);
                    }
                } else {
                    if(timerText.getVisibility()!=View.GONE){
                        timerText.setVisibility(View.GONE);
                    }
                    if(!smsValidationCodeForceButton.isEnabled()) {
                        smsValidationCodeForceButton.setToggled(true);
                        smsValidationCodeForceButton.setEnabled(true);
                    }
                }
            });
            smsValidationCodeForceButton.setOnClickListener(v -> authViewModel.tryValidatePhone());
        }

        public void bind(){
            smsValidationEditText.requestFocus();
        }

        public void clear(){
            if(smsValidationRoot!=null){
                if (timerText.getVisibility() != View.GONE) {
                    timerText.setVisibility(View.GONE);
                }
                if(smsValidationCodeForceButton.isEnabled()) {
                    smsValidationCodeForceButton.setEnabled(false);
                }
                smsValidationEditText.clearFocus();
                smsValidationEditText.setText("");
            }
        }
    }

}
