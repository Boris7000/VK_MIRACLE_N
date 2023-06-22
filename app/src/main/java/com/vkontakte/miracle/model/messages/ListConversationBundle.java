package com.vkontakte.miracle.model.messages;

import com.miracle.engine.recyclerview.TypedData;
import com.vkontakte.miracle.model.messages.fields.ChatActions;
import com.vkontakte.miracle.util.constants.TypedDataConstants;

import java.util.Objects;

public class ListConversationBundle implements TypedData {

    private final long updateTime;

    private final Conversation conversation;

    private final ChatActions chatActions;

    private final Message lastMessage;

    public ListConversationBundle(long updateTime,
                                  Conversation conversation,
                                  ChatActions chatActions,
                                  Message lastMessage) {
        this.updateTime = updateTime;
        this.conversation = conversation;
        this.chatActions = chatActions;
        this.lastMessage = lastMessage;
    }

    public String getPeerId(){
        return conversation.getPeer().getId();
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

    public Message getLastMessage() {
        return lastMessage;
    }

    @Override
    public int getDataType() {
        return TypedDataConstants.TYPE_CONVERSATION;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListConversationBundle conversationBundle = (ListConversationBundle) o;
        return getPeerId().equals(conversationBundle.getPeerId());
    }

    @Override
    public boolean equalsContent(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ListConversationBundle conversationBundle = (ListConversationBundle) o;
        return updateTime==conversationBundle.updateTime;
    }

    @Override
    public int contentHashCode() {
        return Objects.hashCode(updateTime);
    }
}
