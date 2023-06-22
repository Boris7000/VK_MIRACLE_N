package com.vkontakte.miracle.model.messages;

import androidx.annotation.Nullable;

import com.vkontakte.miracle.model.general.JSONable;
import com.vkontakte.miracle.model.messages.fields.CanWrite;
import com.vkontakte.miracle.model.messages.fields.ChatSettings;
import com.vkontakte.miracle.model.messages.fields.Peer;
import com.vkontakte.miracle.model.messages.fields.PushSettings;

import org.json.JSONException;
import org.json.JSONObject;

public class Conversation implements JSONable {

    private final Peer peer;

    private String inRead;
    private String outRead;

    private int unreadCount;

    private String lastMessageId;
    private String lastConversationMessageId;

    private boolean important;

    private CanWrite canWrite;

    private ChatSettings chatSettings;

    private PushSettings pushSettings;

    public Conversation(JSONObject jsonObject) throws JSONException {

        peer = new Peer(jsonObject.getJSONObject("peer"));

        inRead = jsonObject.getString("in_read");
        outRead = jsonObject.getString("out_read");

        unreadCount = jsonObject.optInt("unread_count");

        lastMessageId = jsonObject.getString("last_message_id");
        lastConversationMessageId = jsonObject.getString("last_conversation_message_id");

        important = jsonObject.getBoolean("important");

        canWrite = new CanWrite(jsonObject.getJSONObject("can_write"));

        if(jsonObject.has("push_settings")){
            pushSettings = new PushSettings(jsonObject.getJSONObject("push_settings"));
        }

        switch (peer.getType()){

            case "user":
            case "group":{
                break;
            }
            case "chat":{
                chatSettings = new ChatSettings(jsonObject.getJSONObject("chat_settings"));
                break;
            }
            case "email":{
                break;
            }
        }

    }

    public Conversation(Conversation conversation){
        peer = conversation.peer;
        inRead = conversation.inRead;
        outRead = conversation.outRead;
        unreadCount = conversation.unreadCount;
        lastMessageId = conversation.lastMessageId;
        lastConversationMessageId = conversation.lastConversationMessageId;
        important = conversation.important;
        canWrite = conversation.canWrite;
        chatSettings = conversation.chatSettings;
        pushSettings = conversation.pushSettings;
    }

    public Peer getPeer() {
        return peer;
    }

    public String getInRead() {
        return inRead;
    }

    public String getOutRead() {
        return outRead;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public String getLastMessageId() {
        return lastMessageId;
    }

    public String getLastConversationMessageId() {
        return lastConversationMessageId;
    }

    public boolean isImportant() {
        return important;
    }

    public CanWrite getCanWrite() {
        return canWrite;
    }

    @Nullable
    public ChatSettings getChatSettings() {
        return chatSettings;
    }

    @Nullable
    public PushSettings getPushSettings() {
        return pushSettings;
    }

    //------------------------------------------------------------//

    public void setInRead(String inRead) {
        this.inRead = inRead;
    }

    public void setOutRead(String outRead) {
        this.outRead = outRead;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public void setLastMessageId(String lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public void setLastConversationMessageId(String lastConversationMessageId) {
        this.lastConversationMessageId = lastConversationMessageId;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    public void setCanWrite(CanWrite canWrite) {
        this.canWrite = canWrite;
    }

    //------------------------------------------------------------//

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("peer", peer.toJSONObject());

        jsonObject.put("in_read", inRead);
        jsonObject.put("out_read", outRead);

        if(unreadCount!=0){
            jsonObject.put("unread_count", unreadCount);
        }

        jsonObject.put("last_message_id", lastMessageId);
        jsonObject.put("last_conversation_message_id", lastConversationMessageId);

        jsonObject.put("important", important);

        jsonObject.put("can_write", canWrite.toJSONObject());

        if(pushSettings!=null){
            jsonObject.put("push_settings", pushSettings.toJSONObject());
        }

        if(chatSettings!=null){
            jsonObject.put("chat_settings", chatSettings.toJSONObject());
        }

        return jsonObject;
    }



}
