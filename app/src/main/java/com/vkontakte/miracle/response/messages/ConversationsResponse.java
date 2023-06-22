package com.vkontakte.miracle.response.messages;

import static com.vkontakte.miracle.network.vkapi.APIConstants.MESSAGES_FIELDS;
import static com.vkontakte.miracle.network.vkapi.APIMethodsFactory.messages;

import com.vkontakte.miracle.model.general.ExtendedArrays;
import com.vkontakte.miracle.model.messages.ResponseConversationBundle;
import com.vkontakte.miracle.network.vkapi.VKResponseUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class ConversationsResponse {

    private final int count;
    private final List<ResponseConversationBundle> conversationBundles;
    private final ExtendedArrays extendedArrays;

    public ConversationsResponse(int count, List<ResponseConversationBundle> conversationBundles, ExtendedArrays extendedArrays) {
        this.count = count;
        this.conversationBundles = conversationBundles;
        this.extendedArrays = extendedArrays;
    }

    public ConversationsResponse(JSONObject joResponse) throws JSONException {

        count = joResponse.getInt("count");

        extendedArrays = new ExtendedArrays(joResponse);

        JSONArray items = joResponse.getJSONArray("items");
        conversationBundles = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            conversationBundles.add(new ResponseConversationBundle(items.getJSONObject(i)));
        }
    }

    public static ConversationsResponse call(int offset, int count, String startMessageId, String filter) throws Exception {

        Response<JSONObject> response = messages().getConversations(
                offset, count, startMessageId, filter, 1,MESSAGES_FIELDS).execute();

        JSONObject joResponse = VKResponseUtil.validate(response).getJSONObject("response");

        return new ConversationsResponse(joResponse);
    }

    public int getCount() {
        return count;
    }

    public List<ResponseConversationBundle> getConversationBundles() {
        return conversationBundles;
    }

    public ExtendedArrays getExtendedArrays() {
        return extendedArrays;
    }

}
