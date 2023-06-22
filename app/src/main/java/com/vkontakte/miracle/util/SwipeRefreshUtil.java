package com.vkontakte.miracle.util;

import static com.miracle.engine.util.ResourcesUtl.getColorByAttributeId;

import android.content.Context;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class SwipeRefreshUtil {

    public static void applyDefaultStyle(SwipeRefreshLayout swipeRefreshLayout, Context context){
        swipeRefreshLayout.setColorSchemeColors(
                getColorByAttributeId(context, androidx.appcompat.R.attr.colorPrimary),
                getColorByAttributeId(context, com.google.android.material.R.attr.colorSecondary));

        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(
                getColorByAttributeId(context, com.miracle.engine.R.attr.swipeRefreshCircleColor));

        swipeRefreshLayout.setProgressViewOffset(true, 0, 1);
        swipeRefreshLayout.setProgressViewEndTarget(true, 64);

    }

}
