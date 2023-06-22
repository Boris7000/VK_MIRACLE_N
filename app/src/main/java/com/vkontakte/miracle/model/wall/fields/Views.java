package com.vkontakte.miracle.model.wall.fields;

import org.json.JSONException;
import org.json.JSONObject;

public class Views {

    private final int count;

    public Views(JSONObject jsonObject) throws JSONException {
        count = jsonObject.getInt("count");
    }

    public int getCount() {
        return count;
    }
}
