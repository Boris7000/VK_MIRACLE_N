package com.vkontakte.miracle.util;

import android.content.Context;

import com.vkontakte.miracle.R;

public class OnlineUtil {

    public static final int ONLINE = R.string.online;
    public static final int LAST_SEEN_MALE = R.string.last_seen_male;
    public static final int LAST_SEEN_FEMALE = R.string.last_seen_female;
    public static final int LAST_SEEN_NO_SEX = R.string.last_seen_no_sex;

    public static String getOnlineString(boolean online, long timeSec, int sex, Context context) {
        if (online) return context.getString(ONLINE);

        switch (sex) {
            default:
            case 0: {
                return context.getString(LAST_SEEN_NO_SEX,
                        TimeUtil.getRelativeDateString(context, timeSec, false));
            }
            case 1: {
                return context.getString(LAST_SEEN_FEMALE,
                        TimeUtil.getRelativeDateString(context, timeSec, false));
            }
            case 2: {
                return context.getString(LAST_SEEN_MALE,
                        TimeUtil.getRelativeDateString(context, timeSec, false));
            }
        }
    }

}
