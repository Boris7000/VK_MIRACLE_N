package com.vkontakte.miracle.model.longpoll.conversations;

import org.json.JSONArray;
import org.json.JSONException;

public class MessagesInReadEvent extends MessagesReadEvent{
    public MessagesInReadEvent(JSONArray jsonArray) throws JSONException {
        super(jsonArray);
    }
}
