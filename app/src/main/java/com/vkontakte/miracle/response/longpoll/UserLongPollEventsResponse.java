package com.vkontakte.miracle.response.longpoll;

import static com.vkontakte.miracle.model.longpoll.LongPollEvent.TYPE_MESSAGES_IN_READ;
import static com.vkontakte.miracle.model.longpoll.LongPollEvent.TYPE_MESSAGES_OUT_READ;
import static com.vkontakte.miracle.model.longpoll.LongPollEvent.TYPE_MESSAGE_ADD;
import static com.vkontakte.miracle.model.longpoll.LongPollEvent.TYPE_MESSAGE_EDIT;
import static com.vkontakte.miracle.model.longpoll.LongPollEvent.TYPE_MESSAGE_FLAGS_CLEAR;
import static com.vkontakte.miracle.model.longpoll.LongPollEvent.TYPE_MESSAGE_FLAGS_SET;
import static com.vkontakte.miracle.model.longpoll.LongPollEvent.TYPE_MESSAGE_FLAGS_SWAP;
import static com.vkontakte.miracle.model.longpoll.LongPollEvent.TYPE_RECORDING_VOICE_MESSAGE;
import static com.vkontakte.miracle.model.longpoll.LongPollEvent.TYPE_TYPING_MESSAGE;
import static com.vkontakte.miracle.model.longpoll.LongPollEvent.TYPE_USER_OFFLINE;
import static com.vkontakte.miracle.model.longpoll.LongPollEvent.TYPE_USER_ONLINE;
import static com.vkontakte.miracle.network.vkapi.APIMethodsFactory.longPoll;

import android.util.Log;

import com.vkontakte.miracle.model.longpoll.LongPollEvent;
import com.vkontakte.miracle.model.longpoll.action.RecordingVoiceMessageEvent;
import com.vkontakte.miracle.model.longpoll.action.TypingMessageEvent;
import com.vkontakte.miracle.model.longpoll.action.UserOfflineEvent;
import com.vkontakte.miracle.model.longpoll.action.UserOnlineEvent;
import com.vkontakte.miracle.model.longpoll.conversations.MessagesInReadEvent;
import com.vkontakte.miracle.model.longpoll.conversations.MessagesOutReadEvent;
import com.vkontakte.miracle.model.longpoll.messages.MessageAddEvent;
import com.vkontakte.miracle.model.longpoll.messages.MessageEditEvent;
import com.vkontakte.miracle.model.longpoll.messages.MessageFlagsClearEvent;
import com.vkontakte.miracle.model.longpoll.messages.MessageFlagsEvent;
import com.vkontakte.miracle.model.longpoll.messages.MessageFlagsSetEvent;
import com.vkontakte.miracle.model.longpoll.messages.MessageFlagsSwapEvent;
import com.vkontakte.miracle.network.vkapi.VKResponseUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class UserLongPollEventsResponse {

    private final long ts;
    private final int failed;

    private final List<MessageAddEvent> messageAddEvents;
    private final List<MessageEditEvent> messageEditEvents;
    private final List<MessageFlagsEvent> messageFlagsEvents;
    private final List<MessagesInReadEvent> messagesInReadEvents;
    private final List<MessagesOutReadEvent> messagesOutReadEvents;
    private final List<TypingMessageEvent> typingMessageEvents;
    private final List<RecordingVoiceMessageEvent> recordingVoiceMessageEvents;
    private final List<UserOnlineEvent> userOnlineEvents;
    private final List<UserOfflineEvent> userOfflineEvents;
    private final List<LongPollEvent> badgeCountEvents;

    public UserLongPollEventsResponse(long ts, int failed,
                                      List<MessageAddEvent> messageAddEvents,
                                      List<MessageEditEvent> messageEditEvents,
                                      List<MessageFlagsEvent> messageFlagsEvents,
                                      List<MessagesInReadEvent> messagesInReadEvents,
                                      List<MessagesOutReadEvent> messagesOutReadEvents,
                                      List<TypingMessageEvent> typingMessageEvents,
                                      List<RecordingVoiceMessageEvent> recordingVoiceMessageEvents,
                                      List<UserOnlineEvent> userOnlineEvents,
                                      List<UserOfflineEvent> userOfflineEvents,
                                      List<LongPollEvent> badgeCountEvents) {
        this.ts = ts;
        this.failed = failed;
        this.messageAddEvents = messageAddEvents;
        this.messageEditEvents = messageEditEvents;
        this.messageFlagsEvents = messageFlagsEvents;
        this.messagesInReadEvents = messagesInReadEvents;
        this.messagesOutReadEvents = messagesOutReadEvents;
        this.typingMessageEvents = typingMessageEvents;
        this.recordingVoiceMessageEvents = recordingVoiceMessageEvents;
        this.userOnlineEvents = userOnlineEvents;
        this.userOfflineEvents = userOfflineEvents;
        this.badgeCountEvents = badgeCountEvents;
    }

    public static UserLongPollEventsResponse call(String server, String key, long ts, int mode, int lpVersion) throws Exception{

        Response<JSONObject> response = longPoll().request(
                server,
                "a_check",
                key,
                ts,
                30,
                mode,
                lpVersion
        ).execute();

        Log.d("LongPollService","longPoll "+ response.body());

        JSONObject joResponse = VKResponseUtil.validate(response, false);

        long newTs = joResponse.optLong("ts");

        int failed = joResponse.optInt("failed");


        List<MessageAddEvent> messageAddEvents = new ArrayList<>();
        List<MessageEditEvent> messageEditEvents = new ArrayList<>();
        List<MessageFlagsEvent> messageFlagsEvents = new ArrayList<>();
        List<MessagesInReadEvent> messagesInReadEvents = new ArrayList<>();
        List<MessagesOutReadEvent> messagesOutReadEvents = new ArrayList<>();
        List<TypingMessageEvent> typingMessageEvents = new ArrayList<>();
        List<RecordingVoiceMessageEvent> recordingVoiceMessageEvents = new ArrayList<>();
        List<UserOnlineEvent> userOnlineEvents = new ArrayList<>();
        List<UserOfflineEvent> userOfflineEvents = new ArrayList<>();
        List<LongPollEvent> badgeCountEvents = new ArrayList<>();


        if(joResponse.has("updates")){
            JSONArray updates = joResponse.getJSONArray("updates");
            for (int i = 0; i < updates.length(); i++) {
                JSONArray jsonArray = updates.getJSONArray(i);
                Log.d("LongPollService2", "updateEvent " + jsonArray.toString());
                int type = jsonArray.getInt(0);
                switch (type){
                    case TYPE_MESSAGE_FLAGS_SWAP:{
                        messageFlagsEvents.add(new MessageFlagsSwapEvent(jsonArray));
                        break;
                    }
                    case TYPE_MESSAGE_FLAGS_SET:{
                        messageFlagsEvents.add(new MessageFlagsSetEvent(jsonArray));
                        break;
                    }
                    case TYPE_MESSAGE_FLAGS_CLEAR:{
                        messageFlagsEvents.add(new MessageFlagsClearEvent(jsonArray));
                        break;
                    }
                    case TYPE_MESSAGE_ADD:{
                        messageAddEvents.add(new MessageAddEvent(jsonArray));
                        break;
                    }
                    case TYPE_MESSAGE_EDIT:{
                        messageEditEvents.add(new MessageEditEvent(jsonArray));
                        break;
                    }
                    //------------------------------------------------------------------//
                    case TYPE_TYPING_MESSAGE:{
                        typingMessageEvents.add(new TypingMessageEvent(jsonArray));
                        break;
                    }
                    case TYPE_RECORDING_VOICE_MESSAGE:{
                        recordingVoiceMessageEvents.add(new RecordingVoiceMessageEvent(jsonArray));
                        break;
                    }
                    //------------------------------------------------------------------//
                    case TYPE_MESSAGES_IN_READ:{
                        messagesInReadEvents.add(new MessagesInReadEvent(jsonArray));
                        break;
                    }
                    case TYPE_MESSAGES_OUT_READ:{
                        messagesOutReadEvents.add(new MessagesOutReadEvent(jsonArray));
                        break;
                    }
                    //------------------------------------------------------------------//
                    case TYPE_USER_ONLINE:{
                        userOnlineEvents.add(new UserOnlineEvent(jsonArray));
                        break;
                    }
                    case TYPE_USER_OFFLINE:{
                        userOfflineEvents.add(new UserOfflineEvent(jsonArray));
                        break;
                    }
                }
            }
        }

        return new UserLongPollEventsResponse(newTs, failed,
                messageAddEvents,
                messageEditEvents,
                messageFlagsEvents,
                messagesInReadEvents,
                messagesOutReadEvents,
                typingMessageEvents,
                recordingVoiceMessageEvents,
                userOnlineEvents,
                userOfflineEvents,
                badgeCountEvents);
    }

    public long getTs() {
        return ts;
    }

    public int getFailed() {
        return failed;
    }

    public List<MessageAddEvent> getMessageAddEvents() {
        return messageAddEvents;
    }

    public List<MessageEditEvent> getMessageEditEvents() {
        return messageEditEvents;
    }

    public List<MessageFlagsEvent> getMessageFlagsEvents() {
        return messageFlagsEvents;
    }

    public List<MessagesInReadEvent> getMessagesInReadEvents() {
        return messagesInReadEvents;
    }

    public List<MessagesOutReadEvent> getMessagesOutReadEvents() {
        return messagesOutReadEvents;
    }

    public List<TypingMessageEvent> getTypingMessageEvents() {
        return typingMessageEvents;
    }

    public List<RecordingVoiceMessageEvent> getRecordingVoiceMessageEvents() {
        return recordingVoiceMessageEvents;
    }

    public List<UserOnlineEvent> getUserOnlineEvents() {
        return userOnlineEvents;
    }

    public List<UserOfflineEvent> getUserOfflineEvents() {
        return userOfflineEvents;
    }

    public List<LongPollEvent> getBadgeCountEvents() {
        return badgeCountEvents;
    }
}
