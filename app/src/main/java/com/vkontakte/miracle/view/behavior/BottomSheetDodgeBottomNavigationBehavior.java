package com.vkontakte.miracle.view.behavior;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomSheetDodgeBottomNavigationBehavior<V extends View> extends LockableSheetBehavior<V> {

    private int bottomOffset;

    public BottomSheetDodgeBottomNavigationBehavior() {}

    public BottomSheetDodgeBottomNavigationBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        return dependency instanceof BottomNavigationView;
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        int peek = getPeekHeight();
        int newOffset = (child.getTop()+peek)-dependency.getTop();
        if(newOffset!=bottomOffset){
            bottomOffset = newOffset;
            setPeekHeight(peek);
            return true;
        }
        return false;
    }

    public int getClearPeekHeight() {
        return super.getPeekHeight()-bottomOffset;
    }

    public void setRawPeekHeight(int peekHeight) {
        super.setPeekHeight(peekHeight);
    }

    @Override
    public void setPeekHeight(int peekHeight) {
        super.setPeekHeight(peekHeight+bottomOffset);
    }
}
