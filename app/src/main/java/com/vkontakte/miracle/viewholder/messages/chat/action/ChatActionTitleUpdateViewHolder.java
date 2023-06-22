package com.vkontakte.miracle.viewholder.messages.chat.action;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.model.general.ExtendedArrays;
import com.vkontakte.miracle.model.messages.Message;
import com.vkontakte.miracle.model.messages.fields.Action;
import com.vkontakte.miracle.util.ConversationUtil;

public class ChatActionTitleUpdateViewHolder extends RecyclerView.ViewHolder{

    private final TextView textView;

    public ChatActionTitleUpdateViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = ((TextView)itemView);
    }

    public void bind(Message message, ExtendedArrays extendedArrays){
        String actionMessage = "";
        Action action = message.getAction();

        if(action!=null) {
            actionMessage = ConversationUtil.getTitleUpdateString(
                    message.isOut()?null:extendedArrays.findOwnerById(message.getFromId()),
                    action,
                    message.isOut(),
                    itemView.getContext());
        }
        textView.setText(actionMessage);
    }
}
