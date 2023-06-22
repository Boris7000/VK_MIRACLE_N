package com.vkontakte.miracle.activity.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.material.navigation.NavigationBarView;
import com.miracle.engine.activity.tabs.TabsActivity;
import com.miracle.engine.activity.tabs.TabsActivityController;
import com.miracle.engine.fragment.FragmentFabric;
import com.miracle.engine.preferences.ThemePreferences;
import com.miracle.engine.storage.LargeDataStorage;
import com.miracle.engine.transition.PositionTransition;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.fragment.feed.FragmentFeed;
import com.vkontakte.miracle.fragment.menu.FragmentMenu;
import com.vkontakte.miracle.fragment.messages.FragmentConversations;
import com.vkontakte.miracle.fragment.photos.PhotoViewerFragment;
import com.vkontakte.miracle.view.behavior.DodgeBottomNavigationBehavior;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

public class MainActivity extends TabsActivity {

    private int circularRevealCenterX;
    private int circularRevealCenterY;
    private String circularRevealScreenshotKey;
    private Bitmap circularRevealScreenshot;

    private PhotoViewerFragment photoViewerFragment;

    private WindowInsetsCompat insetsCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(ThemePreferences.get().themeResourceId());
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        ArrayMap<Integer, FragmentFabric> fragmentFabrics = new ArrayMap<>();
        fragmentFabrics.put(R.id.tab_user, new FragmentMenu.Fabric());
        fragmentFabrics.put(R.id.tab_feed, new FragmentFeed.Fabric());
        fragmentFabrics.put(R.id.tab_dialogs, new FragmentConversations.Fabric());
        fragmentFabrics.put(R.id.tab_music, new FragmentMenu.Fabric());

        NavigationBarView navigationBarView = findViewById(R.id.bottomNavigationView);

        TabsActivityController tabsActivityController = getTabsController();
        tabsActivityController.restoreState(savedInstanceState);
        tabsActivityController.setFragmentsFabrics(fragmentFabrics);
        tabsActivityController.setOnSelfTabChangeListener(new TabsActivityController.OnSelfTabChangeListener() {
            @Override
            public void onChange(int tabId) {
                Log.d("eijdiejdied", "self from "+navigationBarView.getSelectedItemId());
                Log.d("eijdiejdied", "self to "+tabId);
                navigationBarView.setSelectedItemId(tabId);
            }
        });
        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                tabsActivityController.selectTab(item.getItemId());
                Log.d("eijdiejdied", "user to "+item.getItemId());
                return true;
            }
        });
        navigationBarView.setOnItemReselectedListener(new NavigationBarView.OnItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                Log.d("eijdiejdied", "reselected "+item.getItemId());
            }
        });

        if(savedInstanceState==null) {
            navigationBarView.setSelectedItemId(R.id.tab_feed);
        }

        KeyboardVisibilityEvent.setEventListener(this, isOpen -> {
            if(isOpen) {
                navigationBarView.setVisibility(View.GONE);
                FrameLayout frameLayout = findViewById(R.id.fragmentContainer);
                ViewGroup.LayoutParams layoutParams = frameLayout.getLayoutParams();
                if(layoutParams instanceof CoordinatorLayout.LayoutParams){
                    CoordinatorLayout.LayoutParams coordinatorLayoutParams = (CoordinatorLayout.LayoutParams)layoutParams;
                    CoordinatorLayout.Behavior<View> behavior = coordinatorLayoutParams.getBehavior();
                    if(behavior instanceof DodgeBottomNavigationBehavior){
                        if(insetsCompat!=null){
                            behavior.onApplyWindowInsets((CoordinatorLayout) frameLayout.getParent(),frameLayout,insetsCompat);
                        }
                    }
                }
            } else{
                navigationBarView.setVisibility(View.VISIBLE);
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(getWindow().getDecorView(), (v, windowInsets) -> {
            insetsCompat = windowInsets;
            return windowInsets;
        });


    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        circularRevealScreenshotKey = savedInstanceState.getString("circularRevealScreenshotKey");
        if(circularRevealScreenshotKey!=null){
            circularRevealScreenshot = (Bitmap) LargeDataStorage.get().extractLargeData(circularRevealScreenshotKey);
            circularRevealCenterX = savedInstanceState.getInt("circularRevealCenterX", 0);
            circularRevealCenterY = savedInstanceState.getInt("circularRevealCenterY", 0);
            savedInstanceState.remove("circularRevealScreenshotKey");
            savedInstanceState.remove("circularRevealCenterX");
            savedInstanceState.remove("circularRevealCenterY");

            if(circularRevealScreenshot!=null){
                ImageView circularRevealImage = findViewById(R.id.circularRevealImage);
                circularRevealImage.setVisibility(View.VISIBLE);
                circularRevealImage.setImageBitmap(circularRevealScreenshot);

                if(circularRevealImage.isAttachedToWindow()){
                    startCircularAnimation(circularRevealImage, circularRevealCenterX, circularRevealCenterY);
                    circularRevealScreenshot = null;
                } else {
                    ViewTreeObserver viewTreeObserver = getWindow().getDecorView().getViewTreeObserver();
                    viewTreeObserver.addOnWindowAttachListener(new ViewTreeObserver.OnWindowAttachListener() {
                        @Override
                        public void onWindowAttached() {
                            getWindow().getDecorView().getViewTreeObserver().removeOnWindowAttachListener(this);
                            startCircularAnimation(circularRevealImage, circularRevealCenterX, circularRevealCenterY);
                            circularRevealScreenshot = null;
                        }
                        @Override
                        public void onWindowDetached() {}
                    });
                }
            }
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.getFragment(savedInstanceState, "photoViewerFragment");
        if(fragment instanceof PhotoViewerFragment){

        }
    }

    //--------------------------------------------------------------------------------------//

    public void prepareToCircularAnimation(String bitmapKey, int centerX, int centerY){
        circularRevealScreenshotKey = bitmapKey;
        circularRevealCenterX = centerX;
        circularRevealCenterY = centerY;
    }

    private void startCircularAnimation(ImageView imageView, int centerX, int centerY){
        View rootView = getWindow().getDecorView().getRootView();

        int dX = Math.max(rootView.getWidth()-centerX, centerX);
        int dY = Math.max(rootView.getHeight()-centerY, centerX);

        int radius = (int) Math.sqrt(Math.pow(dX,2)+Math.pow(dY,2));

        Animator anim = ViewAnimationUtils.createCircularReveal(
                imageView, centerX, centerY, radius, 0f);
        anim.setDuration(500);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());

        Animator.AnimatorListener animationListener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                imageView.setImageDrawable(null);
                imageView.setVisibility(View.GONE);
            }
        };
        anim.addListener(animationListener);
        anim.start();
    }

    //--------------------------------------------------------------------------------------//

    public void openPhotoViewerFragment(View sharedElement){
        photoViewerFragment = new PhotoViewerFragment();

        if(sharedElement.getId()==View.NO_ID) {
            sharedElement.setId(R.id.captchaRoot);
        }

        ViewGroup sceneRoot = findViewById(R.id.root);

        getSupportFragmentManager()
                .beginTransaction()
                .addSharedElement(sharedElement, "TestPhoto")
                .add(R.id.root, photoViewerFragment)
                .addToBackStack("PhotoViewerFragment::Main")
                .commit();


        int[] startLocation = new int[2];
        sharedElement.getLocationInWindow(startLocation);

        PointF startPosition = new PointF(startLocation[0], startLocation[1]);

        Log.d("eijfiejfie","start x:"+startPosition.x+" y:"+startPosition.y);

        photoViewerFragment.getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onResume(@NonNull LifecycleOwner owner) {
                View root = photoViewerFragment.getView();
                if(root!=null) {
                    View testPhoto = root.findViewById(R.id.testPhoto);

                    int[] endLocation = new int[2];
                    testPhoto.getLocationInWindow(endLocation);

                    PointF endPosition = new PointF(endLocation[0], endLocation[1]);
                    Log.d("eijfiejfie","end x:"+endPosition.x+" y:"+endPosition.y);

                    ValueAnimator animator = ValueAnimator.ofObject(new PositionTransition.PointFEvaluator(), startPosition, endPosition);
                    animator.addUpdateListener(valueAnimator -> {
                        PointF value = (PointF) valueAnimator.getAnimatedValue();
                        testPhoto.setTranslationX(value.x);
                        testPhoto.setTranslationY(value.y);
                    });
                    animator.setDuration(600);
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            testPhoto.setX(endPosition.x);
                            testPhoto.setY(endPosition.y);
                        }
                    });
                    animator.start();
                    Log.d("eijfiejfie","started");
                }

            }
        });



        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getSupportFragmentManager().popBackStack();
                remove();
            }
        });


    }

    //--------------------------------------------------------------------------------------//

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(circularRevealScreenshotKey!=null) {
            outState.putString("circularRevealScreenshotKey",circularRevealScreenshotKey);
            outState.putInt("circularRevealCenterX", circularRevealCenterX);
            outState.putInt("circularRevealCenterY", circularRevealCenterY);
        }
        if(photoViewerFragment!=null){
            getSupportFragmentManager().putFragment(outState, "photoViewerFragment", photoViewerFragment);
        }
    }

    @Override
    public void finish() {
        if(circularRevealScreenshot==null&&circularRevealScreenshotKey!=null){
            LargeDataStorage.get().removeLargeData(circularRevealScreenshotKey);
        }
        super.finish();
    }

    @Override
    public void recreate() {
        super.recreate();
        Log.d("ijeifjiefe","recreate-------------------------------------------------");
    }
}