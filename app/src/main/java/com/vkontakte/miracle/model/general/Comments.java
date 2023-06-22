package com.vkontakte.miracle.model.general;

import static com.vkontakte.miracle.network.vkapi.VKResponseUtil.intAsBoolean;

import org.json.JSONException;
import org.json.JSONObject;

public class Comments {

    private final int count;
    private final boolean canPost;
    private final boolean canView;

    public Comments(JSONObject jsonObject) throws JSONException {
        count = jsonObject.getInt("count");
        canPost = intAsBoolean(jsonObject.optInt("can_post"));
        canView = intAsBoolean(jsonObject.optInt("can_view",1));
    }


    public int getCount() {
        return count;
    }

    public boolean isCanPost() {
        return canPost;
    }

    public boolean isCanView() {
        return canView;
    }
}
