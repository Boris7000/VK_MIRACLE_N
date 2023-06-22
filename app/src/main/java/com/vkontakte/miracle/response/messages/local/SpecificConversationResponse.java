package com.vkontakte.miracle.response.messages.local;

import com.vkontakte.miracle.model.general.ExtendedArrays;
import com.vkontakte.miracle.model.messages.Conversation;
import com.vkontakte.miracle.model.messages.Message;
import com.vkontakte.miracle.model.messages.fields.ChatActions;

import java.util.ArrayList;
import java.util.List;

public class SpecificConversationResponse {

    private final long updateTime;
    private final Conversation conversation;
    private final ChatActions chatActions;
    private final int count;
    private final List<Message> messages;
    private final ExtendedArrays extendedArrays;

    public SpecificConversationResponse(long updateTime,
                                        Conversation conversation,
                                        ChatActions chatActions,
                                        int count,
                                        List<Message> messages,
                                        ExtendedArrays extendedArrays) {
        this.updateTime = updateTime;
        this.conversation = conversation;
        this.chatActions = chatActions;
        this.count = count;
        this.messages = messages;
        this.extendedArrays = extendedArrays;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public ChatActions getChatAction() {
        return chatActions;
    }

    public int getCount() {
        return count;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public ExtendedArrays getExtendedArrays() {
        return extendedArrays;
    }
}
