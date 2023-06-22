package com.vkontakte.miracle.model.longpoll.messages;

import com.vkontakte.miracle.model.longpoll.messages.fields.MessageAttachments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MessageAddEvent extends MessageLongPollEvent {

    private long timestamp;
    private String text;
    private String fromId;
    private MessageAttachments attachments;

    public MessageAddEvent(JSONArray jsonArray) throws JSONException {
        super(jsonArray);
        if(jsonArray.length()>4) timestamp = jsonArray.optLong(4);
        if(jsonArray.length()>5) text = jsonArray.optString(5);
        if(jsonArray.length()>6){
            JSONObject additional = jsonArray.getJSONObject(6);
            fromId = additional.optString("from");
        }
        if(jsonArray.length()>7) attachments = new MessageAttachments(jsonArray.getJSONObject(7));
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getText() {
        return text;
    }

    public String getFromId() {
        return fromId;
    }

    public MessageAttachments getAttachments() {
        return attachments;
    }
}
