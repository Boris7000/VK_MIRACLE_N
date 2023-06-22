package com.vkontakte.miracle.model.users.fileds;

import com.vkontakte.miracle.model.general.JSONable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class LastSeen implements JSONable {

    private final long time;
    private final int platform;

    public LastSeen(JSONObject jsonObject) throws JSONException {
        time = jsonObject.getLong("time");
        platform = jsonObject.optInt("platform");
    }

    public int getPlatform() {
        return platform;
    }

    public long getTime() {
        return time;
    }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("time",time);
        jsonObject.put("platform", platform);
        return jsonObject;
    }

}