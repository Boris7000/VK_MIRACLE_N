package com.vkontakte.miracle.response.users;

import static com.vkontakte.miracle.network.vkapi.APIMethodsFactory.users;

import com.vkontakte.miracle.model.auth.User;
import com.vkontakte.miracle.network.vkapi.VKResponseUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Response;

public class UserResponse {

    private final User user;

    private UserResponse(User user){
        this.user = user;
    }

    public static UserResponse call(String userId, String accessToken) throws Exception {
        Response<JSONObject> response = users().get(userId,
                "photo_200,photo_100,online,last_seen",
                accessToken).execute();

        JSONArray jsonArray = VKResponseUtil.validate(response).getJSONArray("response");

        User user = new User(jsonArray.getJSONObject(0));
        user.setAccessToken(accessToken);
        return new UserResponse(user);
    }

    public User getUser() {
        return user;
    }
}
