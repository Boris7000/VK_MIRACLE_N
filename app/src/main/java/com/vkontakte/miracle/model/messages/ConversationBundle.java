package com.vkontakte.miracle.model.messages;

import android.util.ArrayMap;

import com.vkontakte.miracle.model.general.ExtendedArrays;
import com.vkontakte.miracle.model.general.JSONable;
import com.vkontakte.miracle.model.messages.fields.ChatActions;
import com.vkontakte.miracle.response.messages.MessagesHistoryResponse;
import com.vkontakte.miracle.response.messages.local.SpecificConversationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ConversationBundle implements JSONable {

    private long updateTime = 0;

    private Conversation conversation;

    private ChatActions chatActions = null;

    private int count;
    private final Map<String, Message> messagesMap = new ArrayMap<>();
    private final List<Message> messages = new ArrayList<>();

    public ConversationBundle(MessagesHistoryResponse res){
        this(res.getCount(), res.getConversation(), res.getMessages());
    }
    public ConversationBundle(ResponseConversationBundle bundle){
        this(bundle.getConversation(),bundle.getLastMessage());
    }

    public ConversationBundle(int count, Conversation conversation, List<Message> messages) {
        this.count = count;
        this.conversation = conversation;
        mergeMessages(messages);
    }

    public ConversationBundle(Conversation conversation, Message lastMessage){
        this.conversation = conversation;
        count = -1;
        messages.add(lastMessage);
        messagesMap.put(lastMessage.getId(), lastMessage);
    }

    public ConversationBundle(JSONObject joResponse) throws JSONException{

        updateTime = joResponse.optLong("update_time");

        conversation = new Conversation(joResponse.getJSONObject("conversation"));

        count = joResponse.optInt("count", -1);

        JSONArray jaMessages = joResponse.getJSONArray("messages");
        for (int j = 0; j < jaMessages.length(); j++) {
            Message message = new Message(jaMessages.getJSONObject(j));
            messages.add(message);
            messagesMap.put(message.getId(), message);
        }

    }

    public ListConversationBundle createListBundle(){
        return new ListConversationBundle(updateTime, conversation, chatActions, messages.get(0));
    }

    public SpecificConversationResponse createSpecificResponse(ExtendedArrays extendedArrays){
        return new SpecificConversationResponse(updateTime, conversation, chatActions, count, messages, extendedArrays);
    }

    //------------------------------------------------------------//

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public boolean setChatAction(ChatActions chatActions) {
        if(!Objects.equals(this.chatActions, chatActions)){
            this.chatActions = chatActions;
            return true;
        }
        return false;
    }

    public void setCount(int count) {
        this.count = count;
    }

    //------------------------------------------------------------//

    public String getPeerId(){
        return conversation.getPeer().getId();
    }

    public long getLastMessageDate(){
        return messages.get(0).getDate();
    }

    public boolean finallyLoaded(){
        return listIsLoaded()&&messages.size()>=count;
    }

    public boolean listIsLoaded(){
        return count>=0;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public ChatActions getChatActions() {
        return chatActions;
    }

    public int getCount() {
        return count;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public Message findMessageById(String messageId){
        return messagesMap.get(messageId);
    }

    //------------------------------------------------------------//

    public boolean addMessage(Message message){
        if(!listIsLoaded()){
            if(!messagesMap.containsKey(message.getId())) {
                messages.clear();
                messagesMap.clear();
                messages.add(message);
                messagesMap.put(message.getId(), message);
                return true;
            }
        } else {
            if(!messagesMap.containsKey(message.getId())){
                messages.add(message);
                messagesMap.put(message.getId(), message);
                return true;
            }
        }
        return false;
    }

    public boolean updateMessage(Message message){
        Message oldMessage = messagesMap.get(message.getId());
        if(oldMessage!=null){
            messages.set(messages.indexOf(oldMessage),message);
            messagesMap.replace(message.getId(), message);
            return true;
        }
        return false;
    }

    public void mergeMessages(List<Message> messages){
        for (Message message : messages) {
            if(!messagesMap.containsKey(message.getId())){
                this.messages.add(message);
                messagesMap.put(message.getId(), message);
            }
        }
    }

    public void sortMessagesById(){
        messages.sort((o1, o2) -> {
            int id1 = Integer.parseInt(o1.getId());
            int id2 = Integer.parseInt(o2.getId());
            return Integer.compare(id2, id1);
        });
    }

    //------------------------------------------------------------//

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        if(updateTime!=0) {
            jsonObject.put("update_time", updateTime);
        }

        jsonObject.put("conversation", conversation.toJSONObject());

        if(count>=0) {
            jsonObject.put("count", count);
        }

        JSONArray jaMessages = new JSONArray();
        for (Message message:messages) {
            jaMessages.put(message.toJSONObject());
        }
        jsonObject.put("messages", jaMessages);

        return jsonObject;
    }
}
