package com.vkontakte.miracle.model.auth;

import static com.vkontakte.miracle.network.vkapi.VKResponseUtil.intAsBoolean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.miracle.engine.annotation.MayBeEmpty;
import com.miracle.engine.annotation.NotEmpty;
import com.miracle.engine.recyclerview.TypedData;
import com.vkontakte.miracle.model.general.JSONable;
import com.vkontakte.miracle.model.users.fileds.LastSeen;
import com.vkontakte.miracle.util.constants.TypedDataConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class User implements JSONable, TypedData {

    private final String id;

    private String accessToken = "";

    private String firstName = "";

    private String lastName = "";

    private String photo100;

    private String photo200;

    private LastSeen lastSeen;

    private boolean online;

    public User(JSONObject jsonObject) throws JSONException {

        id = jsonObject.getString("id");

        accessToken = jsonObject.optString("access_token");

        firstName = jsonObject.getString("first_name");

        lastName = jsonObject.getString("last_name");

        photo100 = jsonObject.optString("photo_100");

        photo200 = jsonObject.optString("photo_200");

        online = intAsBoolean(jsonObject.optInt("online"));

        if(jsonObject.has("last_seen")){
            lastSeen = new LastSeen(jsonObject.getJSONObject("last_seen"));
        }
    }

    @NonNull
    @NotEmpty
    public String getId() {
        return id;
    }

    @NonNull
    @MayBeEmpty
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(@NonNull String accessToken) {
        this.accessToken = accessToken;
    }

    @NonNull
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NonNull String firstName) {
        this.firstName = firstName;
    }

    @NonNull
    @MayBeEmpty
    public String getLastName() {
        return lastName;
    }

    public void setLastName(@NonNull String lastName) {
        this.lastName = lastName;
    }

    @NonNull
    public String getFullName(){
        if(lastName.isEmpty()){
            return firstName;
        } else {
            return firstName +" "+ lastName;
        }
    }

    @NonNull
    @MayBeEmpty
    public String getPhoto100() {
        return photo100;
    }

    public void setPhoto100(@NonNull String photo100) {
        this.photo100 = photo100;
    }

    @NonNull
    @MayBeEmpty
    public String getPhoto200() {
        return photo200;
    }

    public void setPhoto200(@NonNull String photo200) {
        this.photo200 = photo200;
    }

    @Nullable
    public LastSeen getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(LastSeen lastSeen) {
        this.lastSeen = lastSeen;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("id",id);
        jsonObject.put("access_token",accessToken);

        jsonObject.put("first_name",firstName);
        jsonObject.put("last_name",lastName);

        if(!photo100.isEmpty()){
            jsonObject.put("photo_100", photo100);
        }

        if(!photo200.isEmpty()){
            jsonObject.put("photo_200", photo200);
        }

        jsonObject.put("online",online?1:0);

        if(lastSeen!=null){
            jsonObject.put("last_seen", lastSeen.toJSONObject());
        }

        return jsonObject;
    }

    @Override
    public int getDataType() {
        return TypedDataConstants.TYPE_USER;
    }
}
