package com.vkontakte.miracle.view.behavior;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class ViewMarginBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {

    private ViewMarginHelper viewMarginHelper;

    private int tempTopOffset = 0;
    private int tempBottomOffset = 0;
    private int tempLeftOffset = 0;
    private int tempRightOffset = 0;

    public ViewMarginBehavior() {}

    public ViewMarginBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onLayoutChild(
            @NonNull CoordinatorLayout parent, @NonNull V child, int layoutDirection) {

        if (viewMarginHelper == null) {
            viewMarginHelper = new ViewMarginHelper(child);
        }

        if(tempTopOffset != 0){
            viewMarginHelper.setTopOffset(tempTopOffset);
            tempTopOffset = 0;
        }

        if(tempBottomOffset != 0){
            viewMarginHelper.setBottomOffset(tempBottomOffset);
            tempBottomOffset = 0;
        }

        if(tempLeftOffset != 0){
            viewMarginHelper.setLeftOffset(tempLeftOffset);
            tempLeftOffset = 0;
        }

        if(tempRightOffset != 0){
            viewMarginHelper.setRightOffset(tempRightOffset);
            tempRightOffset = 0;
        }

        parent.onLayoutChild(child, layoutDirection);

        return true;
    }

    public boolean setTopOffset(int offset) {
        if (viewMarginHelper != null) {
            return viewMarginHelper.setTopOffset(offset);
        } else {
            tempTopOffset = offset;
        }
        return false;
    }

    public boolean setBottomOffset(int offset) {
        if (viewMarginHelper != null) {
            return viewMarginHelper.setBottomOffset(offset);
        } else {
            tempBottomOffset = offset;
        }
        return false;
    }

    public boolean setLeftOffset(int offset) {
        if (viewMarginHelper != null) {
            return viewMarginHelper.setLeftOffset(offset);
        } else {
            tempLeftOffset = offset;
        }
        return false;
    }

    public boolean setRightOffset(int offset) {
        if (viewMarginHelper != null) {
            return viewMarginHelper.setRightOffset(offset);
        } else {
            tempRightOffset = offset;
        }
        return false;
    }

    public int getTopOffset() {
        return viewMarginHelper != null ? viewMarginHelper.getOffsetTop() : tempTopOffset;
    }

    public int getBottomOffset() {
        return viewMarginHelper != null ? viewMarginHelper.getOffsetBottom() : tempBottomOffset;
    }

    public int getLeftOffset() {
        return viewMarginHelper != null ? viewMarginHelper.getOffsetLeft() : tempLeftOffset;
    }

    public int getRightOffset() {
        return viewMarginHelper != null ? viewMarginHelper.getOffsetRight() : tempRightOffset;
    }

    public void setVerticalOffsetEnabled(boolean verticalOffsetEnabled) {
        if (viewMarginHelper != null) {
            viewMarginHelper.setVerticalOffsetEnabled(verticalOffsetEnabled);
        }
    }

    public boolean isVerticalOffsetEnabled() {
        return viewMarginHelper != null && viewMarginHelper.isVerticalOffsetEnabled();
    }

    public void setHorizontalOffsetEnabled(boolean horizontalOffsetEnabled) {
        if (viewMarginHelper != null) {
            viewMarginHelper.setHorizontalOffsetEnabled(horizontalOffsetEnabled);
        }
    }

    public boolean isHorizontalOffsetEnabled() {
        return viewMarginHelper != null && viewMarginHelper.isHorizontalOffsetEnabled();
    }

}
