package com.miracle.engine.preferences;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {

    private static final String SHARED_PREFERENCES_NAME = "com.miracle.engine.PREFERENCES";

    private static AppPreferences instance;

    private final SharedPreferences preferences;

    public static AppPreferences get(){
        return instance;
    }

    public AppPreferences(Context context){
        instance = this;
        preferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }
}
