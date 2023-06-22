package com.vkontakte.miracle.model.messages;

import org.json.JSONException;
import org.json.JSONObject;

public class ResponseConversationBundle {

    private final Conversation conversation;

    private final Message lastMessage;

    public ResponseConversationBundle(Conversation conversation, Message lastMessage) {
        this.conversation = conversation;
        this.lastMessage = lastMessage;
    }

    public ResponseConversationBundle(JSONObject joResponse) throws JSONException {
        conversation = new Conversation(joResponse.getJSONObject("conversation"));
        lastMessage = new Message(joResponse.getJSONObject("last_message"));
    }

    public String getPeerId(){
        return conversation.getPeer().getId();
    }

    public Conversation getConversation() {
        return conversation;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

}
