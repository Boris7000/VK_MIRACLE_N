package com.vkontakte.miracle.util;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class ServiceUtil {

    public static void startServiceCompat(Context context, Intent serviceIntents){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntents);
        } else {
            context.startService(serviceIntents);
        }
    }

}
