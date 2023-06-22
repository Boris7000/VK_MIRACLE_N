package com.vkontakte.miracle.model.users;

import static com.vkontakte.miracle.network.vkapi.VKResponseUtil.intAsBoolean;

import androidx.annotation.Nullable;

import com.miracle.engine.recyclerview.TypedData;
import com.vkontakte.miracle.model.general.JSONable;
import com.vkontakte.miracle.model.general.Owner;
import com.vkontakte.miracle.model.users.fileds.Acc;
import com.vkontakte.miracle.model.users.fileds.LastSeen;
import com.vkontakte.miracle.util.constants.TypedDataConstants;

import org.json.JSONException;
import org.json.JSONObject;

public class Profile extends Owner implements TypedData, JSONable {

    //base fields
    private final String firstName;
    private final String lastName;
    private final String shortName;
    private final String fullName;
    private final String nameWithInitials;
    private final boolean online;
    private final LastSeen lastSeen;

    //additional fields
    private final Acc acc;

    //privacy fields
    private final boolean isClosed;
    private final boolean canAccessClosed;

    //personality fields
    private final int sex;

    public Profile(JSONObject jsonObject) throws JSONException {
        super(jsonObject);

        firstName = jsonObject.getString("first_name");
        lastName = jsonObject.getString("last_name");

        if(!lastName.isEmpty()) {
            shortName = firstName;
            fullName = String.format("%s %s", firstName, lastName);
            nameWithInitials = String.format("%s %c.", firstName, lastName.charAt(0));
        } else {
            shortName = firstName;
            fullName = firstName;
            nameWithInitials = firstName;
        }

        acc = new Acc(jsonObject);

        online = intAsBoolean(jsonObject.optInt("online"));

        if(jsonObject.has("last_seen")){
            lastSeen = new LastSeen(jsonObject.getJSONObject("last_seen"));
        } else {
            lastSeen = null;
        }

        isClosed = jsonObject.optBoolean("is_closed");

        canAccessClosed = jsonObject.optBoolean("can_access_closed");

        sex = jsonObject.optInt("sex");

    }

    @Override
    public String getShortName() {
        return shortName;
    }

    @Override
    public String getFullName() {
        return fullName;
    }

    @Override
    public String getNameWithInitials() {
        return nameWithInitials;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Acc getAcc() {
        return acc;
    }

    public boolean isOnline() {
        return online;
    }

    @Nullable
    public LastSeen getLastSeen() {
        return lastSeen;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public boolean isCanAccessClosed() {
        return canAccessClosed;
    }

    public int getSex() {
        return sex;
    }

    @Override
    public int getDataType() {
        return TypedDataConstants.TYPE_PROFILE;
    }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = super.toJSONObject();

        jsonObject.put("first_name", firstName);
        jsonObject.put("last_name", lastName);

        if(!acc.getFirstName().isEmpty()){
            jsonObject.put("first_name_acc", acc.getFirstName());
        }

        if(!acc.getLastName().isEmpty()){
            jsonObject.put("last_name_acc", acc.getLastName());
        }

        jsonObject.put("online", online?1:0);

        if(lastSeen!=null){
            jsonObject.put("last_seen", lastSeen.toJSONObject());
        }

        jsonObject.put("is_closed", isClosed);

        jsonObject.put("can_access_closed", canAccessClosed);

        if(sex!=0){
            jsonObject.put("sex", sex);
        }

        return jsonObject;
    }

    //TODO добавить нормальную проверку на эквивалентность контента
}
