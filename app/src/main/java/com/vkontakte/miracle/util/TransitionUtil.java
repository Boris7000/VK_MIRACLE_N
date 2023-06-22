package com.vkontakte.miracle.util;

import android.content.Context;

import androidx.annotation.IdRes;

import com.google.android.material.transition.MaterialContainerTransform;

public class TransitionUtil {

    public static MaterialContainerTransform buildContainerTransform(Context context, boolean entering, @IdRes int drawingViewId, long duration){
        MaterialContainerTransform transform = new MaterialContainerTransform(context, entering);
        transform.setDrawingViewId(drawingViewId);
        transform.setDuration(duration);
        return transform;
    }

}
