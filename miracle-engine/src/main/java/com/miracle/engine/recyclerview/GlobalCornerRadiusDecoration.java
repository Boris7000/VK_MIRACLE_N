package com.miracle.engine.recyclerview;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GlobalCornerRadiusDecoration extends RecyclerView.ItemDecoration{

    private final int cornerRadius;
    private final RectF defaultRectToClip;

    public GlobalCornerRadiusDecoration(int cornerRadius) {
        defaultRectToClip = new RectF(Float.MAX_VALUE, Float.MAX_VALUE, 0, 0);
        this.cornerRadius = cornerRadius;
    }

    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int childCount = parent.getChildCount();
        if(childCount==0) return;
        final RectF roundRect = new RectF(defaultRectToClip);
        final Rect childRect = new Rect();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, childRect);
            roundRect.left = Math.min(roundRect.left, childRect.left);
            roundRect.top = Math.min(roundRect.top, childRect.top);
            roundRect.right = Math.max(roundRect.right, childRect.right);
            roundRect.bottom = Math.max(roundRect.bottom, childRect.bottom);
        }

        final Path path = new Path();
        path.addRoundRect(roundRect, cornerRadius,
                cornerRadius, Path.Direction.CW);
        canvas.clipPath(path);
    }
}
