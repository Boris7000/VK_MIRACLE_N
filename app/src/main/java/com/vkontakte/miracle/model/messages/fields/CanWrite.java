package com.vkontakte.miracle.model.messages.fields;

import com.vkontakte.miracle.model.general.JSONable;

import org.json.JSONException;
import org.json.JSONObject;

public class CanWrite implements JSONable {

    private final boolean allowed;
    private final int reason;

    public CanWrite(JSONObject jsonObject) throws JSONException {
        allowed = jsonObject.getBoolean("allowed");
        if(!allowed) {
            reason = jsonObject.getInt("reason");
        } else {
            reason = 0;
        }
    }

    public boolean isAllowed() {
        return allowed;
    }

    public int getReason() {
        return reason;
    }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("allowed", allowed);
        if(reason!=0){
            jsonObject.put("reason", reason);
        }
        return jsonObject;
    }
}
