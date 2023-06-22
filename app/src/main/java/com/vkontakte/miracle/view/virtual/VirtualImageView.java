package com.vkontakte.miracle.view.virtual;

import static android.view.View.VISIBLE;
import static com.miracle.widget.Utils.extractTintInfo;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.miracle.widget.TintInfo;

public class VirtualImageView extends VirtualView {

    private Drawable drawable;
    private TintInfo tint;
    private int imageWidth;
    private int imageHeight;
    private boolean shouldStartAnimation = false;

    public VirtualImageView(View parent, TypedArray a, int widthId, int heightId, int drawableId, int tintId, int tintModeId) {
        super(parent);
        if(widthId!=0) {
            imageWidth = a.getDimensionPixelSize(widthId, 0);
        }
        if(widthId!=0) {
            imageHeight = a.getDimensionPixelSize(heightId, 0);
        }
        if(tintId!=0){
            tint = extractTintInfo(a, tintId, tintModeId, null);
        }
        if(drawableId!=0) {
            setDrawable(a.getDrawable(drawableId));
        }
    }

    private void applyTintForDrawable(){
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

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        Drawable oldDrawable = this.drawable;
        if (oldDrawable != drawable) {
            if (oldDrawable != null) {
                oldDrawable.setCallback(null);
                getParent().unscheduleDrawable(oldDrawable);
            }

            this.drawable = drawable;

            if (drawable != null) {
                drawable.setCallback(getParent());
                drawable.setLayoutDirection(getParent().getLayoutDirection());
                applyTintForDrawable();
                drawable.setVisible(getParent().getVisibility() == VISIBLE, false);
                if(drawable instanceof Animatable){
                    shouldStartAnimation = true;
                }
            }
        }
    }

    public TintInfo getTint() {
        return tint;
    }

    public void setTint(TintInfo tint) {
        this.tint = tint;
        applyTintForDrawable();
    }

    public boolean isShouldStartAnimation() {
        return shouldStartAnimation;
    }

    public void setShouldStartAnimation(boolean shouldStartAnimation) {
        this.shouldStartAnimation = shouldStartAnimation;
    }

    @Override
    public int getRawContentWidth() {
        return imageWidth;
    }

    @Override
    public int getRawContentHeight() {
        return imageHeight;
    }

    public void jumpDrawablesToCurrentState() {
        if (drawable != null && drawable.isStateful()){
            drawable.jumpToCurrentState();
        }
    }

    public void drawableStateChanged(int[] drawableState) {
        if (drawable != null && drawable.isStateful() && drawable.setState(drawableState)) {
            getParent().invalidateDrawable(drawable);
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        onVisibilityAggregated(getParent().getVisibility()==VISIBLE);
    }

    public void onVisibilityAggregated(boolean isVisible) {
        if (drawable instanceof Animatable) {
            if (isVisible&&getVisibility()==VISIBLE) {
                startIconAnimation();
                drawable.setVisible(true, false);
            } else {
                stopIconAnimation();
                drawable.setVisible(false, false);
            }
        }
    }

    private void startIconAnimation(){
        shouldStartAnimation = true;
    }

    private void stopIconAnimation(){
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).stop();
            shouldStartAnimation = false;
        }
    }

    public void drawOnCanvas(Canvas canvas){
        if(getVisibility()==VISIBLE) {
            VirtualLayoutParams layoutParams = getLayoutParams();
            drawable.setBounds(layoutParams.left, layoutParams.top, layoutParams.right, layoutParams.bottom);
            drawable.draw(canvas);

            if (shouldStartAnimation && drawable instanceof Animatable) {
                shouldStartAnimation = false;
                ((Animatable) drawable).start();
            }
        }
    }

}
