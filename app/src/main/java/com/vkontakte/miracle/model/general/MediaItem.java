package com.vkontakte.miracle.model.general;

import org.json.JSONException;
import org.json.JSONObject;

public class MediaItem {

    private final String id;
    private final String ownerId;
    private final long date;

    public MediaItem(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getString("id");
        ownerId = jsonObject.getString("owner_id");
        date = jsonObject.getLong("date");
    }

    public String getId() {
        return id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public long getDate() {
        return date;
    }
}
