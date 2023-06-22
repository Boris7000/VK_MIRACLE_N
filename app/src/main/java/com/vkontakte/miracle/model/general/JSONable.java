package com.vkontakte.miracle.model.general;

import org.json.JSONException;
import org.json.JSONObject;

public interface JSONable {
    JSONObject toJSONObject() throws JSONException;
}
