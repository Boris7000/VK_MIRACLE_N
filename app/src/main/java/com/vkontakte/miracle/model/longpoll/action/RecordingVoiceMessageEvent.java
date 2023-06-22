package com.vkontakte.miracle.model.longpoll.action;

import com.vkontakte.miracle.model.longpoll.LongPollEvent;

import org.json.JSONArray;
import org.json.JSONException;

public class RecordingVoiceMessageEvent extends LongPollEvent {

    private final String peerId;
    private final String[] userIds;
    private final int count;
    private final long timestamp;

    public RecordingVoiceMessageEvent(JSONArray jsonArray) throws JSONException {
        super(jsonArray);
        peerId = jsonArray.getString(1);
        JSONArray jaUserIds = jsonArray.getJSONArray(2);
        count = jsonArray.getInt(3);
        userIds = new String[count];
        for (int i=0;i<count;i++){
            userIds[i] = jaUserIds.getString(i);
        }
        timestamp = jsonArray.optLong(4);
    }

    public String getPeerId() {
        return peerId;
    }

    public String[] getUserIds() {
        return userIds;
    }

    public int getCount() {
        return count;
    }

    public long getTimestamp() {
        return timestamp;
    }

}
