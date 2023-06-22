package com.vkontakte.miracle.response.messages.local;

import android.util.ArrayMap;

import com.vkontakte.miracle.memory.storage.UsersStorage;
import com.vkontakte.miracle.model.general.ExtendedArrays;
import com.vkontakte.miracle.model.general.JSONable;
import com.vkontakte.miracle.model.messages.ConversationBundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CachedConversationsResponse implements JSONable {

    private final int count;
    private final Map<String, ConversationBundle> conversationBundleMap;
    private final List<ConversationBundle> conversationBundles;
    private final ExtendedArrays extendedArrays;

    public CachedConversationsResponse(int count,
                                       Map<String, ConversationBundle> conversationBundleMap,
                                       List<ConversationBundle> conversationBundles,
                                       ExtendedArrays extendedArrays) {
        this.count = count;
        this.conversationBundleMap = conversationBundleMap;
        this.conversationBundles = conversationBundles;
        this.extendedArrays = extendedArrays;
    }

    public CachedConversationsResponse(JSONObject joResponse) throws JSONException {

        count = joResponse.optInt("count", -1);

        conversationBundleMap = new ArrayMap<>();

        conversationBundles = new ArrayList<>();

        extendedArrays = new ExtendedArrays(joResponse);

        JSONArray jaConversations = joResponse.getJSONArray("items");
        for (int i = 0; i < jaConversations.length(); i++) {
            ConversationBundle conversationBundle = new ConversationBundle(jaConversations.getJSONObject(i));
            conversationBundleMap.put(conversationBundle.getPeerId(), conversationBundle);
        }

        JSONArray jaListIds = joResponse.getJSONArray("list_items_ids");
        for (int i = 0; i < jaListIds.length(); i++) {
            ConversationBundle conversationBundle = conversationBundleMap.get(jaListIds.getString(i));
            conversationBundles.add(conversationBundle);
        }
    }

    public static CachedConversationsResponse callFromMemory(File currentUserCaches) throws Exception {
        UsersStorage usersStorage = UsersStorage.get();
        return usersStorage.loadConversationsCache(currentUserCaches);
    }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        if(count>=0){
            jsonObject.put("count",count);
        }

        JSONArray jaListIds = new JSONArray();
        for (ConversationBundle conversationBundle: conversationBundles) {
            jaListIds.put(conversationBundle.getPeerId());
        }
        jsonObject.put("list_items_ids", jaListIds);


        JSONArray jaConversations = new JSONArray();
        for (Map.Entry<String,ConversationBundle> entry: conversationBundleMap.entrySet()) {
            ConversationBundle conversationBundle = entry.getValue();
            jaConversations.put(conversationBundle.toJSONObject());
        }
        jsonObject.put("items", jaConversations);


        jsonObject.put("profiles", extendedArrays.toJSONArrayProfiles());
        jsonObject.put("groups", extendedArrays.toJSONArrayGroups());
        return jsonObject;
    }

    public int getCount() {
        return count;
    }

    public Map<String, ConversationBundle> getConversationBundleMap() {
        return conversationBundleMap;
    }

    public List<ConversationBundle> getConversationBundles() {
        return conversationBundles;
    }

    public ExtendedArrays getExtendedArrays() {
        return extendedArrays;
    }
}
