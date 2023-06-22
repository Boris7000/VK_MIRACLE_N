package com.vkontakte.miracle.response.messages;

import static com.vkontakte.miracle.network.vkapi.APIConstants.MESSAGES_FIELDS;
import static com.vkontakte.miracle.network.vkapi.APIMethodsFactory.messages;

import com.vkontakte.miracle.model.general.ExtendedArrays;
import com.vkontakte.miracle.model.messages.Conversation;
import com.vkontakte.miracle.model.messages.Message;
import com.vkontakte.miracle.network.vkapi.VKResponseUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class MessagesHistoryResponse {

    private final int count;
    private final List<Message> messages;
    private final Conversation conversation;
    private final ExtendedArrays extendedArrays;

    public MessagesHistoryResponse(JSONObject joResponse) throws JSONException {

        count = joResponse.getInt("count");

        extendedArrays = new ExtendedArrays(joResponse);

        JSONArray items = joResponse.getJSONArray("items");
        messages = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            messages.add(new Message(items.getJSONObject(i)));
        }

        if(joResponse.has("conversations")) {
            JSONArray conversations = joResponse.getJSONArray("conversations");
            conversation = new Conversation(conversations.getJSONObject(0));
        } else {
            conversation = null;
        }
    }

    public static MessagesHistoryResponse call(String peerId, int offset, int count, String startMessageId) throws Exception {

        Response<JSONObject> response = messages().getHistory(
                peerId, offset, count, startMessageId, 1,MESSAGES_FIELDS).execute();

        JSONObject joResponse = VKResponseUtil.validate(response).getJSONObject("response");

        return new MessagesHistoryResponse(joResponse);
    }

    public int getCount() {
        return count;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public ExtendedArrays getExtendedArrays() {
        return extendedArrays;
    }
}
