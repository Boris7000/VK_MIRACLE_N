package com.vkontakte.miracle.viewholder.messages.chat.action;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.recyclerview.viewholder.bundle.ViewHolderBundle;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.model.general.ExtendedArrays;
import com.vkontakte.miracle.model.messages.Message;

public abstract class ChatActionRecordingVoiceMessageViewHolderBundle extends ViewHolderBundle<RecyclerView.ViewHolder,Object> {
    @Override
    public int getLayoutResourceId() {
        return R.layout.chat_ai_action_recording_voice_message;
    }

    @Override
    public RecyclerView.ViewHolder create(View view) {
        return new ChatActionRecordingVoiceMessageViewHolder(view);
    }

    @Override
    public void bind(RecyclerView.ViewHolder viewHolder, Object data) {
        if(viewHolder instanceof ChatActionRecordingVoiceMessageViewHolder && data instanceof Message){
            ChatActionRecordingVoiceMessageViewHolder chatActionRecordingVoiceMessageViewHolder = (ChatActionRecordingVoiceMessageViewHolder) viewHolder;
            Message message = (Message) data;
            chatActionRecordingVoiceMessageViewHolder.bind(message, requestExtendedArrays());
        }
    }

    public abstract ExtendedArrays requestExtendedArrays();
}
