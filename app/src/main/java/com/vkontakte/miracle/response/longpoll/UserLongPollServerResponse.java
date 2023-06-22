package com.vkontakte.miracle.response.longpoll;

import static com.vkontakte.miracle.network.vkapi.APIMethodsFactory.messages;
import static com.vkontakte.miracle.network.vkapi.VKResponseUtil.getJSONStringIfHas;

import com.vkontakte.miracle.memory.storage.UsersStorage;
import com.vkontakte.miracle.model.general.JSONable;
import com.vkontakte.miracle.network.vkapi.VKResponseUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import retrofit2.Response;

public class UserLongPollServerResponse implements JSONable {

    private final String server;
    private final String key;
    private final long ts;

    public UserLongPollServerResponse(String server, String key, long ts) {
        this.server = server;
        this.key = key;
        this.ts = ts;
    }

    public UserLongPollServerResponse(JSONObject jsonObject) throws JSONException {
        server = getJSONStringIfHas(jsonObject,"server",null);
        key = getJSONStringIfHas(jsonObject,"key", null);
        ts = jsonObject.optLong("ts");
    }

    public static UserLongPollServerResponse call(int needPts, int lpVersion) throws Exception {

        Response<JSONObject> response = messages().getLongPollServer(needPts, lpVersion).execute();

        JSONObject joResponse = VKResponseUtil.validate(response).getJSONObject("response");

        return new UserLongPollServerResponse(joResponse);
    }

    public static UserLongPollServerResponse callFromMemory(File currentUserCaches) throws Exception {
        UsersStorage usersStorage = UsersStorage.get();
        return usersStorage.loadUserLongPollServerCache(currentUserCaches);
    }

    public String getServer() {
        return server;
    }

    public String getKey() {
        return key;
    }

    public long getTs() {
        return ts;
    }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("server",server);
        jsonObject.put("key",key);
        jsonObject.put("ts",ts);
        return jsonObject;
    }
}
