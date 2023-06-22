package com.vkontakte.miracle.util;

import android.content.Context;

import com.vkontakte.miracle.R;

import java.util.Calendar;
import java.util.Locale;

public class TimeUtil {

    private static final int ONE_MINUTE_SEC = 60;
    private static final int ONE_HOUR_SEC = 3600;
    private static final int ONE_DAY_SEC = 86400;
    private static final int ONE_WEEK_SEC = 604800;
    private static final int ONE_MOTH_SEC = 2678400;
    private static final int ONE_YEAR_SEC = 31536000;

    public static String getDurationStringSecs(long secs){
        if(secs>=ONE_HOUR_SEC){
            return String.format(Locale.ENGLISH,"%d:%d:%02d",
                    secs/ONE_HOUR_SEC,
                    (secs%ONE_HOUR_SEC)/ONE_MINUTE_SEC,
                    secs%ONE_MINUTE_SEC);
        } else {
            return String.format(Locale.ENGLISH,"%d:%02d",
                    (secs%ONE_HOUR_SEC)/ONE_MINUTE_SEC,
                    secs%ONE_MINUTE_SEC);
        }
    }

    public static String getDurationStringMills(long millis){
        return  getDurationStringSecs(millis/1000);
    }

    public static String getShortDateString(Context context, long dateSec){

        long currentDate = System.currentTimeMillis();
        long currentDateSec = currentDate/1000;
        long deltaSec = currentDateSec - dateSec;

        if(deltaSec<=0){
            return "";
        }

        if(deltaSec<ONE_MINUTE_SEC){
            return String.format(context.getString(R.string.formatted_date_ago_short_sec), deltaSec);
        }

        if(deltaSec<ONE_HOUR_SEC){
            long minutesDelta = deltaSec/ONE_MINUTE_SEC;
            return String.format(context.getString(R.string.formatted_date_ago_short_minute), minutesDelta);
        }

        if(deltaSec<ONE_DAY_SEC){
            long hoursDelta = deltaSec/ONE_HOUR_SEC;
            return String.format(context.getString(R.string.formatted_date_ago_short_hour), hoursDelta);
        }

        if(deltaSec<ONE_WEEK_SEC){
            long dayDelta = deltaSec/ONE_DAY_SEC;
            return String.format(context.getString(R.string.formatted_date_ago_short_day), dayDelta);
        }

        if(deltaSec<ONE_MOTH_SEC){
            long weekDelta = deltaSec/ONE_WEEK_SEC;
            return String.format(context.getString(R.string.formatted_date_ago_short_week), weekDelta);
        }

        if(deltaSec<ONE_YEAR_SEC){
            long monthDelta = deltaSec/ONE_MOTH_SEC;
            return String.format(context.getString(R.string.formatted_date_ago_short_month), monthDelta);
        }

        long yearDelta = deltaSec/ONE_YEAR_SEC;
        return String.format(context.getString(R.string.formatted_date_ago_short_year), yearDelta);

    }

    public static String getRelativeDateString(Context context, long dateSec, boolean useToday){
        long currentDateMills = System.currentTimeMillis();
        long currentDateSec = currentDateMills/1000;
        long deltaSec = currentDateSec - dateSec;

        if(deltaSec<5){
            return context.getString(R.string.recently);
        } else if (deltaSec<ONE_MINUTE_SEC){
            return secondsDeclination(context, (int) deltaSec);
        } else if (deltaSec<ONE_HOUR_SEC){
            return minutesDeclination(context, ((int) deltaSec/ONE_MINUTE_SEC));
        } else if (deltaSec<ONE_HOUR_SEC*2){
            return context.getString(R.string.formatted_date_ago_hour4);
        } else {
            Calendar nowCalendar = Calendar.getInstance();
            nowCalendar.setTimeInMillis(currentDateMills);
            int currentYear = nowCalendar.get(Calendar.YEAR);
            int currentDayOfYear = nowCalendar.get(Calendar.DAY_OF_YEAR);

            Calendar pastCalendar = Calendar.getInstance();
            pastCalendar.setTimeInMillis(dateSec*1000);
            int pastYear = pastCalendar.get(Calendar.YEAR);
            int pastMonth = pastCalendar.get(Calendar.MONTH);
            int pastDayOfMonth = pastCalendar.get(Calendar.DAY_OF_MONTH);
            int pastDayOfYear = pastCalendar.get(Calendar.DAY_OF_YEAR);
            String pastTime = pastCalendar.get(Calendar.HOUR_OF_DAY)+":"+pastCalendar.get(Calendar.MINUTE);

            if(currentYear==pastYear){
                if(currentDayOfYear==pastDayOfYear){
                    if(useToday){
                        return context.getString(R.string.formatted_date_today_at, pastTime);
                    } else {
                        return context.getString(R.string.formatted_date_at, pastTime);
                    }

                } else if(currentDayOfYear-1==pastDayOfYear){
                    return context.getString(R.string.formatted_date_yesterday_at, pastTime);
                } else {
                    return context.getString(R.string.formatted_date_day_month_time,
                            pastDayOfMonth, shortMonths(context, pastMonth), pastTime);
                }
            } else {
                return context.getString(R.string.formatted_date_day_month_year_time,
                        pastDayOfMonth, shortMonths(context, pastMonth), pastYear, pastTime);
            }
        }

    }

    private static String shortMonths(Context context, int month){
        switch (month){
            default:
            case 0: return context.getString(R.string.formatted_date_short_month_1);
            case 1: return context.getString(R.string.formatted_date_short_month_2);
            case 2: return context.getString(R.string.formatted_date_short_month_3);
            case 3: return context.getString(R.string.formatted_date_short_month_4);
            case 4: return context.getString(R.string.formatted_date_short_month_5);
            case 5: return context.getString(R.string.formatted_date_short_month_6);
            case 6: return context.getString(R.string.formatted_date_short_month_7);
            case 7: return context.getString(R.string.formatted_date_short_month_8);
            case 8: return context.getString(R.string.formatted_date_short_month_9);
            case 9: return context.getString(R.string.formatted_date_short_month_10);
            case 10: return context.getString(R.string.formatted_date_short_month_11);
            case 11: return context.getString(R.string.formatted_date_short_month_12);
        }
    }

    private static String minutesDeclination(Context context, int minutes){
        if (minutes==1){
            return context.getString(R.string.formatted_date_ago_minute4);
        } else if (minutes<20 && minutes>10){
            return context.getString(R.string.formatted_date_ago_minute2, minutes);
        } else {
            switch (minutes%10){
                default:
                case 9:
                case 8:
                case 7:
                case 6:
                case 5:
                case 0:{
                    return context.getString(R.string.formatted_date_ago_minute2, minutes);
                }
                case 1:{
                    return context.getString(R.string.formatted_date_ago_minute3, minutes);
                }
                case 3:
                case 4:
                case 2:{
                    return context.getString(R.string.formatted_date_ago_minute1, minutes);
                }
            }
        }
    }

    private static String secondsDeclination(Context context, int seconds){
        if (seconds==1){
            return context.getString(R.string.formatted_date_ago_sec4);
        } else if (seconds<20 && seconds>10){
            return context.getString(R.string.formatted_date_ago_sec2, seconds);
        } else {
            switch (seconds%10){
                default:
                case 9:
                case 8:
                case 7:
                case 6:
                case 5:
                case 0:{
                    return context.getString(R.string.formatted_date_ago_sec2, seconds);
                }
                case 1:{
                    return context.getString(R.string.formatted_date_ago_sec3, seconds);
                }
                case 3:
                case 4:
                case 2:{
                    return context.getString(R.string.formatted_date_ago_sec1, seconds);
                }
            }
        }
    }

}
