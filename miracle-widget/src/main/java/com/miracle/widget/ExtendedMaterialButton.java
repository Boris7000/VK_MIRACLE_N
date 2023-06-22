package com.miracle.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;

public class ExtendedMaterialButton extends MaterialButton implements ExtendedTextViewIcons{

    private final ExtendedTextHelper textHelper = new ExtendedTextHelper(this);
    private final ExtendedMaterialButtonHelper buttonHelper = new ExtendedMaterialButtonHelper(this, textHelper);

    public ExtendedMaterialButton(@NonNull Context context) {
        this(context, null);
    }

    public ExtendedMaterialButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, com.google.android.material.R.attr.materialButtonStyle);
    }

    public ExtendedMaterialButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        buttonHelper.loadFromAttributes(attrs,defStyleAttr);

        Resources.Theme theme = getContext().getTheme();
        final TypedArray a = theme.obtainStyledAttributes(attrs, R.styleable.ExtendedMaterialButton, defStyleAttr, R.style.ExtendedMaterialButton);
        final boolean checked = a.getBoolean(R.styleable.ExtendedMaterialButton_android_checked, false);
        setChecked(checked);
        a.recycle();

        textHelper.loadFromAttributes(attrs,defStyleAttr);
    }

    //////////////////////////////////////////////////////////////////

    @Override
    public void onVisibilityAggregated(boolean isVisible) {
        super.onVisibilityAggregated(isVisible);
        if(textHelper!=null) {
            textHelper.onVisibilityAggregated(isVisible);
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if(textHelper!=null) {
            textHelper.changeIconsDrawablesStates(getDrawableState());
        }
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if(textHelper!=null) {
            textHelper.jumpIconsDrawablesToState();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        textHelper.drawIcons(canvas);
    }

    //////////////////////////////////////////////////////////////////

    @Override
    public int getCompoundPaddingTop() {
        return textHelper.getCompoundPaddingTop(super.getCompoundPaddingTop());
    }

    @Override
    public int getCompoundPaddingBottom() {
        return textHelper.getCompoundPaddingBottom(super.getCompoundPaddingBottom());
    }

    @Override
    public int getCompoundPaddingLeft() {
        return textHelper.getCompoundPaddingLeft(super.getCompoundPaddingLeft());
    }

    @Override
    public int getCompoundPaddingRight() {
        return textHelper.getCompoundPaddingRight(super.getCompoundPaddingRight());
    }

    //////////////////////////////////////////////////////////////////

    @Override
    public void setIcon(@Nullable Drawable drawable, @ExtendedTextHelper.Position int pos) {
        textHelper.setIcon(drawable, pos);
    }

    @Override
    public void setIconResource(@DrawableRes int resourceId, @ExtendedTextHelper.Position int pos) {
        textHelper.setIconResource(resourceId, pos);
    }

    @Override
    public void setIconTint(@ColorInt int color, @ExtendedTextHelper.Position int pos) {
        textHelper.setIconTint(color, pos);
    }

    @Override
    public void setIconTintList(ColorStateList tintList, @ExtendedTextHelper.Position int pos) {
        textHelper.setIconTintList(tintList, pos);
    }

    @Override
    public void setIconTintMode(PorterDuff.Mode tintMode, @ExtendedTextHelper.Position int pos) {
        textHelper.setIconTintMode(tintMode, pos);
    }

    @Override
    public void setIconSize(int iconSize, @ExtendedTextHelper.Position int pos) {
        textHelper.setIconSize(iconSize, pos);
    }

    @Override
    public void setIconStickingToText(boolean stickingToText, @ExtendedTextHelper.Position int pos) {
        textHelper.setIconStickingToText(stickingToText, pos);
    }

    @Override
    public void setIconsTint(int color) {
        textHelper.setIconsTint(color);
    }

    @Override
    public void setIconsTintList(ColorStateList tintList) {
        textHelper.setIconsTintList(tintList);
    }

    @Override
    public void setIconsTintMode(PorterDuff.Mode tintMode) {
        textHelper.setIconsTintMode(tintMode);
    }

    @Override
    public void setIconsSize(int iconSize) {
        textHelper.setIconsSize(iconSize);
    }

    @Override
    public void setIconsStickingToText(boolean stickingToText) {
        textHelper.setIconsStickingToText(stickingToText);
    }

    //////////////////////////////////////////////////////////////////

    public void setAnimationDuration(int animationDuration){
        buttonHelper.setAnimationDuration(animationDuration);
    }

    //////////////////////////////////////////////////////////////////

    private boolean mToggled = false;

    @Override
    public void toggle() {
        mToggled = true;
        super.toggle();
    }

    public void setToggled(boolean mToggled) {
        this.mToggled = mToggled;
    }

}
