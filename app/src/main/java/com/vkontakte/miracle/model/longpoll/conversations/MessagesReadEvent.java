package com.vkontakte.miracle.model.longpoll.conversations;

import com.vkontakte.miracle.model.longpoll.LongPollEvent;

import org.json.JSONArray;
import org.json.JSONException;

public class MessagesReadEvent extends LongPollEvent {

    private final String peerId;
    private final String localId;
    private final int unreadCount;

    public MessagesReadEvent(JSONArray jsonArray) throws JSONException {
        super(jsonArray);
        peerId = jsonArray.getString(1);
        localId = jsonArray.getString(2);
        unreadCount = jsonArray.getInt(3);
    }

    public String getPeerId() {
        return peerId;
    }

    public String getLocalId() {
        return localId;
    }

    public int getUnreadCount() {
        return unreadCount;
    }
}
