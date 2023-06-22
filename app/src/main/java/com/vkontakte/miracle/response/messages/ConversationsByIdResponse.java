package com.vkontakte.miracle.response.messages;

import static com.vkontakte.miracle.network.vkapi.APIConstants.MESSAGES_FIELDS;
import static com.vkontakte.miracle.network.vkapi.APIMethodsFactory.messages;

import android.util.ArrayMap;

import com.vkontakte.miracle.model.general.ExtendedArrays;
import com.vkontakte.miracle.model.messages.Conversation;
import com.vkontakte.miracle.model.messages.Message;
import com.vkontakte.miracle.model.messages.ResponseConversationBundle;
import com.vkontakte.miracle.network.vkapi.VKResponseUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class ConversationsByIdResponse {

    private final List<ResponseConversationBundle> conversationBundles;
    private final ExtendedArrays extendedArrays;

    public ConversationsByIdResponse(List<ResponseConversationBundle> conversationBundles, ExtendedArrays extendedArrays) {
        this.conversationBundles = conversationBundles;
        this.extendedArrays = extendedArrays;
    }

    public static ConversationsByIdResponse callWithMessages(String peerIds) throws Exception {

        Response<JSONObject> response = messages().getConversationsById(
                peerIds, 1, MESSAGES_FIELDS).execute();

        JSONObject joResponse = VKResponseUtil.validate(response).getJSONObject("response");

        ExtendedArrays extendedArrays = new ExtendedArrays(joResponse);

        List<String> messageIds = new ArrayList<>();
        JSONArray items = joResponse.getJSONArray("items");
        ArrayMap<String, Conversation> conversationsMap = new ArrayMap<>();
        for (int i = 0; i < items.length(); i++) {
            Conversation conversation = new Conversation(items.getJSONObject(i));
            messageIds.add(conversation.getLastMessageId());
            conversationsMap.put(conversation.getPeer().getId(),conversation);
        }

        List<ResponseConversationBundle> conversationBundles = new ArrayList<>();

        if(!messageIds.isEmpty()) {
            MessagesByIdResponse messagesByIdResponse = MessagesByIdResponse.call(String.join(",", messageIds));
            extendedArrays.merge(messagesByIdResponse.getExtendedArrays());

            for (Message message : messagesByIdResponse.getMessages()) {
                Conversation conversation = conversationsMap.get(message.getPeerId());
                if(conversation!=null){
                    conversationBundles.add(new ResponseConversationBundle(conversation, message));
                }
            }
        }

        return new ConversationsByIdResponse(conversationBundles, extendedArrays);
    }

    public List<ResponseConversationBundle> getConversationBundles() {
        return conversationBundles;
    }

    public ExtendedArrays getExtendedArrays() {
        return extendedArrays;
    }
}
