package com.vkontakte.miracle.view.virtual;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.view.View;

import com.google.android.material.resources.TextAppearance;

@SuppressLint("RestrictedApi")
public class VirtualTextView extends VirtualView {

    final TextPaint paint = new TextPaint();
    TextAppearance textAppearance;
    String text = "";
    String originalText = "";

    int measuredWidth;
    int measuredHeight;
    int originalMeasuredWidth;

    public VirtualTextView(View parent, TypedArray a, int textAppearanceId){
        super(parent);
        setTextAppearance(new TextAppearance(parent.getContext(), a.getResourceId(textAppearanceId,0)));
    }

    public TextPaint getPaint() {
        return paint;
    }

    public TextAppearance getTextAppearance() {
        return textAppearance;
    }

    public void setTextAppearance(TextAppearance textAppearance) {
        this.textAppearance = textAppearance;
        paint.setAntiAlias(true);
        paint.setTextSize(textAppearance.getTextSize());
        paint.setTypeface(Typeface.create(textAppearance.fontFamily, Typeface.NORMAL));
        paint.setLetterSpacing(textAppearance.letterSpacing);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        measuredHeight = (int) (fontMetrics.bottom - fontMetrics.top);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.originalText = text;
        this.text = text;
        measuredWidth = (int) paint.measureText(text);
        originalMeasuredWidth = measuredWidth;
    }

    public String getOriginalText() {
        return originalText;
    }

    public int getRawOriginalMeasuredWidth() {
        return originalMeasuredWidth;
    }

    public int getOriginalContentWidth() {
        if(getVisibility()==GONE) {
            return 0;
        } else {
            return originalMeasuredWidth;
        }
    }

    @Override
    public int getRawContentWidth() {
        return measuredWidth;
    }

    @Override
    public int getRawContentHeight() {
        return measuredHeight;
    }

    public void restoreText(){
        text = originalText;
        measuredWidth = originalMeasuredWidth;
    }

    public void trimText(){
        int rawWidth = getRawWidth();
        restoreText();
        if(measuredWidth>rawWidth){
            int textWidth = measuredWidth;
            while (textWidth>rawWidth){
                text = text.substring(0, text.length()-1);
                textWidth =(int) paint.measureText(text+"...");
            }
            text+="...";
            measuredWidth = textWidth;
        }
    }

    public void trimText(int desired){
        restoreText();
        if(measuredWidth>desired){
            int textWidth = measuredWidth;
            while (textWidth>desired){
                text = text.substring(0, text.length()-1);
                textWidth =(int) paint.measureText(text+"...");
            }
            text+="...";
            measuredWidth = textWidth;
        }
    }

    public View getParent(){
        return null;
    }

    public void drawableStateChanged(int[] drawableState) {
        ColorStateList colorStateList = textAppearance.getTextColor();
        if(colorStateList!=null){
            paint.setColor(colorStateList.getColorForState(drawableState, Color.BLACK));
        }
    }

    public void drawOnCanvas(Canvas canvas){
        if(getVisibility()==VISIBLE) {
            VirtualLayoutParams layoutParams = getLayoutParams();
            Paint.FontMetrics fm = paint.getFontMetrics();
            canvas.drawText(text, layoutParams.left, layoutParams.top + (-fm.ascent), paint);
        }
    }


    /*
    private int getTextWidth(TextPaint textPaint, String text){
        return (int) textPaint.measureText(text);
    }

    private int getTextHeight(TextPaint textPaint) {
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        return (int) (fm.descent - fm.ascent);
    }

    private int getTextFullHeight(TextPaint textPaint) {
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        return (int) (fm.bottom - fm.top);
    }

    private int getTextBottom(TextPaint textPaint) {
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        return (int) fm.bottom;
    }

    private int getTextTop(TextPaint textPaint) {
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        return (int) fm.top;
    }
    */
}
