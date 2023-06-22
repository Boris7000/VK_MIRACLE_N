package com.vkontakte.miracle.model.general;

import static com.vkontakte.miracle.network.vkapi.VKResponseUtil.intAsBoolean;

import org.json.JSONException;
import org.json.JSONObject;

public class Likes {

    private int count;
    private final boolean canLike;
    private boolean userLikes;

    private final boolean canPublish;
    private final boolean repostDisabled;

    public Likes(JSONObject jsonObject) throws JSONException {
        count = jsonObject.getInt("count");
        canLike = intAsBoolean(jsonObject.optInt("can_like"));
        userLikes = intAsBoolean(jsonObject.optInt("user_likes"));
        canPublish = intAsBoolean(jsonObject.optInt("can_publish"));
        repostDisabled = jsonObject.getBoolean("repost_disabled");
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isCanLike() {
        return canLike;
    }

    public boolean isUserLikes() {
        return userLikes;
    }

    public void setUserLikes(boolean userLikes) {
        this.userLikes = userLikes;
    }

    public boolean isCanPublish() {
        return canPublish;
    }

    public boolean isRepostDisabled() {
        return repostDisabled;
    }
}
