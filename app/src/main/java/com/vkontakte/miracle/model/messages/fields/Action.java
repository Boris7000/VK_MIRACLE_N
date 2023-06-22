package com.vkontakte.miracle.model.messages.fields;

import com.miracle.engine.annotation.MayBeEmpty;
import com.vkontakte.miracle.model.general.JSONable;

import org.json.JSONException;
import org.json.JSONObject;

public class Action implements JSONable {

    private final String type;
    private final String text;
    private final String message;
    private final String memberId;

    public Action(JSONObject jsonObject) throws JSONException {

        type = jsonObject.getString("type");

        text = jsonObject.optString("text");

        message = jsonObject.optString("message");

        memberId = jsonObject.optString("member_id");

    }

    public String getType() {
        return type;
    }

    @MayBeEmpty
    public String getText() {
        return text;
    }

    @MayBeEmpty
    public String getMessage() {
        return message;
    }

    @MayBeEmpty
    public String getMemberId() {
        return memberId;
    }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("type", type);

        if (!text.isEmpty()){
            jsonObject.put("text", text);
        }

        if(!message.isEmpty()){
            jsonObject.put("message", message);
        }

        if(!memberId.isEmpty()){
            jsonObject.put("member_id", memberId);
        }

        return jsonObject;
    }
}
