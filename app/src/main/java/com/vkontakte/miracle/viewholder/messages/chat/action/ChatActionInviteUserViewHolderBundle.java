package com.vkontakte.miracle.viewholder.messages.chat.action;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.recyclerview.viewholder.bundle.ViewHolderBundle;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.model.general.ExtendedArrays;
import com.vkontakte.miracle.model.messages.Message;

public abstract class ChatActionInviteUserViewHolderBundle extends ViewHolderBundle<RecyclerView.ViewHolder,Object> {
    @Override
    public int getLayoutResourceId() {
        return R.layout.chat_ai_action_invite_user;
    }

    @Override
    public RecyclerView.ViewHolder create(View view) {
        return new ChatActionInviteUserViewHolder(view);
    }

    @Override
    public void bind(RecyclerView.ViewHolder viewHolder, Object data) {
        if(viewHolder instanceof ChatActionInviteUserViewHolder && data instanceof Message){
            ChatActionInviteUserViewHolder chatActionInviteUserViewHolder = (ChatActionInviteUserViewHolder) viewHolder;
            Message message = (Message) data;
            chatActionInviteUserViewHolder.bind(message, requestExtendedArrays());
        }
    }

    public abstract ExtendedArrays requestExtendedArrays();
}
