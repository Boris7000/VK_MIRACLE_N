package com.vkontakte.miracle.viewholder.messages;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.recyclerview.viewholder.bundle.ViewHolderBundle;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.model.general.ExtendedArrays;
import com.vkontakte.miracle.model.messages.Conversation;
import com.vkontakte.miracle.model.messages.ListConversationBundle;
import com.vkontakte.miracle.model.wall.Post;
import com.vkontakte.miracle.viewholder.auth.AccountViewHolder;
import com.vkontakte.miracle.viewholder.messages.ConversationViewHolder;
import com.vkontakte.miracle.viewholder.wall.PostViewHolder;

public abstract class ConversationViewHolderBundle extends ViewHolderBundle<RecyclerView.ViewHolder,Object>
                                                   implements ConversationViewHolder.OnConversationActionsListener{
    @Override
    public int getLayoutResourceId() {
        return R.layout.conversations_ai_conversation;
    }

    @Override
    public RecyclerView.ViewHolder create(View view) {
        return new ConversationViewHolder(view);
    }

    @Override
    public void bind(RecyclerView.ViewHolder viewHolder, Object data) {
        if(viewHolder instanceof ConversationViewHolder && data instanceof ListConversationBundle){
            ConversationViewHolder conversationViewHolder = (ConversationViewHolder) viewHolder;
            ListConversationBundle conversation = (ListConversationBundle) data;
            conversationViewHolder.bind(conversation, requestExtendedArrays());
            conversationViewHolder.setOnConversationActionsListener(this);
        }
    }

    //-------------------------------------------------------------------------//

    @Override
    public void onClick(ListConversationBundle listConversationBundle) {

    }

    @Override
    public void onLongClick(ListConversationBundle listConversationBundle) {

    }

    //-------------------------------------------------------------------------//

    public abstract ExtendedArrays requestExtendedArrays();
}
