package com.vkontakte.miracle.viewholder.messages.chat.message;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.recyclerview.viewholder.bundle.ViewHolderBundle;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.model.general.ExtendedArrays;
import com.vkontakte.miracle.model.messages.Conversation;
import com.vkontakte.miracle.model.messages.Message;

public abstract class MessageInViewHolderBundle extends ViewHolderBundle<RecyclerView.ViewHolder,Object> {
    @Override
    public int getLayoutResourceId() {
        return R.layout.chat_ai_message_in;
    }

    @Override
    public RecyclerView.ViewHolder create(View view) {
        return new MessageInViewHolder(view);
    }

    @Override
    public void bind(RecyclerView.ViewHolder viewHolder, Object data) {
        if(viewHolder instanceof MessageInViewHolder && data instanceof Message){
            MessageInViewHolder messageViewHolder = (MessageInViewHolder) viewHolder;
            Message message = (Message) data;
            messageViewHolder.bind(message, requestConversation(), requestExtendedArrays());
        }
    }

    public abstract ExtendedArrays requestExtendedArrays();

    public abstract Conversation requestConversation();
}
