package com.vkontakte.miracle.util;

import static android.content.Context.WINDOW_SERVICE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.vkontakte.miracle.network.vkapi.APIConstants;

import java.util.Locale;

public class DeviceUtil {

    public static String getUserAgent(){
        return String.format(Locale.US,
                "VKAndroidApp/%s-%s (Android %s; SDK %d; %s; %s; %s; %s)",
                APIConstants.APP_VERSION_NAME,
                APIConstants.APP_VERSION_CODE,
                Build.VERSION.RELEASE,
                Build.VERSION.SDK_INT,
                Build.SUPPORTED_ABIS[0],
                getDeviceName(),
                System.getProperty("user.language"),
                SCREEN_RESOLUTION());
    }

    @SuppressLint("HardwareIds")
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public static String SCREEN_RESOLUTION() {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        if (metrics == null) {
            return "1920x1080";
        } else {
            return metrics.heightPixels + "x" + metrics.widthPixels;
        }
    }


    public static int getWindowHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    public static int getWindowWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

}
