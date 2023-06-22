package com.vkontakte.miracle.model.longpoll;

import org.json.JSONArray;
import org.json.JSONException;

public class LongPollEvent {

    public static final int TYPE_MESSAGE_FLAGS_SWAP = 1;
    public static final int TYPE_MESSAGE_FLAGS_SET = 2;
    public static final int TYPE_MESSAGE_FLAGS_CLEAR = 3;
    public static final int TYPE_MESSAGE_ADD = 4;
    public static final int TYPE_MESSAGE_EDIT = 5;
    public static final int TYPE_MESSAGES_IN_READ = 6;
    public static final int TYPE_MESSAGES_OUT_READ = 7;

    public static final int TYPE_USER_ONLINE = 8;
    public static final int TYPE_USER_OFFLINE = 9;

    public static final int TYPE_CONVERSATION_FLAGS_CLEAR = 10;
    public static final int TYPE_CONVERSATION_FLAGS_CHANGE = 11;
    public static final int TYPE_CONVERSATION_FLAGS_SET = 12;

    //public static final int TYPE_CONVERSATION_MESSAGES_RANGE_DELETE = 13;
    //public static final int TYPE_CONVERSATION_MESSAGES_RANGE_RECOVER = 14;

    public static final int TYPE_TYPING_MESSAGE = 63;
    public static final int TYPE_RECORDING_VOICE_MESSAGE = 64;

    public static final int TYPE_BADGE_COUNTER_CHANGE = 80;

    private final int type;

    public LongPollEvent(JSONArray jsonArray) throws JSONException {
        type = jsonArray.getInt(0);
    }

    public int getType() {
        return type;
    }
}
