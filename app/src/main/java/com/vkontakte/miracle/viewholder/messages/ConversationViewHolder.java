package com.vkontakte.miracle.viewholder.messages;

import static com.vkontakte.miracle.util.ConversationUtil.ACTION_RECORDING_VOICE_MESSAGE;
import static com.vkontakte.miracle.util.ConversationUtil.ACTION_RECORDING_VOICE_MESSAGE_BY;
import static com.vkontakte.miracle.util.ConversationUtil.ACTION_RECORDING_VOICE_MESSAGE_BY_MULTIPLE;
import static com.vkontakte.miracle.util.ConversationUtil.ACTION_TYPING;
import static com.vkontakte.miracle.util.ConversationUtil.ACTION_TYPING_BY;
import static com.vkontakte.miracle.util.ConversationUtil.ACTION_TYPING_BY_MULTIPLE;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.model.general.ExtendedArrays;
import com.vkontakte.miracle.model.general.Owner;
import com.vkontakte.miracle.model.groups.Group;
import com.vkontakte.miracle.model.messages.Conversation;
import com.vkontakte.miracle.model.messages.ListConversationBundle;
import com.vkontakte.miracle.model.messages.Message;
import com.vkontakte.miracle.model.messages.fields.Action;
import com.vkontakte.miracle.model.messages.fields.ChatActions;
import com.vkontakte.miracle.model.messages.fields.ChatSettings;
import com.vkontakte.miracle.model.messages.fields.Peer;
import com.vkontakte.miracle.model.messages.fields.PushSettings;
import com.vkontakte.miracle.model.users.Profile;
import com.vkontakte.miracle.model.users.fileds.LastSeen;
import com.vkontakte.miracle.util.TimeUtil;
import com.vkontakte.miracle.util.ConversationUtil;
import com.vkontakte.miracle.view.messages.ConversationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConversationViewHolder extends RecyclerView.ViewHolder{

    private OnConversationActionsListener onConversationActionsListener;

    private ListConversationBundle listConversationBundle;

    private final ConversationView conversationView;
    private final ImageView ownerImage;

    public ConversationViewHolder(@NonNull View itemView) {
        super(itemView);
        conversationView = (ConversationView) itemView;
        ownerImage = conversationView.getAvatarImage();

        itemView.setOnClickListener(v -> {
            if(listConversationBundle!=null&&onConversationActionsListener!=null){
                onConversationActionsListener.onClick(listConversationBundle);
            }
        });

        itemView.setOnLongClickListener(v -> {
            if(listConversationBundle!=null&&onConversationActionsListener!=null){
                onConversationActionsListener.onLongClick(listConversationBundle);
            }
            return true;
        });
    }

    public void bind(ListConversationBundle listConversationBundle, ExtendedArrays extendedArrays){
        this.listConversationBundle = listConversationBundle;
        String photo = null;
        String title = null;
        boolean verified = false;
        boolean muted = false;
        String messageOwner = null;
        String message = null;
        String actionMessage = null;
        String date = null;
        int unreadCount = 0;
        boolean outUnread = false;
        boolean typingMessage = false;
        boolean recordingVoiceMessage = false;
        boolean online = false;
        boolean mobile = false;

        Context context = itemView.getContext();

        Conversation conversation = listConversationBundle.getConversation();
        Message lastMessage = listConversationBundle.getLastMessage();
        ChatActions chatActions = listConversationBundle.getChatAction();

        PushSettings pushSettings = conversation.getPushSettings();
        Peer peer = conversation.getPeer();

        switch (peer.getType()){
            case "user":{
                Profile profile = extendedArrays.getProfiles().get(peer.getId());
                if(profile!=null) {
                    title = profile.getFullName();
                    photo = profile.getPhoto200();
                    verified = profile.isVerified();
                    online =  profile.isOnline();
                    LastSeen lastSeen = profile.getLastSeen();
                    if(lastSeen!=null){
                        mobile = profile.getLastSeen().getPlatform()<6;
                    }
                }
                break;
            }
            case "group":{
                Group group = extendedArrays.getGroups().get(peer.getId());
                if(group!=null) {
                    title = group.getName();
                    photo = group.getPhoto200();
                    verified = group.isVerified();
                }
                break;
            }
            case "chat":{
                ChatSettings chatSettings = conversation.getChatSettings();
                if(chatSettings!=null){
                    title = chatSettings.getTitle();
                    if(chatSettings.getPhoto()!=null){
                        photo = chatSettings.getPhoto().getPhoto200();
                    }
                }
                break;
            }
            case "email":{
                break;
            }
        }

        if(pushSettings!=null){
            muted = pushSettings.isDisabledForever()||pushSettings.isNoSound();
        }


        if(lastMessage.isOut()){
            if (!conversation.getOutRead().equals(lastMessage.getId())) {
                outUnread = true;
            }
        } else {
            unreadCount = conversation.getUnreadCount();
        }

        if(chatActions !=null){
            switch (chatActions.getAverageType()){
                case TYPING_MESSAGE:{
                    List<String> ids = new ArrayList<>();
                    for (Map.Entry<String, ChatActions.Type>entry:chatActions.getActions().entrySet()) {
                        ids.add(entry.getKey());
                    }
                    if(ids.size()==1){
                        switch (peer.getType()) {
                            case "user":
                            case "group": {
                                actionMessage = context.getString(ACTION_TYPING);
                                typingMessage = true;
                                break;
                            }
                            case "chat": {
                                Owner owner = extendedArrays.findOwnerById(ids.get(0));
                                if(owner!=null) {
                                    actionMessage = context.getString(ACTION_TYPING_BY,
                                            owner.getShortName());
                                    typingMessage = true;
                                }
                                break;
                            }
                            case "email": {
                                break;
                            }
                        }
                    } else {
                        ArrayList<String> names = new ArrayList<>();
                        int countWithoutLast = ids.size()-1;
                        for (int i=0; i<countWithoutLast;i++){
                            Owner owner = extendedArrays.findOwnerById(ids.get(i));
                            if(owner!=null){
                                names.add(owner.getShortName());
                            }
                        }
                        Owner owner = extendedArrays.findOwnerById(ids.get(countWithoutLast));
                        if(!names.isEmpty()&&owner!=null){
                            actionMessage = context.getString(ACTION_TYPING_BY_MULTIPLE,
                                    String.join(", ", names),owner.getShortName());
                            typingMessage = true;
                        }
                    }
                    break;
                }
                case RECORDING_VOICE_MESSAGE:{
                    List<String> ids = new ArrayList<>();
                    for (Map.Entry<String, ChatActions.Type>entry:chatActions.getActions().entrySet()) {
                        ids.add(entry.getKey());
                    }
                    if(ids.size()==1){
                        switch (peer.getType()) {
                            case "user":
                            case "group": {
                                actionMessage = context.getString(ACTION_RECORDING_VOICE_MESSAGE);
                                recordingVoiceMessage = true;
                                break;
                            }
                            case "chat": {
                                Owner owner = extendedArrays.findOwnerById(ids.get(0));
                                if(owner!=null) {
                                    actionMessage = context.getString(ACTION_RECORDING_VOICE_MESSAGE_BY,
                                            owner.getShortName());
                                    recordingVoiceMessage = true;
                                }
                                break;
                            }
                            case "email": {
                                break;
                            }
                        }
                    } else {
                        ArrayList<String> names = new ArrayList<>();
                        int countWithoutLast = ids.size()-1;
                        for (int i=0; i<countWithoutLast;i++){
                            Owner owner = extendedArrays.findOwnerById(ids.get(i));
                            if(owner!=null){
                                names.add(owner.getShortName());
                            }
                        }
                        Owner owner = extendedArrays.findOwnerById(ids.get(countWithoutLast));
                        if(!names.isEmpty()&&owner!=null){
                            actionMessage = context.getString(ACTION_RECORDING_VOICE_MESSAGE_BY_MULTIPLE,
                                    String.join(", ", names),owner.getShortName());
                            recordingVoiceMessage = true;
                        }
                    }
                    break;
                }
            }
        } else {
            Action action = lastMessage.getAction();
            Owner lastMessageOwner = extendedArrays.findOwnerById(lastMessage.getFromId());

            date = TimeUtil.getShortDateString(context, lastMessage.getDate());

            if (action != null) {
                if (!action.getMessage().isEmpty()) {
                    actionMessage = action.getMessage();
                } else {
                    switch (action.getType()) {
                        case "chat_create": {
                            actionMessage = ConversationUtil.getCreateString(
                                    lastMessageOwner,action,lastMessage.isOut(),
                                    context);
                            break;
                        }
                        case "chat_photo_update": {
                            actionMessage = ConversationUtil.getPhotoUpdateString(
                                    lastMessageOwner,lastMessage.isOut(),
                                    context);
                            break;
                        }
                        case "chat_title_update": {
                            actionMessage = ConversationUtil.getTitleUpdateString(
                                    lastMessageOwner,action,lastMessage.isOut(),
                                    context);
                            break;
                        }
                        case "chat_invite_user": {
                            actionMessage = ConversationUtil.getInviteUserString(
                                    lastMessageOwner, action, extendedArrays, lastMessage.isOut(),
                                    context);
                            break;
                        }
                        case "chat_kick_user": {
                            actionMessage = ConversationUtil.getKickUserString(
                                    lastMessageOwner, action, extendedArrays, lastMessage.isOut(),
                                    context);
                            break;
                        }
                    }
                }
            } else {
                if (!lastMessage.getText().isEmpty()) {
                    message = lastMessage.getText();
                } else {
                    actionMessage = "Вложения";
                }
                switch (peer.getType()) {
                    case "user":
                    case "group": {
                        if (lastMessage.isOut()) {
                            messageOwner = context.getString(R.string.you);
                        }
                        break;
                    }
                    case "chat": {
                        if (lastMessage.isOut()) {
                            messageOwner = context.getString(R.string.you);
                        } else {
                            if (lastMessageOwner != null) {
                                messageOwner = lastMessageOwner.getShortName();
                            }
                        }
                        break;
                    }
                    case "email": {
                        break;
                    }
                }
            }
        }

        conversationView.setTitleText(title);
        conversationView.setVerified(verified);
        conversationView.setMuted(muted);
        conversationView.setMessageOwnerText(messageOwner);
        conversationView.setMessageText(message);
        conversationView.setActionMessageText(actionMessage);
        conversationView.setDateText(date);
        conversationView.setUnreadInCount(unreadCount);
        conversationView.setUnreadOut(outUnread);
        conversationView.setTypingMessage(typingMessage);
        conversationView.setRecordingVoiceMessage(recordingVoiceMessage);
        conversationView.setOnline(online, mobile);


        Picasso.get().cancelRequest(ownerImage);
        if(photo==null||photo.isEmpty()){
            ownerImage.setImageDrawable(ConversationUtil.getConversationAvatarPlaceHolder(title, context));
        } else {
            Picasso.get().load(photo).into(ownerImage);
        }

        conversationView.requestLayout();
        conversationView.invalidate();

    }

    //------------------------------------------//

    public void setOnConversationActionsListener(OnConversationActionsListener onConversationActionsListener) {
        this.onConversationActionsListener = onConversationActionsListener;
    }

    public interface OnConversationActionsListener{
        void onClick(ListConversationBundle listConversationBundle);
        void onLongClick(ListConversationBundle listConversationBundle);

    }

}
