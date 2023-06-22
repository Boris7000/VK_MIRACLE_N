package com.miracle.engine.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;

public class DimensionUtil {
    public static float dpToPx(@NonNull Context context, @Dimension(unit = Dimension.DP) int dp) {
        Resources r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }
}
