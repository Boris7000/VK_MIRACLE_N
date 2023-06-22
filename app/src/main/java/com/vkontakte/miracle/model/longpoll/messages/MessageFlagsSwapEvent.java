package com.vkontakte.miracle.model.longpoll.messages;

import org.json.JSONArray;
import org.json.JSONException;

public class MessageFlagsSwapEvent extends MessageFlagsEvent{
    public MessageFlagsSwapEvent(JSONArray jsonArray) throws JSONException {
        super(jsonArray);
    }

    @Override
    int apply(int flags) {
        return getFlags();
    }
}
