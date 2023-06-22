package com.miracle.engine.preferences;

import android.content.SharedPreferences;

public abstract class SidePreferences {

    private final SharedPreferences preferences;

    public SidePreferences(){
        preferences = AppPreferences.get().getPreferences();
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }
}
