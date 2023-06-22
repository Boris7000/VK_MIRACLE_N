package com.vkontakte.miracle.model.longpoll.messages;

import org.json.JSONArray;
import org.json.JSONException;

public class MessageFlagsSetEvent extends MessageFlagsEvent{
    public MessageFlagsSetEvent(JSONArray jsonArray) throws JSONException {
        super(jsonArray);
    }

    @Override
    int apply(int flags) {
        return flags|getFlags();
    }
}
