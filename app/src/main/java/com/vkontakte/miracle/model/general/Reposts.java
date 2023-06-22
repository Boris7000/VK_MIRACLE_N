package com.vkontakte.miracle.model.general;

import static com.vkontakte.miracle.network.vkapi.VKResponseUtil.intAsBoolean;

import org.json.JSONException;
import org.json.JSONObject;

public class Reposts {

    private final int count;
    private boolean userReposted;

    public Reposts(JSONObject jsonObject) throws JSONException {
        count = jsonObject.getInt("count");
        userReposted = intAsBoolean(jsonObject.optInt("user_reposted"));
    }

    public int getCount() {
        return count;
    }

    public boolean isUserReposted() {
        return userReposted;
    }

    public void setUserReposted(boolean userReposted) {
        this.userReposted = userReposted;
    }
}
