package com.vkontakte.miracle.model.general;

import org.json.JSONException;
import org.json.JSONObject;

public class Gift {

    private final String id;
    private final String thumb48;
    private final String thumb96;
    private final String thumb256;

    private final String stickersProductId;

    public Gift(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getString("id");
        thumb48 = jsonObject.optString("thumb_48");
        thumb96 = jsonObject.optString("thumb_96");
        thumb256 = jsonObject.optString("thumb_256");

        stickersProductId = jsonObject.optString("stickers_product_id");
    }

    public String getId() {
        return id;
    }

    public String getThumb48() {
        return thumb48;
    }

    public String getThumb96() {
        return thumb96;
    }

    public String getThumb256() {
        return thumb256;
    }

    public String getStickersProductId() {
        return stickersProductId;
    }
}
