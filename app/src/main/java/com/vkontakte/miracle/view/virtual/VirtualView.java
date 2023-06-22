package com.vkontakte.miracle.view.virtual;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.view.View;

public abstract class VirtualView {

    private final View parent;

    public VirtualView(View parent) {
        this.parent = parent;
    }

    private int visibility = VISIBLE;

    private final VirtualLayoutParams layoutParams = new VirtualLayoutParams();

    public View getParent() {
        return parent;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public VirtualLayoutParams getLayoutParams() {
        return layoutParams;
    }

    public int getContentWidth(){
        if(getVisibility()==GONE) {
            return 0;
        } else {
            return getRawContentWidth();
        }
    }

    public int getContentHeight(){
        if(getVisibility()==GONE) {
            return 0;
        } else {
            return getRawContentHeight();
        }
    }

    public abstract int getRawContentWidth();

    public abstract int getRawContentHeight();

    public int getWidth(){
        if(visibility==GONE) {
            return 0;
        } else {
            return getRawWidth();
        }
    }

    public int getHeight(){
        if(visibility==GONE) {
            return 0;
        } else {
            return getRawHeight();
        }
    }

    public int getRawWidth(){
        return layoutParams.right-layoutParams.left;
    }

    public int getRawHeight(){
        return layoutParams.bottom-layoutParams.top;
    }
}
