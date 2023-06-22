package com.vkontakte.miracle.response.messages.local;

import com.vkontakte.miracle.model.general.ExtendedArrays;
import com.vkontakte.miracle.model.messages.ListConversationBundle;

import java.util.List;

public class ListConversationsResponse {

    private final int count;
    private final List<ListConversationBundle> conversationBundles;
    private final ExtendedArrays extendedArrays;


    public ListConversationsResponse(int count,
                                     List<ListConversationBundle> conversationBundles,
                                     ExtendedArrays extendedArrays) {
        this.count = count;
        this.conversationBundles = conversationBundles;
        this.extendedArrays = extendedArrays;
    }

    public int getCount() {
        return count;
    }

    public List<ListConversationBundle> getConversationBundles() {
        return conversationBundles;
    }

    public ExtendedArrays getExtendedArrays() {
        return extendedArrays;
    }
}
