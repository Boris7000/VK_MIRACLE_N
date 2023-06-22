package com.vkontakte.miracle.model.general;

import static com.vkontakte.miracle.network.vkapi.VKResponseUtil.intAsBoolean;

import androidx.annotation.Nullable;

import com.miracle.engine.annotation.MayBeEmpty;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Owner implements JSONable{

    //base fields
    private final String id;
    private final String screenName;
    private final String photo100;
    private final String photo200;
    private final boolean verified;
    private final String status;
    private final Cover cover;

    //privacy fields
    private final String deactivated;

    public Owner(JSONObject jsonObject) throws JSONException {
        this(jsonObject, null);
    }

    public Owner(JSONObject jsonObject, String idPrefix) throws JSONException {

        String id = jsonObject.getString("id");
        if(idPrefix!=null&&!id.startsWith(idPrefix)){
            this.id = idPrefix + id;
        } else {
            this.id = id;
        }

        screenName = jsonObject.optString("screen_name", id);

        photo100 = jsonObject.optString("photo_100");

        photo200 = jsonObject.optString("photo_200");

        verified = intAsBoolean(jsonObject.optInt("verified"));

        status = jsonObject.optString("status");

        if(jsonObject.has("cover")){
            cover = new Cover(jsonObject.getJSONObject("cover"));
        } else {
            cover = null;
        }

        deactivated = jsonObject.optString("deactivated");

    }

    public String getId() {
        return id;
    }

    public String getScreenName() {
        return screenName;
    }

    public boolean isVerified() {
        return verified;
    }

    public abstract String getFullName();

    public abstract String getShortName();

    public abstract String getNameWithInitials();

    @MayBeEmpty
    public String getPhoto100() {
        return photo100;
    }

    @MayBeEmpty
    public String getPhoto200() {
        return photo200;
    }

    @MayBeEmpty
    public String getStatus() {
        return status;
    }

    @Nullable
    public Cover getCover() {
        return cover;
    }

    @MayBeEmpty
    public String getDeactivated() {
        return deactivated;
    }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("screen_name", screenName);

        if(!photo100.isEmpty()){
            jsonObject.put("photo_100", photo100);
        }

        if(!photo200.isEmpty()){
            jsonObject.put("photo_200", photo200);
        }

        jsonObject.put("verified", verified?1:0);

        if(!status.isEmpty()){
            jsonObject.put("status", status);
        }

        if(cover!=null){
            jsonObject.put("cover", cover.toJSONObject());
        }

        if(!deactivated.isEmpty()){
            jsonObject.put("deactivated", deactivated);
        }

        return jsonObject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Owner owner = (Owner) o;
        return id.equals(owner.id);
    }
}
