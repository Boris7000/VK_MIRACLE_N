package com.miracle.engine.preferences;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;

import com.miracle.engine.R;

public class ThemePreferences extends SidePreferences{
    public static final int THEME_BLUE = 0;
    public static final int THEME_EMERALD = 1;
    public static final int THEME_VIOLET = 2;
    public static final int THEME_ORANGE = 3;
    public static final int THEME_CRIMSON = 4;
    public static final int THEME_CARROT = 5;
    public static final int THEME_NIGHT = 6;
    public static final int THEME_SAND = 7;
    public static final int THEME_ULTRAMARINE = 8;
    public static final int THEME_MONO = 9;
    public static final int THEME_EVA01 = 10;
    public static final int THEME_EVA02 = 11;
    public static final int THEME_SYSTEM1 = 12;

    private static final int[] THEME_RECOURSE_IDS = new int[]{
            R.style.BlueTheme, //0
            R.style.EmeraldTheme, //1
            R.style.VioletTheme, //2
            R.style.OrangeTheme, //3
            R.style.CrimsonTheme, //4
            R.style.CarrotTheme, //5
            R.style.NightTheme, //6
            R.style.SandTheme, //7
            R.style.UltramarineTheme, //8
            R.style.MonoTheme, //9
            R.style.EVA01Theme, //10
            R.style.EVA02Theme, //11
            R.style.SystemTheme}; //12

    private static final int THEME_DEFAULT = THEME_BLUE;

    public void storeNightMode(int nightMode){
        getPreferences().edit().putInt("nightMode",nightMode).apply();
    }

    public int nightMode(){
        return getPreferences().getInt("nightMode", MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public void storeThemeId(int resId){
        getPreferences().edit().putInt("themeId", resId).apply();
    }

    public int themeId(){
        return getPreferences().getInt("themeId", THEME_DEFAULT);
    }

    public int themeResourceId(){
        return THEME_RECOURSE_IDS[themeId()];
    }


    private static ThemePreferences instance;

    public static ThemePreferences get(){
        if (null == instance){
            instance = new ThemePreferences();
        }
        return instance;
    }


}
