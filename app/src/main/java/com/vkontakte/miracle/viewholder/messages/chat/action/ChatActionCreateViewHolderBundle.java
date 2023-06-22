package com.vkontakte.miracle.viewholder.messages.chat.action;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.recyclerview.viewholder.bundle.ViewHolderBundle;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.model.general.ExtendedArrays;
import com.vkontakte.miracle.model.messages.Message;

public abstract class ChatActionCreateViewHolderBundle extends ViewHolderBundle<RecyclerView.ViewHolder,Object> {
    @Override
    public int getLayoutResourceId() {
        return R.layout.chat_ai_action_create;
    }

    @Override
    public RecyclerView.ViewHolder create(View view) {
        return new ChatActionCreateViewHolder(view);
    }

    @Override
    public void bind(RecyclerView.ViewHolder viewHolder, Object data) {
        if(viewHolder instanceof ChatActionCreateViewHolder && data instanceof Message){
            ChatActionCreateViewHolder chatActionCreateViewHolder = (ChatActionCreateViewHolder) viewHolder;
            Message message = (Message) data;
            chatActionCreateViewHolder.bind(message, requestExtendedArrays());
        }
    }

    public abstract ExtendedArrays requestExtendedArrays();
}
