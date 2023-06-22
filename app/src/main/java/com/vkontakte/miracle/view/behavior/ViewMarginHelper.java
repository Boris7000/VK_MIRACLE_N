package com.vkontakte.miracle.view.behavior;

import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class ViewMarginHelper {

    private final View view;

    private int offsetTop;
    private int offsetBottom;
    private int offsetLeft;
    private int offsetRight;

    private boolean verticalOffsetEnabled = true;
    private boolean horizontalOffsetEnabled = true;

    public ViewMarginHelper(View view) {
        this.view = view;
    }

    void applyOffsets() {
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
        layoutParams.bottomMargin = offsetBottom;
        layoutParams.topMargin = offsetTop;
        layoutParams.rightMargin = offsetRight;
        layoutParams.leftMargin = offsetLeft;
        view.requestLayout();
    }

    public boolean setTopOffset(int offset) {
        if (verticalOffsetEnabled && offsetTop != offset) {
            offsetTop = offset;
            applyOffsets();
            return true;
        }
        return false;
    }

    public boolean setBottomOffset(int offset) {
        if (verticalOffsetEnabled && offsetBottom != offset) {
            offsetBottom = offset;
            applyOffsets();
            return true;
        }
        return false;
    }

    public boolean setLeftOffset(int offset) {
        if (horizontalOffsetEnabled && offsetLeft != offset) {
            offsetLeft = offset;
            applyOffsets();
            return true;
        }
        return false;
    }

    public boolean setRightOffset(int offset) {
        if (horizontalOffsetEnabled && offsetRight != offset) {
            offsetRight = offset;
            applyOffsets();
            return true;
        }
        return false;
    }

    public int getOffsetTop() {
        return offsetTop;
    }

    public int getOffsetBottom() {
        return offsetBottom;
    }

    public int getOffsetLeft() {
        return offsetLeft;
    }

    public int getOffsetRight() {
        return offsetRight;
    }

    public void setVerticalOffsetEnabled(boolean verticalOffsetEnabled) {
        this.verticalOffsetEnabled = verticalOffsetEnabled;
    }

    public void setHorizontalOffsetEnabled(boolean horizontalOffsetEnabled) {
        this.horizontalOffsetEnabled = horizontalOffsetEnabled;
    }

    public boolean isVerticalOffsetEnabled() {
        return verticalOffsetEnabled;
    }

    public boolean isHorizontalOffsetEnabled() {
        return horizontalOffsetEnabled;
    }
}
