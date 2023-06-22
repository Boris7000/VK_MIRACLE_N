package com.vkontakte.miracle.view.behavior;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsCompat;
import androidx.customview.view.AbsSavedState;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.List;

public class DodgeBottomNavigationBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {

    private int bottomOffset = 0;

    public DodgeBottomNavigationBehavior() {}

    public DodgeBottomNavigationBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @NonNull
    @Override
    public WindowInsetsCompat onApplyWindowInsets(@NonNull CoordinatorLayout parent, @NonNull V child, @NonNull WindowInsetsCompat insetsCompat) {

        List<View> dependencies = parent.getDependencies(child);
        if(dependencies.isEmpty()){
            Insets insets = insetsCompat.getInsets(WindowInsetsCompat.Type.systemBars() |
                    WindowInsetsCompat.Type.ime());
            if(insets.bottom!=bottomOffset){
                bottomOffset = insets.bottom;
                applyOffset(child);
            }

        }
        return super.onApplyWindowInsets(parent, child, insetsCompat);
    }

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull V child, @NonNull View dependency) {
        if(dependency.getVisibility()!=View.GONE) {
            if (dependency instanceof BottomNavigationView) {
                return true;
            } else {
                ViewGroup.LayoutParams params = dependency.getLayoutParams();
                if (params instanceof CoordinatorLayout.LayoutParams) {
                    CoordinatorLayout.Behavior<?> behavior = ((CoordinatorLayout.LayoutParams) params).getBehavior();
                    return behavior instanceof BottomSheetBehavior;
                }
            }
        }
        return false;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull V child, @NonNull View dependency) {
        List<View> dependencies = parent.getDependencies(child);
        int maxBottomOffset = 0;
        for (View dep : dependencies) {
            if(dep.getVisibility()!=View.GONE) {
                if (dep instanceof BottomNavigationView) {
                    int newOffset = parent.getBottom() - dep.getTop();
                    maxBottomOffset = Math.max(maxBottomOffset, newOffset);
                } else {
                    ViewGroup.LayoutParams params = dep.getLayoutParams();
                    if (params instanceof CoordinatorLayout.LayoutParams) {
                        CoordinatorLayout.Behavior<?> behavior = ((CoordinatorLayout.LayoutParams) params).getBehavior();
                        if (behavior instanceof BottomSheetBehavior) {
                            BottomSheetBehavior<?> bottomSheetBehavior = (BottomSheetBehavior<?>) behavior;
                            int newOffset = bottomSheetBehavior.getPeekHeight();
                            maxBottomOffset = Math.max(maxBottomOffset, newOffset);
                        }
                    }
                }
            }
        }
        Log.d("iwjfijwief", "maxBottomOffset "+maxBottomOffset);

        if(maxBottomOffset!=bottomOffset){
            bottomOffset = maxBottomOffset;
            return applyOffset(child);
        }

        return false;
    }



    private boolean applyOffset(V child){
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        if(layoutParams.bottomMargin!=bottomOffset) {
            layoutParams.bottomMargin = bottomOffset;
            child.requestLayout();
            return true;
        }
        return false;
    }

    @Override
    public boolean onMeasureChild(@NonNull CoordinatorLayout parent, @NonNull V child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        int heightSize = View.MeasureSpec.getSize(parentHeightMeasureSpec)-bottomOffset;
        int newHeightMeasureSpec =  View.MeasureSpec.makeMeasureSpec(heightSize, View.MeasureSpec.EXACTLY);
        child.measure(parentWidthMeasureSpec,newHeightMeasureSpec);
        return true;
    }

    @Override
    public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull V child, int layoutDirection) {
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        layoutParams.bottomMargin = bottomOffset;
        parent.onLayoutChild(child, layoutDirection);
        return true;
    }

    @Override
    public void onRestoreInstanceState(@NonNull CoordinatorLayout parent, @NonNull V child, @NonNull Parcelable state) {
        if(state instanceof SavedState){
            bottomOffset = ((SavedState) state).bottomOffset;
            applyOffset(child);
        }
    }

    @Nullable
    @Override
    public Parcelable onSaveInstanceState(@NonNull CoordinatorLayout parent, @NonNull V child) {
        Parcelable savedState = super.onSaveInstanceState(parent, child);
        if (bottomOffset!=0){
            SavedState ss = new SavedState(savedState!=null?savedState: AbsSavedState.EMPTY_STATE);
            ss.bottomOffset = bottomOffset;
            savedState = ss;
        }
        return savedState;
    }

    protected static class SavedState extends AbsSavedState {

        int bottomOffset;

        protected SavedState(@NonNull Parcel source, @Nullable ClassLoader loader) {
            super(source, loader);
            bottomOffset = source.readInt();
        }

        protected SavedState(@NonNull Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(bottomOffset);
        }

        public static final Creator<SavedState> CREATOR =
                new ClassLoaderCreator<SavedState>() {
                    @NonNull
                    @Override
                    public SavedState createFromParcel(@NonNull Parcel source, ClassLoader loader) {
                        return new SavedState(source, loader);
                    }

                    @NonNull
                    @Override
                    public SavedState createFromParcel(@NonNull Parcel source) {
                        return new SavedState(source, null);
                    }

                    @NonNull
                    @Override
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}



