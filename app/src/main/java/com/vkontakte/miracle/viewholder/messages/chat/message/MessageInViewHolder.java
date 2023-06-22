package com.vkontakte.miracle.viewholder.messages.chat.message;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewStub;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.model.general.ExtendedArrays;
import com.vkontakte.miracle.model.messages.Conversation;
import com.vkontakte.miracle.model.messages.Message;
import com.vkontakte.miracle.view.messages.MessageInContainerView;
import com.vkontakte.miracle.view.text.VKTextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MessageInViewHolder extends RecyclerView.ViewHolder {

    private final MessageInContainerView messageInContainerView;

    private final ViewStub textStub;
    private VKTextView text;

    public MessageInViewHolder(@NonNull View itemView) {
        super(itemView);
        messageInContainerView = (MessageInContainerView) itemView;
        textStub = itemView.findViewById(R.id.textVS);
    }

    public void bind(Message message, Conversation conversation, ExtendedArrays extendedArrays){
        String date = new SimpleDateFormat("H:mm", Locale.getDefault()).format(message.getDate()*1000);
        int inReadId = Integer.parseInt(conversation.getInRead());
        int messageId = Integer.parseInt(message.getId());

        boolean intRead = messageId<=inReadId;
        boolean deleted = message.isMarkedAsDeleted();

        messageInContainerView.setInRead(intRead);
        messageInContainerView.setDateText(date);
        messageInContainerView.setDeleted(deleted);

        messageInContainerView.requestLayout();
        messageInContainerView.invalidate();

        if(message.getText().isEmpty()){
            hideText();
        } else {
            showText();
            text.setText(message.getText());
        }

    }

    private void stubText(){
        if(text==null) {
            if(textStub!=null) {
                text = (VKTextView) textStub.inflate();
            } else {
                text = itemView.findViewById(R.id.text);
            }
            if(text!=null){
                text.setOnUrlClickListener(url -> {/*
                    if(onPostActionsListener!=null){
                        onPostActionsListener.onUrlLinkClick(url);
                    }*/
                });
                text.setOnHashtagClickListener(hashtag -> {/*
                    if(onPostActionsListener!=null){
                        onPostActionsListener.onHashTagLinkClick(hashtag);
                    }*/
                });
                text.setOnOwnerClickListener(ownerId -> {/*
                    if(onPostActionsListener!=null){
                        onPostActionsListener.onOwnerLinkClick(ownerId);
                    }*/
                });
                text.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
    }

    private void showText(){
        stubText();
        if (text.getVisibility() != VISIBLE) {
            text.setVisibility(VISIBLE);
        }
    }

    private void hideText(){
        if (text != null && text.getVisibility() != GONE) {
            text.setVisibility(GONE);
        }
    }
}
