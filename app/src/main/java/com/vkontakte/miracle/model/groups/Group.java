package com.vkontakte.miracle.model.groups;

import static com.vkontakte.miracle.network.vkapi.VKResponseUtil.intAsBoolean;

import androidx.annotation.Nullable;

import com.miracle.engine.recyclerview.TypedData;
import com.vkontakte.miracle.model.general.JSONable;
import com.vkontakte.miracle.model.general.Owner;
import com.vkontakte.miracle.util.constants.TypedDataConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class Group extends Owner implements TypedData, JSONable {

    //base fields
    private final String name;
    private final String activity;
    private final String description;

    //privacy fields
    private final int isClosed;
    private final boolean isAdmin;
    private final int adminLevel;
    private final boolean isMember;

    public Group(JSONObject jsonObject) throws JSONException {
        super(jsonObject, "-");

        name = jsonObject.getString("name");

        isClosed = jsonObject.optInt("is_closed");

        isAdmin = intAsBoolean(jsonObject.optInt("is_admin"));

        adminLevel = jsonObject.optInt("admin_level");

        isMember = intAsBoolean(jsonObject.optInt("is_member"));

        activity = jsonObject.optString("activity");

        description = jsonObject.optString("description");

    }

    @Override
    public String getFullName() {
        return name;
    }

    @Override
    public String getShortName() {
        return name;
    }

    @Override
    public String getNameWithInitials() {
        return name;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public String getActivity() {
        return activity;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public int isClosed() {
        return isClosed;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public int getAdminLevel() {
        return adminLevel;
    }

    public boolean isMember() {
        return isMember;
    }

    @Override
    public int getDataType() {
        return TypedDataConstants.TYPE_GROUP;
    }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = super.toJSONObject();

        jsonObject.put("name", name);

        jsonObject.put("is_closed", isClosed);

        jsonObject.put("is_admin", isAdmin?1:0);

        if(adminLevel!=0){
            jsonObject.put("admin_level", adminLevel);
        }

        jsonObject.put("is_member", isMember?1:0);

        if(!activity.isEmpty()){
            jsonObject.put("activity", activity);
        }

        if(!description.isEmpty()){
            jsonObject.put("description", description);
        }

        return jsonObject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Group group = (Group) o;
        return isClosed == group.isClosed && isAdmin == group.isAdmin && adminLevel == group.adminLevel && isMember == group.isMember && name.equals(group.name) && activity.equals(group.activity) && description.equals(group.description);
    }

    @Override
    public int hashCode() {
        super.hashCode();
        return Objects.hash(name, activity, description, isClosed, isAdmin, adminLevel, isMember);
    }


    //TODO добавить нормальную проверку на эквивалентность контента
}
