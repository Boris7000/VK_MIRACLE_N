package com.vkontakte.miracle.fragment.menu;

import static android.view.View.VISIBLE;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;
import static com.miracle.engine.util.ViewUtil.takeScreenshot;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.miracle.engine.fragment.FragmentFabric;
import com.miracle.engine.fragment.MiracleFragment;
import com.miracle.engine.preferences.ThemePreferences;
import com.miracle.engine.storage.LargeDataStorage;
import com.miracle.engine.util.BitmapUtil;
import com.miracle.widget.ExtendedMaterialButton;
import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.activity.auth.AuthActivity;
import com.vkontakte.miracle.activity.main.MainActivity;
import com.vkontakte.miracle.memory.storage.UsersStorage;
import com.vkontakte.miracle.model.auth.User;
import com.vkontakte.miracle.model.users.fileds.LastSeen;
import com.vkontakte.miracle.util.SwipeRefreshUtil;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FragmentMenu extends MiracleFragment {

    private Disposable screenshotDisposable;
    private MenuViewModel menuViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menuViewModel = new ViewModelProvider(this).get(MenuViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.menu_fragment, container, false);
        LinearLayout linearLayout = rootView.findViewById(R.id.linearLayout);
        inflater.inflate(R.layout.menu_fragment_content, linearLayout, true);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindAppbar(view);

        bindContent(view);

        bindSwitches(view);

        bindButtons(view);

    }

    private void bindAppbar(View view){
        AppBarLayout appBarLayout = view.findViewById(R.id.appbarLayout);
        Toolbar toolbar = appBarLayout.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.profile);
    }

    private void bindContent(View view){
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.refreshLayout);
        SwipeRefreshUtil.applyDefaultStyle(swipeRefreshLayout,requireContext());
        menuViewModel.loadingType.observe(requireActivity(), loadingType -> {
            swipeRefreshLayout.setEnabled(loadingType==MenuViewModel.LoadingType.REFRESHING||
                    loadingType==MenuViewModel.LoadingType.NO_LOADING);
            swipeRefreshLayout.setRefreshing(loadingType==MenuViewModel.LoadingType.REFRESHING);});
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (!menuViewModel.tryRefresh()) swipeRefreshLayout.setRefreshing(false);});

        FrameLayout frameLayout = view.findViewById(R.id.profileLink);
        TextView username = frameLayout.findViewById(R.id.current_user_name);
        ImageView avatar = frameLayout.findViewById(R.id.photo);
        ImageView onlineStatus = frameLayout.findViewById(R.id.onlineStatus);

        menuViewModel.viewState.observe(requireActivity(), viewState -> {
            if(viewState instanceof MenuViewModel.ViewState.Loading){
                //TODO возможно стоит добавить индикатор
            } else if(viewState instanceof MenuViewModel.ViewState.Success){
                User user = ((MenuViewModel.ViewState.Success)viewState).getUser();
                username.setText(user.getFullName());

                UsersStorage usersStorage = UsersStorage.get();
                Picasso.get().cancelRequest(avatar);
                Bitmap photo = usersStorage.loadBitmapForCurrentUser("userImage200.png");
                if(photo==null){
                    Picasso.get().load(user.getPhoto200()).into(avatar);
                } else {
                    avatar.setImageBitmap(photo);
                }

                if (user.isOnline()) {
                    if (onlineStatus.getVisibility() != VISIBLE) {
                        onlineStatus.setVisibility(VISIBLE);
                    }
                    LastSeen lastSeen = user.getLastSeen();
                    onlineStatus.setImageResource(lastSeen.getPlatform() == 7 ?
                            R.drawable.ic_online_16 : R.drawable.ic_online_mobile_16);
                    onlineStatus.setBackgroundResource(lastSeen.getPlatform() == 7 ?
                            R.drawable.ic_online_subtract_16 : R.drawable.ic_online_mobile_subtract_16);
                } else {
                    onlineStatus.setVisibility(View.GONE);
                }
            } else if(viewState instanceof MenuViewModel.ViewState.Error){
                Snackbar.make(view, ((MenuViewModel.ViewState.Error) viewState).getErrorMessage(),
                        Snackbar.LENGTH_SHORT).show();
            } else if(viewState instanceof MenuViewModel.ViewState.Exit){
                Activity activity = getActivity();
                if(activity!=null){
                    Intent intent = new Intent(activity, AuthActivity.class);
                    startActivity(intent);
                    activity.finish();
                    activity.overridePendingTransition(R.anim.slide_in_reverse,R.anim.slide_out_reverse);
                }
            }
        });
    }

    private void bindSwitches(View view){
        ExtendedMaterialButton nightModeSwitchButton = view.findViewById(R.id.darkModeSwitch);
        ThemePreferences themePreferences = ThemePreferences.get();
        nightModeSwitchButton.setChecked(themePreferences.nightMode()==MODE_NIGHT_YES);
        nightModeSwitchButton.addOnCheckedChangeListener((button, isChecked) -> {
            button.setClickable(false);
            int newNightMode = isChecked?MODE_NIGHT_YES:MODE_NIGHT_NO;
            themePreferences.storeNightMode(newNightMode);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Activity activity = getActivity();
                if(activity!=null){
                    if(activity instanceof MainActivity){
                        MainActivity mainActivity = (MainActivity) activity;
                        View rootView = activity.getWindow().getDecorView().getRootView();
                        final Bitmap screenShot = takeScreenshot(rootView);
                        final int[] location = new int[2];
                        button.getLocationInWindow(location);
                        final int rawX = location[0]+button.getWidth()/2;
                        final int rawY = location[1]+button.getHeight()/2;
                        screenshotDisposable = Single.just(BitmapUtil.compressBitmap(screenShot,0.7f))
                                .subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread())
                                .subscribe(compressed -> {
                                    String key = LargeDataStorage.get().storeLargeData(compressed);
                                    mainActivity.prepareToCircularAnimation(key, rawX, rawY);
                                    AppCompatDelegate.setDefaultNightMode(newNightMode);
                                }, throwable -> AppCompatDelegate.setDefaultNightMode(newNightMode));
                    } else {
                        AppCompatDelegate.setDefaultNightMode(newNightMode);
                    }
                }
            }, 300);
        });
    }

    private void bindButtons(View view){
        MaterialButton exitButton = view.findViewById(R.id.exitButton);
        exitButton.setOnClickListener(v -> menuViewModel.tryExit());
    }

    public static class Fabric implements FragmentFabric {
        @Override
        public Fragment create() {
            return new FragmentMenu();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(screenshotDisposable!=null&&!screenshotDisposable.isDisposed()){
            screenshotDisposable.dispose();
        }
    }
}
