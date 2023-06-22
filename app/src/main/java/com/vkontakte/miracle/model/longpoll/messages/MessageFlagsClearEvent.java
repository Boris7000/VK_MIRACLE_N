package com.vkontakte.miracle.model.longpoll.messages;

import org.json.JSONArray;
import org.json.JSONException;

public class MessageFlagsClearEvent extends MessageFlagsEvent{
    public MessageFlagsClearEvent(JSONArray jsonArray) throws JSONException {
        super(jsonArray);
    }

    @Override
    int apply(int flags) {
        return flags&~getFlags();
    }
}
