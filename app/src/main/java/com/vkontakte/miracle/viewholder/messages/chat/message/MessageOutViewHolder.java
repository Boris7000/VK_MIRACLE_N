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
import com.vkontakte.miracle.view.messages.MessageOutContainerView;
import com.vkontakte.miracle.view.text.VKTextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MessageOutViewHolder extends RecyclerView.ViewHolder {

    private final MessageOutContainerView messageOutContainerView;

    private final ViewStub textStub;
    private VKTextView text;

    public MessageOutViewHolder(@NonNull View itemView) {
        super(itemView);
        messageOutContainerView = (MessageOutContainerView) itemView;
        textStub = itemView.findViewById(R.id.textVS);
    }

    public void bind(Message message, Conversation conversation, ExtendedArrays extendedArrays){
        String date = new SimpleDateFormat("H:mm", Locale.getDefault()).format(message.getDate()*1000);
        int outReadId = Integer.parseInt(conversation.getOutRead());
        int messageId = Integer.parseInt(message.getId());

        boolean outRead = messageId<=outReadId;
        boolean deleted = message.isMarkedAsDeleted();

        messageOutContainerView.setOutRead(outRead);
        messageOutContainerView.setDateText(date);
        messageOutContainerView.setDeleted(deleted);

        messageOutContainerView.requestLayout();
        messageOutContainerView.invalidate();

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