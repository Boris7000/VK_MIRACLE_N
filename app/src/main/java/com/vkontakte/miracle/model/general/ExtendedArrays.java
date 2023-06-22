package com.vkontakte.miracle.model.general;

import android.util.ArrayMap;

import androidx.annotation.Nullable;

import com.miracle.engine.util.StringsUtil;
import com.vkontakte.miracle.model.groups.Group;
import com.vkontakte.miracle.model.users.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class ExtendedArrays {

    private final ArrayMap<String, Profile> profiles;
    private final ArrayMap<String, Group> groups;

    public ExtendedArrays(){
        profiles = new ArrayMap<>();
        groups = new ArrayMap<>();
    }

    public ExtendedArrays(JSONObject response) throws JSONException {
        if(response.has("profiles")) {
            profiles = parseProfilesArrayMap(response.getJSONArray("profiles"));
        } else {
            profiles = new ArrayMap<>();
        }

        if(response.has("groups")) {
            groups = parseGroupsArrayMap(response.getJSONArray("groups"));
        } else {
            groups = new ArrayMap<>();
        }
    }

    public ArrayMap<String, Profile> getProfiles() {
        return profiles;
    }

    public ArrayMap<String, Group> getGroups() {
        return groups;
    }

    //------------------------------------------//

    @Nullable
    public Owner findOwnerById(@Nullable String id){
        if(StringsUtil.nonNullAndNonEmpty(id)) {
            if (id.charAt(0) == '-') {
                return groups.get(id);
            } else {
                return profiles.get(id);
            }
        }
        return null;
    }

    //------------------------------------------//

    public void merge(ExtendedArrays extendedArrays){
        profiles.putAll(extendedArrays.profiles);
        groups.putAll(extendedArrays.groups);
    }

    public void clear(){
        profiles.clear();
        groups.clear();
    }

    //------------------------------------------//

    public static ArrayMap<String, Profile> parseProfilesArrayMap(JSONArray profiles) throws JSONException {
        ArrayMap<String,Profile> map = new ArrayMap<>();
        for(int i=0;i < profiles.length(); i++){
            Profile profile = new Profile(profiles.getJSONObject(i));
            map.put(profile.getId(), profile);
        }
        return map;
    }

    public static ArrayMap<String, Group> parseGroupsArrayMap(JSONArray groups) throws JSONException {
        ArrayMap<String,Group> map = new ArrayMap<>();
        for(int i=0;i < groups.length(); i++){
            Group group = new Group(groups.getJSONObject(i));
            map.put(group.getId(), group);
        }
        return map;
    }

    public JSONArray toJSONArrayProfiles() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (Map.Entry<String, Profile> entry: profiles.entrySet()) {
            jsonArray.put(entry.getValue().toJSONObject());
        }
        return jsonArray;
    }

    public JSONArray toJSONArrayGroups() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (Map.Entry<String, Group> entry: groups.entrySet()) {
            jsonArray.put(entry.getValue().toJSONObject());
        }
        return jsonArray;
    }
}
