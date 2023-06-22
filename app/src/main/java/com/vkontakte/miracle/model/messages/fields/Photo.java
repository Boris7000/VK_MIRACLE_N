package com.vkontakte.miracle.model.messages.fields;

import com.vkontakte.miracle.model.general.JSONable;

import org.json.JSONException;
import org.json.JSONObject;

public class Photo implements JSONable {

    private final String photo100;
    private final String photo200;

    public Photo(JSONObject jsonObject) throws JSONException {
        photo100 = jsonObject.getString("photo_100");
        photo200 = jsonObject.getString("photo_200");
    }

    public String getPhoto100() {
        return photo100;
    }

    public String getPhoto200() {
        return photo200;
    }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("photo_100", photo100);
        jsonObject.put("photo_200", photo200);
        return jsonObject;
    }
}
