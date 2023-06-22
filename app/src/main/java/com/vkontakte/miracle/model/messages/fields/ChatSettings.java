package com.vkontakte.miracle.model.messages.fields;

import androidx.annotation.Nullable;

import com.vkontakte.miracle.model.general.JSONable;
import com.vkontakte.miracle.model.messages.Message;

import org.json.JSONException;
import org.json.JSONObject;

public class ChatSettings implements JSONable {

    private String title;
    private Photo photo;
    private int membersCount;
    private int friendsCount;

    private String state;

    private Message pinnedMessage;

    public ChatSettings(JSONObject jsonObject) throws JSONException {

        title = jsonObject.getString("title");

        membersCount = jsonObject.optInt("members_count");

        friendsCount = jsonObject.optInt("friends_count");

        if(jsonObject.has("photo")){
            photo = new Photo(jsonObject.getJSONObject("photo"));
        }

        state = jsonObject.getString("state");

        if(jsonObject.has("pinned_message")){
            pinnedMessage = new Message(jsonObject.getJSONObject("pinned_message"));
        }

    }

    public String getTitle() {
        return title;
    }

    @Nullable
    public Photo getPhoto() {
        return photo;
    }

    public int getMembersCount() {
        return membersCount;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public String getState() {
        return state;
    }

    public Message getPinnedMessage() {
        return pinnedMessage;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public void setMembersCount(int membersCount) {
        this.membersCount = membersCount;
    }

    public void setFriendsCount(int friendsCount) {
        this.friendsCount = friendsCount;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setPinnedMessage(Message pinnedMessage) {
        this.pinnedMessage = pinnedMessage;
    }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("title", title);

        if(membersCount!=0) {
            jsonObject.put("members_count", membersCount);
        }

        if(friendsCount!=0) {
            jsonObject.put("friends_count", friendsCount);
        }

        if(photo!=null){
            jsonObject.put("photo", photo.toJSONObject());
        }

        jsonObject.put("state", state);

        if(pinnedMessage!=null){
            jsonObject.put("pinned_message", pinnedMessage.toJSONObject());
        }

        return jsonObject;
    }
}
