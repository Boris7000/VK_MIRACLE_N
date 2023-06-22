package com.vkontakte.miracle.view.virtual;

import static android.view.View.VISIBLE;
import static com.miracle.widget.Utils.extractTintInfo;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.miracle.widget.TintInfo;

public class VirtualOnlineIcon extends VirtualView{

    public enum State {
        STATE_OFFLINE,
        STATE_ONLINE,
        STATE_MOBILE
    }

    private State state = State.STATE_OFFLINE;

    private Drawable mobileIcon;
    private Drawable mobileIconSubtract;
    private Drawable onlineIcon;
    private Drawable onlineIconSubtract;
    private TintInfo iconTint;
    private TintInfo iconSubtractTint;
    private int imageSize;

    public VirtualOnlineIcon(View parent, TypedArray a, int sizeId, int iconTintId, int iconSubtractTintId, int iconTintModeId,
                             int mobileIconId, int mobileIconSubtractId, int onlineIconId, int onlineIconSubtractId) {
        super(parent);
        if(sizeId!=0) {
            imageSize = a.getDimensionPixelSize(sizeId, 0);
        }
        if(iconTintId!=0){
            iconTint = extractTintInfo(a, iconTintId, iconTintModeId, null);
        }
        if(iconSubtractTintId!=0){
            iconSubtractTint = extractTintInfo(a, iconSubtractTintId, iconTintModeId, null);
        }
        if(mobileIconId!=0){
            mobileIcon = a.getDrawable(mobileIconId);
        }
        if(mobileIconSubtractId!=0){
            mobileIconSubtract = a.getDrawable(mobileIconSubtractId);
        }
        if(onlineIconId!=0){
            onlineIcon = a.getDrawable(onlineIconId);
        }
        if(onlineIconSubtractId!=0){
            onlineIconSubtract = a.getDrawable(onlineIconSubtractId);
        }
        applyTintForIcon();
    }

    private void applyTintForDrawable(Drawable drawable, TintInfo tint){
        if(tint!=null&&(tint.mHasTintList||tint.mHasTintMode)) {
            if(drawable!=null){
                drawable.mutate();

                if (tint.mHasTintList) {
                    drawable.setTintList(tint.mTintList);
                } else {
                    drawable.setTintList(null);
                }

                if (tint.mHasTintMode) {
                    drawable.setTintMode(tint.mTintMode);
                }

                if (drawable.isStateful()) {
                    drawable.setState(getParent().getDrawableState());
                }
            }
        } else {
            if(drawable!=null) {
                drawable.mutate();
                drawable.setTintList(null);
                if (drawable.isStateful()) {
                    drawable.setState(getParent().getDrawableState());
                }
            }
        }
    }

    private void applyTintForIcon(){
        applyTintForDrawable(onlineIcon, iconTint);
        applyTintForDrawable(mobileIcon, iconTint);
        applyTintForDrawable(onlineIconSubtract, iconSubtractTint);
        applyTintForDrawable(mobileIconSubtract, iconSubtractTint);
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public int getRawContentWidth() {
        return imageSize;
    }

    @Override
    public int getRawContentHeight() {
        return imageSize;
    }

    public void jumpDrawablesToCurrentState() {
        if (onlineIcon != null && onlineIcon.isStateful()){
            onlineIcon.jumpToCurrentState();
        }
        if (mobileIcon != null && mobileIcon.isStateful()){
            mobileIcon.jumpToCurrentState();
        }
        if (onlineIconSubtract != null && onlineIconSubtract.isStateful()){
            onlineIconSubtract.jumpToCurrentState();
        }
        if (mobileIconSubtract != null && mobileIconSubtract.isStateful()){
            mobileIconSubtract.jumpToCurrentState();
        }
    }

    public void drawableStateChanged(int[] drawableState) {
        if (onlineIcon != null && onlineIcon.isStateful() && onlineIcon.setState(drawableState)) {
            getParent().invalidateDrawable(onlineIcon);
        }
        if (mobileIcon != null && mobileIcon.isStateful() && mobileIcon.setState(drawableState)) {
            getParent().invalidateDrawable(mobileIcon);
        }
        if (onlineIconSubtract != null && onlineIconSubtract.isStateful() && onlineIconSubtract.setState(drawableState)) {
            getParent().invalidateDrawable(onlineIconSubtract);
        }
        if (mobileIconSubtract != null && mobileIconSubtract.isStateful() && mobileIconSubtract.setState(drawableState)) {
            getParent().invalidateDrawable(mobileIconSubtract);
        }
    }

    public void drawOnCanvas(Canvas canvas){
        if(getVisibility()==VISIBLE) {
            switch (state){
                case STATE_ONLINE:{
                    VirtualLayoutParams layoutParams = getLayoutParams();
                    onlineIconSubtract.setBounds(layoutParams.left, layoutParams.top, layoutParams.right, layoutParams.bottom);
                    onlineIconSubtract.draw(canvas);
                    onlineIcon.setBounds(layoutParams.left, layoutParams.top, layoutParams.right, layoutParams.bottom);
                    onlineIcon.draw(canvas);
                    break;
                }
                case STATE_MOBILE:{
                    VirtualLayoutParams layoutParams = getLayoutParams();
                    mobileIconSubtract.setBounds(layoutParams.left, layoutParams.top, layoutParams.right, layoutParams.bottom);
                    mobileIconSubtract.draw(canvas);
                    mobileIcon.setBounds(layoutParams.left, layoutParams.top, layoutParams.right, layoutParams.bottom);
                    mobileIcon.draw(canvas);
                    break;
                }
            }
        }
    }
}
