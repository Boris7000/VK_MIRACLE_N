package com.vkontakte.miracle.util;

import android.content.Context;

import com.vkontakte.miracle.R;

import java.text.DecimalFormat;

public class CountUtil {

    public static String reduceTheNumber(int number){
        if(number>=1000000){
            return new DecimalFormat("#.#M").format(number/1000000f);
        }
        if(number>=1000){
            return new DecimalFormat("#.#K").format(number/1000f);
        }
        return number>0?String.valueOf(number):"";
    }

    public static String getMembersCount(int count, Context context){
        if(count==0) return "";

        if(count==1) return context.getString(R.string.member);

        String reducedNumberString = reduceTheNumber(count);

        if(count<20 && count>10){
            return context.getString(R.string.members_counter_2, reducedNumberString);
        }

        switch (count%10){
            default:
            case 9:
            case 8:
            case 7:
            case 6:
            case 5:
            case 0:{return
                    context.getString(R.string.members_counter_2, reducedNumberString);
            }
            case 1:{
                return context.getString(R.string.members_counter_3, reducedNumberString);
            }
            case 3:
            case 4:
            case 2:{
                return  context.getString(R.string.members_counter_1, reducedNumberString);
            }
        }
    }

    public static String getAttachmentsCount(int count, Context context){
        if(count==0) return "";

        String reducedNumberString = reduceTheNumber(count);

        if(count<20 && count>10){
            return context.getString(R.string.attachments_counter_2, reducedNumberString);
        }

        switch (count%10){
            default:
            case 9:
            case 8:
            case 7:
            case 6:
            case 5:
            case 0:{return
                    context.getString(R.string.attachments_counter_2, reducedNumberString);
            }
            case 1:{
                return context.getString(R.string.attachments_counter_3, reducedNumberString);
            }
            case 3:
            case 4:
            case 2:{
                return  context.getString(R.string.attachments_counter_1, reducedNumberString);
            }
        }
    }

    public static String getPhotosCount(int count, Context context){
        if(count==0) return "";

        if(count==1) return context.getString(R.string.photo);

        String reducedNumberString = reduceTheNumber(count);

        if(count<20 && count>10){
            return context.getString(R.string.photos_counter_2, reducedNumberString);
        }

        switch (count%10){
            default:
            case 9:
            case 8:
            case 7:
            case 6:
            case 5:
            case 0:{return
                    context.getString(R.string.photos_counter_2, reducedNumberString);
            }
            case 1:{
                return context.getString(R.string.photos_counter_3, reducedNumberString);
            }
            case 3:
            case 4:
            case 2:{
                return  context.getString(R.string.photos_counter_1, reducedNumberString);
            }
        }
    }

    public static String getAudiosCount(int count, Context context){
        if(count==0) return "";

        if(count==1) return context.getString(R.string.audio);

        String reducedNumberString = reduceTheNumber(count);

        if(count<20 && count>10){
            return context.getString(R.string.audios_counter_2, reducedNumberString);
        }

        switch (count%10){
            default:
            case 9:
            case 8:
            case 7:
            case 6:
            case 5:
            case 0:{return
                    context.getString(R.string.audios_counter_2, reducedNumberString);
            }
            case 1:{
                return context.getString(R.string.audios_counter_3, reducedNumberString);
            }
            case 3:
            case 4:
            case 2:{
                return  context.getString(R.string.audios_counter_1, reducedNumberString);
            }
        }
    }

    public static String getVideosCount(int count, Context context){
        if(count==0) return "";

        if(count==1) return context.getString(R.string.video);

        String reducedNumberString = reduceTheNumber(count);

        if(count<20 && count>10){
            return context.getString(R.string.video_counter_2, reducedNumberString);
        }

        switch (count%10){
            default:
            case 9:
            case 8:
            case 7:
            case 6:
            case 5:
            case 0:{return
                    context.getString(R.string.video_counter_2, reducedNumberString);
            }
            case 1:{
                return context.getString(R.string.video_counter_3, reducedNumberString);
            }
            case 3:
            case 4:
            case 2:{
                return  context.getString(R.string.video_counter_1, reducedNumberString);
            }
        }
    }

    public static String getFilesCount(int count, Context context){
        if(count==0) return "";

        if(count==1) return context.getString(R.string.file);

        String reducedNumberString = reduceTheNumber(count);

        if(count<20 && count>10){
            return context.getString(R.string.files_counter_2, reducedNumberString);
        }

        switch (count%10){
            default:
            case 9:
            case 8:
            case 7:
            case 6:
            case 5:
            case 0:{return
                    context.getString(R.string.files_counter_2, reducedNumberString);
            }
            case 1:{
                return context.getString(R.string.files_counter_3, reducedNumberString);
            }
            case 3:
            case 4:
            case 2:{
                return  context.getString(R.string.files_counter_1, reducedNumberString);
            }
        }
    }

}
