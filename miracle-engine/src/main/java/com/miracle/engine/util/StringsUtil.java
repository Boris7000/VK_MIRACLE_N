package com.miracle.engine.util;

import android.text.Editable;

import androidx.annotation.Nullable;

public class StringsUtil {

    public static boolean nonNullAndNonEmpty(@Nullable String s){
        if(s == null) return false;
        return !s.isEmpty();
    }

    public static String trimEditable(Editable e){
       return e.toString().trim();
    }

    public static String upperCaseFirstLetter(@Nullable String string){
        if(string==null||string.isEmpty()) return null;
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

}
