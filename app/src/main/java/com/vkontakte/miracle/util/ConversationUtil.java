package com.vkontakte.miracle.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.miracle.engine.util.ResourcesUtl;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.drawable.ChatPhotoDrawable;
import com.vkontakte.miracle.model.general.ExtendedArrays;
import com.vkontakte.miracle.model.general.Owner;
import com.vkontakte.miracle.model.messages.fields.Action;
import com.vkontakte.miracle.model.users.Profile;
import com.vkontakte.miracle.model.users.fileds.Acc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConversationUtil {


    public static final int ACTION_TYPING = R.string.typing_message;
    public static final int ACTION_TYPING_BY = R.string.typing_message_by;
    public static final int ACTION_TYPING_BY_MULTIPLE = R.string.typing_message_by_multiple;

    public static final int ACTION_RECORDING_VOICE_MESSAGE = R.string.recording_voice_message;
    public static final int ACTION_RECORDING_VOICE_MESSAGE_BY = R.string.recording_voice_message_by;
    public static final int ACTION_RECORDING_VOICE_MESSAGE_BY_MULTIPLE = R.string.recording_voice_message_by_multiple;


    public static final int ACTION_INVITE_USER_MALE = R.string.conversation_action_invite_user_male;
    public static final int ACTION_INVITE_USER_FEMALE = R.string.conversation_action_invite_user_female;
    public static final int ACTION_INVITE_USER_NO_SEX = R.string.conversation_action_invite_user_no_sex;
    public static final int ACTION_INVITE_USER_NEUTER = R.string.conversation_action_invite_user_neuter;
    public static final int ACTION_INVITE_USER_BY_SELF_MALE = R.string.conversation_action_invite_user_by_self_male;
    public static final int ACTION_INVITE_USER_BY_SELF_FEMALE = R.string.conversation_action_invite_user_by_self_female;
    public static final int ACTION_INVITE_USER_BY_SELF_NO_SEX = R.string.conversation_action_invite_user_by_self_no_sex;
    public static final int ACTION_INVITE_USER_BY_SELF_NEUTER = R.string.conversation_action_invite_user_by_self_neuter;
    public static final int ACTION_INVITE_USER_BY_YOU = R.string.conversation_action_invite_user_by_you;
    public static final int ACTION_INVITE_USER_BY_SELF_YOU = R.string.conversation_action_invite_user_by_self_you;

    public static final int ACTION_KICK_USER_MALE = R.string.conversation_action_kick_user_male;
    public static final int ACTION_KICK_USER_FEMALE = R.string.conversation_action_kick_user_female;
    public static final int ACTION_KICK_USER_NO_SEX = R.string.conversation_action_kick_user_no_sex;
    public static final int ACTION_KICK_USER_NEUTER = R.string.conversation_action_kick_user_neuter;
    public static final int ACTION_KICK_USER_BY_SELF_MALE = R.string.conversation_action_kick_user_by_self_male;
    public static final int ACTION_KICK_USER_BY_SELF_FEMALE = R.string.conversation_action_kick_user_by_self_female;
    public static final int ACTION_KICK_USER_BY_SELF_NO_SEX = R.string.conversation_action_kick_user_by_self_no_sex;
    public static final int ACTION_KICK_USER_BY_SELF_NEUTER = R.string.conversation_action_kick_user_by_self_neuter;
    public static final int ACTION_KICK_USER_BY_YOU = R.string.conversation_action_kick_user_by_you;
    public static final int ACTION_KICK_USER_BY_SELF_YOU = R.string.conversation_action_kick_user_by_self_you;

    public static final int ACTION_CHAT_CREATE_MALE = R.string.conversation_action_chat_create_male;
    public static final int ACTION_CHAT_CREATE_FEMALE = R.string.conversation_action_chat_create_female;
    public static final int ACTION_CHAT_CREATE_NO_SEX = R.string.conversation_action_chat_create_no_sex;
    public static final int ACTION_CHAT_CREATE_NEUTER = R.string.conversation_action_chat_create_neuter;
    public static final int ACTION_CHAT_CREATE_BY_YOU = R.string.conversation_action_chat_create_by_you;

    public static final int ACTION_CHAT_PHOTO_UPDATE_MALE = R.string.conversation_action_chat_photo_update_male;
    public static final int ACTION_CHAT_PHOTO_UPDATE_FEMALE = R.string.conversation_action_chat_photo_update_female;
    public static final int ACTION_CHAT_PHOTO_UPDATE_NO_SEX = R.string.conversation_action_chat_photo_update_no_sex;
    public static final int ACTION_CHAT_PHOTO_UPDATE_NEUTER = R.string.conversation_action_chat_photo_update_neuter;
    public static final int ACTION_CHAT_PHOTO_UPDATE_BY_YOU = R.string.conversation_action_chat_photo_update_by_you;

    public static final int ACTION_CHAT_TITLE_UPDATE_MALE = R.string.conversation_action_chat_title_update_male;
    public static final int ACTION_CHAT_TITLE_UPDATE_FEMALE = R.string.conversation_action_chat_title_update_female;
    public static final int ACTION_CHAT_TITLE_UPDATE_NO_SEX = R.string.conversation_action_chat_title_update_no_sex;
    public static final int ACTION_CHAT_TITLE_UPDATE_NEUTER = R.string.conversation_action_chat_title_update_neuter;
    public static final int ACTION_CHAT_TITLE_UPDATE_BY_YOU = R.string.conversation_action_chat_title_update_by_you;

    public static String getCreateString(Owner messageOwner, Action action, boolean isOut, Context context){
        if (isOut) {
            return context.getString(ACTION_CHAT_CREATE_BY_YOU, action.getText());
        } else {
            if (messageOwner instanceof Profile) {
                Profile profile = (Profile) messageOwner;
                switch (profile.getSex()){
                    default:
                    case 0:{
                        return context.getString(ACTION_CHAT_CREATE_NO_SEX,
                                profile.getFullName(), action.getText());
                    }
                    case 1:{
                        return context.getString(ACTION_CHAT_CREATE_FEMALE,
                                profile.getFullName(), action.getText());
                    }
                    case 2:{
                        return context.getString(ACTION_CHAT_CREATE_MALE,
                                profile.getFullName(), action.getText());
                    }
                }
            } else if (messageOwner != null) {
                return context.getString(ACTION_CHAT_CREATE_NEUTER,
                        messageOwner.getFullName(), action.getText());
            }
        }
        return "";
    }

    public static String getPhotoUpdateString(Owner messageOwner, boolean isOut, Context context){
        if (isOut) {
            return context.getString(ACTION_CHAT_PHOTO_UPDATE_BY_YOU);
        } else {
            if (messageOwner instanceof Profile) {
                Profile profile = (Profile) messageOwner;
                switch (profile.getSex()){
                    default:
                    case 0:{
                        return context.getString(ACTION_CHAT_PHOTO_UPDATE_NO_SEX,
                                profile.getFullName());
                    }
                    case 1:{
                        return context.getString(ACTION_CHAT_PHOTO_UPDATE_FEMALE,
                                profile.getFullName());
                    }
                    case 2:{
                        return context.getString(ACTION_CHAT_PHOTO_UPDATE_MALE,
                                profile.getFullName());
                    }
                }
            } else if (messageOwner != null) {
                return context.getString(ACTION_CHAT_PHOTO_UPDATE_NEUTER,
                        messageOwner.getFullName());
            }
        }
        return "";
    }

    public static String getTitleUpdateString(Owner messageOwner, Action action, boolean isOut, Context context){
        if (isOut) {
            return context.getString(ACTION_CHAT_TITLE_UPDATE_BY_YOU, action.getText());
        } else {
            if (messageOwner instanceof Profile) {
                Profile profile = (Profile) messageOwner;
                switch (profile.getSex()){
                    default:
                    case 0:{
                        return context.getString(ACTION_CHAT_TITLE_UPDATE_NO_SEX,
                                profile.getFullName(), action.getText());
                    }
                    case 1:{
                        return context.getString(ACTION_CHAT_TITLE_UPDATE_FEMALE,
                                profile.getFullName(), action.getText());
                    }
                    case 2:{
                        return context.getString(ACTION_CHAT_TITLE_UPDATE_MALE,
                                profile.getFullName(), action.getText());
                    }
                }
            } else if (messageOwner != null) {
                return context.getString(ACTION_CHAT_TITLE_UPDATE_NEUTER,
                        messageOwner.getFullName(), action.getText());
            }
        }
        return "";
    }

    public static String getInviteUserString(Owner messageOwner, Action action, ExtendedArrays extendedArrays, boolean isOut, Context context){
        if(action.getMemberId()!=null&&messageOwner!=null){
            if(isOut){
                if (action.getMemberId().equals(messageOwner.getId())) {
                    return context.getString(ACTION_INVITE_USER_BY_SELF_YOU);
                } else {
                    Owner member = extendedArrays.findOwnerById(action.getMemberId());
                    if(member!=null) {
                        if(member instanceof Profile){
                            Profile profileMember = (Profile) member;
                            Acc acc = profileMember.getAcc();
                            return context.getString(ACTION_INVITE_USER_BY_YOU, acc.getFullName());
                        } else {
                            return context.getString(ACTION_INVITE_USER_BY_YOU, member.getFullName());
                        }
                    }
                }
            } else {
                if(action.getMemberId().equals(messageOwner.getId())){
                    if (messageOwner instanceof Profile) {
                        Profile profile = (Profile) messageOwner;
                        switch (profile.getSex()){
                            default:
                            case 0:{
                                return context.getString(ACTION_INVITE_USER_BY_SELF_NO_SEX,
                                        profile.getFullName());
                            }
                            case 1:{
                                return context.getString(ACTION_INVITE_USER_BY_SELF_FEMALE,
                                        profile.getFullName());
                            }
                            case 2:{
                                return context.getString(ACTION_INVITE_USER_BY_SELF_MALE,
                                        messageOwner.getFullName());
                            }
                        }
                    } else {
                        return context.getString(ACTION_INVITE_USER_BY_SELF_NEUTER,
                                        messageOwner.getFullName());
                    }
                } else {
                    Owner member = extendedArrays.findOwnerById(action.getMemberId());
                    if(member!=null){
                        if(member instanceof Profile){
                            Profile profileMember = (Profile) member;
                            Acc acc = profileMember.getAcc();
                            if (messageOwner instanceof Profile) {
                                Profile profile = (Profile) messageOwner;
                                switch (profile.getSex()){
                                    default:
                                    case 0:{
                                        return context.getString(ACTION_INVITE_USER_NO_SEX,
                                                profile.getFullName(), acc.getFullName());
                                    }
                                    case 1:{
                                        return context.getString(ACTION_INVITE_USER_FEMALE,
                                                profile.getFullName(), acc.getFullName());
                                    }
                                    case 2:{
                                        return context.getString(ACTION_INVITE_USER_MALE,
                                                profile.getFullName(), acc.getFullName());
                                    }
                                }
                            } else {
                                return context.getString(ACTION_INVITE_USER_NEUTER,
                                                messageOwner.getFullName(), acc.getFullName());
                            }
                        } else {
                            if (messageOwner instanceof Profile) {
                                Profile profile = (Profile) messageOwner;
                                switch (profile.getSex()){
                                    default:
                                    case 0:{
                                        return context.getString(ACTION_INVITE_USER_NO_SEX,
                                                profile.getFullName(), member.getFullName());
                                    }
                                    case 1:{
                                        return context.getString(ACTION_INVITE_USER_FEMALE,
                                                profile.getFullName(), member.getFullName());
                                    }
                                    case 2:{
                                        return context.getString(ACTION_INVITE_USER_MALE,
                                                profile.getFullName(), member.getFullName());
                                    }
                                }
                            } else {
                                return context.getString(ACTION_INVITE_USER_NEUTER,
                                                messageOwner.getFullName(), member.getFullName());
                            }
                        }
                    }
                }
            }
        }
        return "";
    }

    public static String getKickUserString(Owner messageOwner, Action action, ExtendedArrays extendedArrays, boolean isOut, Context context){
        if(action.getMemberId()!=null&&messageOwner!=null){
            if(isOut){
                if (action.getMemberId().equals(messageOwner.getId())) {
                    return context.getString(ACTION_KICK_USER_BY_SELF_YOU);
                } else {
                    Owner member = extendedArrays.findOwnerById(action.getMemberId());
                    if(member!=null) {
                        if(member instanceof Profile){
                            Profile profileMember = (Profile) member;
                            Acc acc = profileMember.getAcc();
                            return context.getString(ACTION_KICK_USER_BY_YOU, acc.getFullName());
                        } else {
                            return context.getString(ACTION_KICK_USER_BY_YOU, member.getFullName());
                        }
                    }
                }
            } else {
                if(action.getMemberId().equals(messageOwner.getId())){
                    if (messageOwner instanceof Profile) {
                        Profile profile = (Profile) messageOwner;
                        switch (profile.getSex()){
                            default:
                            case 0:{
                                return context.getString(ACTION_KICK_USER_BY_SELF_NO_SEX,
                                        profile.getFullName());
                            }
                            case 1:{
                                return context.getString(ACTION_KICK_USER_BY_SELF_FEMALE,
                                        profile.getFullName());
                            }
                            case 2:{
                                return context.getString(ACTION_KICK_USER_BY_SELF_MALE,
                                        messageOwner.getFullName());
                            }
                        }
                    } else {
                        return context.getString(ACTION_KICK_USER_BY_SELF_NEUTER,
                                messageOwner.getFullName());
                    }
                } else {
                    Owner member = extendedArrays.findOwnerById(action.getMemberId());
                    if(member!=null){
                        if(member instanceof Profile){
                            Profile profileMember = (Profile) member;
                            Acc acc = profileMember.getAcc();
                            if (messageOwner instanceof Profile) {
                                Profile profile = (Profile) messageOwner;
                                switch (profile.getSex()){
                                    default:
                                    case 0:{
                                        return context.getString(ACTION_KICK_USER_NO_SEX,
                                                profile.getFullName(), acc.getFullName());
                                    }
                                    case 1:{
                                        return context.getString(ACTION_KICK_USER_FEMALE,
                                                profile.getFullName(), acc.getFullName());
                                    }
                                    case 2:{
                                        return context.getString(ACTION_KICK_USER_MALE,
                                                profile.getFullName(), acc.getFullName());
                                    }
                                }
                            } else {
                                return context.getString(ACTION_KICK_USER_NEUTER,
                                        messageOwner.getFullName(), acc.getFullName());
                            }
                        } else {
                            if (messageOwner instanceof Profile) {
                                Profile profile = (Profile) messageOwner;
                                switch (profile.getSex()){
                                    default:
                                    case 0:{
                                        return context.getString(ACTION_KICK_USER_NO_SEX,
                                                profile.getFullName(), member.getFullName());
                                    }
                                    case 1:{
                                        return context.getString(ACTION_KICK_USER_FEMALE,
                                                profile.getFullName(), member.getFullName());
                                    }
                                    case 2:{
                                        return context.getString(ACTION_KICK_USER_MALE,
                                                profile.getFullName(), member.getFullName());
                                    }
                                }
                            } else {
                                return context.getString(ACTION_KICK_USER_NEUTER,
                                        messageOwner.getFullName(), member.getFullName());
                            }
                        }
                    }
                }
            }
        }
        return "";
    }



    public static Drawable getConversationAvatarPlaceHolder(String title, Context context){
        int color1 = ResourcesUtl.getColorByAttributeId(context, com.miracle.engine.R.attr.colorPrimary_60);
        int color2 = ResourcesUtl.getColorByAttributeId(context, com.miracle.engine.R.attr.colorPrimary_50);
        return new ChatPhotoDrawable(getFirstTwoLetters(title), Color.WHITE, color1, color2);
    }


    private static String getFirstTwoLetters(String str) {
        String[] words = str.split("\\s+");
        StringBuilder result = new StringBuilder();
        if (words.length >= 1) {
            result.append(getWordFirstLetter(words[0]));
        }
        if (words.length >= 2) {
            result.append(getWordFirstLetter(words[1]));

        }
        return result.toString().toUpperCase();
    }

    private static String getWordFirstLetter(String word){
        if(word.length()>1){
           String sub = word.substring(0,2);
           if (isTwoCharEmoji(sub)){
               return sub;
           } else {
               return word.substring(0,1);
           }
        } else {
            return word;
        }
    }

    public static boolean isTwoCharEmoji(String str) {
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length - 1; i++) {
            char c1 = chars[i];
            char c2 = chars[i + 1];
            if (Character.isHighSurrogate(c1)) {
                if (Character.isLowSurrogate(c2)) {
                    int codePoint = Character.toCodePoint(c1, c2);
                    if (isTwoCharEmoji(codePoint)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean isTwoCharEmoji(int codePoint) {
        // проверяем, является ли код точки символом эмодзи из двух символов
        // диапазоны кодов точек были взяты из стандарта Unicode для эмодзи
        return (codePoint >= 0x1F600 && codePoint <= 0x1F64F) // эмодзи смайликов
                || (codePoint >= 0x1F900 && codePoint <= 0x1F9FF) // дополнительные эмодзи
                || (codePoint >= 0x1F680 && codePoint <= 0x1F6FF) // эмодзи транспорта и техники
                || (codePoint >= 0x2600 && codePoint <= 0x26FF) // эмодзи погоды
                || (codePoint >=0x2700 && codePoint <= 0x27BF) // эмодзи символов
                || (codePoint >= 0xFE00 && codePoint <= 0xFE0F) // варианты эмодзи
                || (codePoint >= 0x1F1E6 && codePoint <= 0x1F1FF); // эмодзи флагов
    }

}
