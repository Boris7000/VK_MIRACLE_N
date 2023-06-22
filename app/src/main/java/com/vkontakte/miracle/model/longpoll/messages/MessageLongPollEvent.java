package com.vkontakte.miracle.model.longpoll.messages;

import com.vkontakte.miracle.model.longpoll.LongPollEvent;

import org.json.JSONArray;
import org.json.JSONException;

public class MessageLongPollEvent extends LongPollEvent {

    private final String messageId;
    private final int flags;
    private final String peerId;

    public MessageLongPollEvent(JSONArray jsonArray) throws JSONException {
        super(jsonArray);
        messageId = jsonArray.getString(1);
        flags = jsonArray.getInt(2);
        peerId = jsonArray.optString(3);
    }

    public String getMessageId() {
        return messageId;
    }

    public int getFlags() {
        return flags;
    }

    public String getPeerId() {
        return peerId;
    }
}
