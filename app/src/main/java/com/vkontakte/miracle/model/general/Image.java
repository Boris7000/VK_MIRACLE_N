package com.vkontakte.miracle.model.general;

import org.json.JSONException;
import org.json.JSONObject;

public class Image implements JSONable{
    private final int width;
    private final int height;
    private final String url;

    public Image(JSONObject jsonObject) throws JSONException {
        this.width = jsonObject.getInt("width");
        this.height = jsonObject.getInt("height");
        this.url = jsonObject.getString("url");
    }

    public String getUrl() {
        return url;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("width", width);
        jsonObject.put("height", height);
        jsonObject.put("url", url);
        return jsonObject;
    }
}
