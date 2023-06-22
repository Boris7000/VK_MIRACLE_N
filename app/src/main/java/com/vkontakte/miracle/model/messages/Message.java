package com.vkontakte.miracle.model.messages;

import static com.vkontakte.miracle.network.vkapi.VKResponseUtil.intAsBoolean;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.miracle.engine.annotation.MayBeEmpty;
import com.miracle.engine.recyclerview.TypedData;
import com.vkontakte.miracle.model.general.Attachments;
import com.vkontakte.miracle.model.general.JSONable;
import com.vkontakte.miracle.model.messages.fields.Action;
import com.vkontakte.miracle.util.constants.TypedDataConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class Message implements TypedData, JSONable {

    private final String id;
    private final String conversationMessageId;
    private final String randomId;
    private final String fromId;
    private final String peerId;
    private final boolean out;
    private final long date;
    private long updateTime;

    private boolean markedAsDeleted;

    private String text;
    private Attachments attachments;
    private Action action;

    public Message(JSONObject jsonObject) throws JSONException {

        Log.d("efoekfoekofef",jsonObject.toString());

        id = jsonObject.getString("id");
        conversationMessageId = jsonObject.getString("conversation_message_id");

        randomId = jsonObject.optString("random_id","0");

        fromId = jsonObject.getString("from_id");

        peerId = jsonObject.getString("peer_id");

        out = intAsBoolean(jsonObject.optInt("out"));

        date = jsonObject.getLong("date");

        updateTime = jsonObject.optLong("update_time");

        text = jsonObject.optString("text");


        if(jsonObject.has("attachments")){
            attachments = new Attachments(jsonObject.getJSONArray("attachments"), true);
        }

        if(jsonObject.has("action")){
            action = new Action(jsonObject.getJSONObject("action"));
        }

        markedAsDeleted = jsonObject.optBoolean("marked_as_deleted");

    }

    public String getId() {
        return id;
    }

    public String getConversationMessageId() {
        return conversationMessageId;
    }

    public String getRandomId() {
        return randomId;
    }

    public String getFromId() {
        return fromId;
    }

    public String getPeerId() {
        return peerId;
    }

    public boolean isOut() {
        return out;
    }

    public long getDate() {
        return date;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public boolean isMarkedAsDeleted() {
        return markedAsDeleted;
    }

    @MayBeEmpty
    public String getText() {
        return text;
    }

    @Nullable
    public Attachments getAttachments() {
        return attachments;
    }

    @Nullable
    public Action getAction() {
        return action;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setAttachments(Attachments attachments) {
        this.attachments = attachments;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setMarkedAsDeleted(boolean markedAsDeleted) {
        this.markedAsDeleted = markedAsDeleted;
    }

    @Override
    public JSONObject toJSONObject() throws JSONException {

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("id",id);

        jsonObject.put("conversation_message_id", conversationMessageId);

        jsonObject.put("random_id", randomId);

        jsonObject.put("from_id", fromId);

        jsonObject.put("peer_id", peerId);

        jsonObject.put("out", out?1:0);

        jsonObject.put("date", date);

        if(updateTime!=0) {
            jsonObject.put("update_time", updateTime);
        }

        if(!text.isEmpty()) {
            jsonObject.put("text", text);
        }

        //TODO надо доделать
        /*
        if(attachments!=null){
            jsonObject.put("attachments", attachments.toJSONObject());
        }*/

        if(action!=null){
            jsonObject.put("action", action.toJSONObject());
        }

        if(markedAsDeleted){
            jsonObject.put("marked_as_deleted", true);
        }

        return jsonObject;
    }

    @Override
    public int getDataType() {
        if(action!=null){
            switch (action.getType()){
                case "chat_create":{
                    return TypedDataConstants.TYPE_CHAT_ACTION_CREATE;
                }
                case "chat_invite_user":{
                    return TypedDataConstants.TYPE_CHAT_ACTION_INVITE_USER;
                }
                case "chat_kick_user":{
                    return TypedDataConstants.TYPE_CHAT_ACTION_KICK_USER;
                }
                case "chat_photo_update":{
                    return TypedDataConstants.TYPE_CHAT_ACTION_PHOTO_UPDATE;
                }
                case "chat_title_update":{
                    return TypedDataConstants.TYPE_CHAT_ACTION_TITLE_UPDATE;
                }
            }
        }
        return out?TypedDataConstants.TYPE_MESSAGE_OUT:TypedDataConstants.TYPE_MESSAGE_IN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return id.equals(message.getId());
    }

    @Override
    public boolean equalsContent(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return updateTime==message.updateTime;
    }

    @Override
    public int contentHashCode() {
        return Objects.hashCode(updateTime);
    }
}
