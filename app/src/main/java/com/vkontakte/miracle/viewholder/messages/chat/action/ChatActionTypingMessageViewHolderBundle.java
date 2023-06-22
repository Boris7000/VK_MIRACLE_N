package com.vkontakte.miracle.viewholder.messages.chat.action;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.recyclerview.viewholder.bundle.ViewHolderBundle;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.model.general.ExtendedArrays;
import com.vkontakte.miracle.model.messages.Message;

public abstract class ChatActionTypingMessageViewHolderBundle extends ViewHolderBundle<RecyclerView.ViewHolder,Object> {
    @Override
    public int getLayoutResourceId() {
        return R.layout.chat_ai_action_typing_message;
    }

    @Override
    public RecyclerView.ViewHolder create(View view) {
        return new ChatActionTypingMessageViewHolder(view);
    }

    @Override
    public void bind(RecyclerView.ViewHolder viewHolder, Object data) {
        if(viewHolder instanceof ChatActionTypingMessageViewHolder && data instanceof Message){
            ChatActionTypingMessageViewHolder chatActionTypingMessageViewHolder = (ChatActionTypingMessageViewHolder) viewHolder;
            Message message = (Message) data;
            chatActionTypingMessageViewHolder.bind(message, requestExtendedArrays());
        }
    }

    public abstract ExtendedArrays requestExtendedArrays();
}
