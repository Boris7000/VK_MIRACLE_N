package com.vkontakte.miracle.model.longpoll.conversations;

import org.json.JSONArray;
import org.json.JSONException;

public class MessagesOutReadEvent extends MessagesReadEvent{
    public MessagesOutReadEvent(JSONArray jsonArray) throws JSONException {
        super(jsonArray);
    }
}
