package com.vkontakte.miracle.model.messages.fields;

import com.vkontakte.miracle.model.general.JSONable;

import org.json.JSONException;
import org.json.JSONObject;

public class Peer implements JSONable {

    private final String id;
    private final String type;
    private final String localId;

    public Peer(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getString("id");
        type = jsonObject.getString("type");
        localId = jsonObject.getString("local_id");
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getLocalId() {
        return localId;
    }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("type", type);
        jsonObject.put("local_id", localId);
        return jsonObject;
    }
}
