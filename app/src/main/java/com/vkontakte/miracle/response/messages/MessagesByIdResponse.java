package com.vkontakte.miracle.response.messages;

import static com.vkontakte.miracle.network.vkapi.APIConstants.MESSAGES_FIELDS;
import static com.vkontakte.miracle.network.vkapi.APIMethodsFactory.messages;

import android.util.Log;

import com.vkontakte.miracle.model.general.ExtendedArrays;
import com.vkontakte.miracle.model.messages.Message;
import com.vkontakte.miracle.network.vkapi.VKResponseUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class MessagesByIdResponse {

    private final List<Message> messages;
    private final ExtendedArrays extendedArrays;

    public MessagesByIdResponse(List<Message> messages, ExtendedArrays extendedArrays) {
        this.messages = messages;
        this.extendedArrays = extendedArrays;
    }

    public static MessagesByIdResponse call(String messagesIds) throws Exception {

        Response<JSONObject> response = messages().getById(
                messagesIds, 1, MESSAGES_FIELDS).execute();

        JSONObject joResponse = VKResponseUtil.validate(response).getJSONObject("response");

        ExtendedArrays extendedArrays = new ExtendedArrays(joResponse);

        JSONArray items = joResponse.getJSONArray("items");
        List<Message> messages = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            messages.add(new Message(items.getJSONObject(i)));
        }

        return new MessagesByIdResponse(messages, extendedArrays);
    }


    public List<Message> getMessages() {
        return messages;
    }

    public ExtendedArrays getExtendedArrays() {
        return extendedArrays;
    }

}
