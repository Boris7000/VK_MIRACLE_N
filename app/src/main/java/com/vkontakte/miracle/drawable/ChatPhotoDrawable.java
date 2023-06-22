package com.vkontakte.miracle.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;

import androidx.annotation.NonNull;

public class ChatPhotoDrawable extends Drawable {

    private final String text;
    private final int[] colors;
    private final TextPaint textPaint;
    private final Paint gradientPaint;

    private float textDrawX = 0;
    private float textDrawY = 0;

    public ChatPhotoDrawable(String text, int textColor, int... colors) {
        this.text = text;
        this.colors = colors;
        this.textPaint = new TextPaint();

        textPaint.setColor(textColor);
        textPaint.setAntiAlias(true);

        gradientPaint = new Paint();
        gradientPaint.setStyle(Paint.Style.FILL);
        gradientPaint.setAntiAlias(true);
    }

    private void updateBounds(int left, int top, int right, int bottom){
        updateBounds(new Rect(left, top, right, bottom));
    }

    private void updateBounds(Rect bounds){
        int width = bounds.width();
        int height = bounds.height();
        Shader shader = new LinearGradient(0, 0, width, height, colors, null, Shader.TileMode.CLAMP);
        gradientPaint.setShader(shader);

        float textSize = ((width*0.66f)/2);
        textPaint.setTextSize(textSize);

        float measuredTextWidth = textPaint.measureText(text);
        float aviableWidth = width-measuredTextWidth;
        textDrawX = aviableWidth/2f;

        Paint.FontMetrics fm = textPaint.getFontMetrics();
        float texHeight = fm.descent-fm.ascent;
        float aviableHeight = height-texHeight;
        textDrawY = (aviableHeight/2f)-fm.ascent;

    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        updateBounds(left, top, right, bottom);
    }

    @Override
    public void setBounds(@NonNull Rect bounds) {
        super.setBounds(bounds);
        updateBounds(bounds);
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        canvas.drawRect(bounds, gradientPaint);
        canvas.drawText(text, textDrawX, textDrawY, textPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        gradientPaint.setAlpha(alpha);
        textPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        gradientPaint.setColorFilter(colorFilter);
        textPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return gradientPaint.getAlpha();
    }

}
