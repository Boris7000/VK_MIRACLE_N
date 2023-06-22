package com.vkontakte.miracle.model.longpoll.messages;

import org.json.JSONArray;
import org.json.JSONException;

public abstract class MessageFlagsEvent extends MessageLongPollEvent{
    public MessageFlagsEvent(JSONArray jsonArray) throws JSONException {
        super(jsonArray);
    }
    abstract int apply(int flags);
}
