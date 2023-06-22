package com.vkontakte.miracle.service.user;

import android.util.ArrayMap;
import android.util.Log;

import androidx.annotation.Nullable;

import com.vkontakte.miracle.memory.storage.UsersStorage;
import com.vkontakte.miracle.model.auth.User;
import com.vkontakte.miracle.model.general.ExtendedArrays;
import com.vkontakte.miracle.model.longpoll.action.RecordingVoiceMessageEvent;
import com.vkontakte.miracle.model.longpoll.action.TypingMessageEvent;
import com.vkontakte.miracle.model.longpoll.conversations.MessagesInReadEvent;
import com.vkontakte.miracle.model.longpoll.conversations.MessagesOutReadEvent;
import com.vkontakte.miracle.model.longpoll.messages.MessageAddEvent;
import com.vkontakte.miracle.model.longpoll.messages.MessageEditEvent;
import com.vkontakte.miracle.model.longpoll.messages.MessageFlagsClearEvent;
import com.vkontakte.miracle.model.longpoll.messages.MessageFlagsEvent;
import com.vkontakte.miracle.model.longpoll.messages.MessageFlagsSetEvent;
import com.vkontakte.miracle.model.longpoll.messages.MessageFlagsSwapEvent;
import com.vkontakte.miracle.model.longpoll.messages.fields.MessageFlags;
import com.vkontakte.miracle.model.messages.Conversation;
import com.vkontakte.miracle.model.messages.ConversationBundle;
import com.vkontakte.miracle.model.messages.ListConversationBundle;
import com.vkontakte.miracle.model.messages.Message;
import com.vkontakte.miracle.model.messages.ResponseConversationBundle;
import com.vkontakte.miracle.model.messages.fields.ChatActions;
import com.vkontakte.miracle.response.longpoll.UserLongPollEventsResponse;
import com.vkontakte.miracle.response.messages.ConversationsByIdResponse;
import com.vkontakte.miracle.response.messages.ConversationsResponse;
import com.vkontakte.miracle.response.messages.MessagesByIdResponse;
import com.vkontakte.miracle.response.messages.MessagesHistoryResponse;
import com.vkontakte.miracle.response.messages.local.CachedConversationsResponse;
import com.vkontakte.miracle.response.messages.local.ListConversationsResponse;
import com.vkontakte.miracle.response.messages.local.SpecificConversationResponse;
import com.vkontakte.miracle.util.async.DisposableMap;
import com.vkontakte.miracle.util.async.SequenceOfSingle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class ConversationsModel {

    private static ConversationsModel instance;
    private final static String LOG_TAG = "ConversationsModel";

    //--------------------------------------------------//

    private final int step = 25;
    private boolean memoryIsChecked = false;
    private boolean loading = false;
    private Map<String, ConversationBundle> conversationBundleMap = null;
    private List<ConversationBundle> conversationBundles = null;
    private ExtendedArrays extendedArrays = null;
    private int count = -1;

    //--------------------------------------------------//

    private User user;
    private File userDir;

    //--------------------------------------------------//

    private final List<PublishSubject<ListConversationsResponse>> subscribers = new ArrayList<>();
    private final Map<String, Boolean> specificLoadingMap = new ArrayMap<>();
    private final Map<String, List<PublishSubject<SpecificConversationResponse>>> specificSubscribersMap = new ArrayMap<>();
    private SequenceOfSingle sequenceOfSingle = new SequenceOfSingle();
    private final DisposableMap<String> conversationActionsDisposableMap = new DisposableMap<>();

    //--------------------------------------------------//

    public static ConversationsModel get(){
        if (null == instance){
            instance = new ConversationsModel();
        }
        return instance;
    }

    //--------------------------------------------------//

    public void changeUser(@Nullable User newUser, @Nullable File newUserDir) {
        if(!Objects.equals(user, newUser)){
            softClear();
            loading = false;
            memoryIsChecked = false;

            if (newUser != null && newUserDir != null) {
                user = newUser;
                userDir = newUserDir;
            } else {
                user = null;
                userDir = null;
            }
        }
    }

    private void softClear(){
        sequenceOfSingle.clear();
        sequenceOfSingle = new SequenceOfSingle();
        conversationActionsDisposableMap.clear();

        count = -1;
        extendedArrays = null;
        conversationBundleMap = null;
        conversationBundles = null;
    }

    public void clear(){
        softClear();
        memoryIsChecked = true;

        Log.d(LOG_TAG,"Очистка памяти");

        sequenceOfSingle.add(removeConversationsFromMemory(), o -> {}, throwable -> {});

        for (Map.Entry<String,?> entry: specificSubscribersMap.entrySet()){
            specificLoadingMap.put(entry.getKey(), true);
            Single<Object> single1 = loadSpecificConversationFromServerSingle(entry.getKey());
            Consumer<Object> consumer = loadSpecificConversationFromServerConsumer(single1, entry.getKey());
            Consumer<Throwable> onError = loadSpecificConversationFromServerOnError(single1, entry.getKey());
            sequenceOfSingle.add(single1, consumer, onError);
        }

        if(subscribers.isEmpty()){
            loading = false;
        } else {
            loading = true;
            Single<Object> single1 = loadConversationsFromServerSingle();
            Consumer<Object> consumer = loadConversationsFromServerConsumer(single1);
            Consumer<Throwable> onError = loadConversationsFromServerOnError(single1);
            sequenceOfSingle.add(single1, consumer, onError);
        }
    }

    public void applyUpdates(UserLongPollEventsResponse userLongPollEventsResponse, Single<Object> success){
        Single<Object> rootSingle = Single.just((Object) true).observeOn(AndroidSchedulers.mainThread());
        sequenceOfSingle.add(rootSingle, ignore -> {
            if(!isEmpty()){
                Log.d(LOG_TAG,"Применение изменений");

                Map<String,Boolean> updatedMap = new ArrayMap<>();

                Single<Object> lastSingle = rootSingle;

                List<MessageFlagsEvent> messageFlagsEvents = userLongPollEventsResponse.getMessageFlagsEvents();
                if(!messageFlagsEvents.isEmpty()){
                    Log.d(LOG_TAG,"MessageFlagsEvent applying");
                    for (MessageFlagsEvent event : messageFlagsEvents){
                        if(event instanceof MessageFlagsSetEvent){
                            MessageFlagsSetEvent messageFlagsSetEvent = (MessageFlagsSetEvent) event;
                            ConversationBundle conversationBundle = conversationBundleMap.get(messageFlagsSetEvent.getPeerId());
                            if(conversationBundle!=null){
                                Message message = conversationBundle.findMessageById(messageFlagsSetEvent.getMessageId());
                                if(message!=null){
                                    MessageFlags messageFlags = new MessageFlags(messageFlagsSetEvent.getFlags());
                                    if (messageFlags.isDeleted()||messageFlags.isDeletedForAll()){
                                        if(!message.isMarkedAsDeleted()) {
                                            message.setMarkedAsDeleted(true);
                                            long updateTime = System.currentTimeMillis();
                                            message.setUpdateTime(updateTime);
                                            conversationBundle.setUpdateTime(updateTime);
                                            updatedMap.put(messageFlagsSetEvent.getPeerId(), true);
                                        }
                                    }
                                }
                            }
                        } else if(event instanceof MessageFlagsSwapEvent){
                            MessageFlagsSwapEvent messageFlagsSwapEvent = (MessageFlagsSwapEvent) event;

                        } else if(event instanceof MessageFlagsClearEvent){
                            MessageFlagsClearEvent messageFlagsClearEvent = (MessageFlagsClearEvent) event;
                        }
                    }
                }

                List<MessagesInReadEvent> messagesInReadEvents = userLongPollEventsResponse.getMessagesInReadEvents();
                if(!messagesInReadEvents.isEmpty()){
                    Log.d(LOG_TAG,"MessagesInReadEvent applying");
                    for (MessagesInReadEvent event : messagesInReadEvents){
                        ConversationBundle conversationBundle = conversationBundleMap.get(event.getPeerId());
                        if(conversationBundle!=null){
                            Conversation conversation = conversationBundle.getConversation();
                            Message oldLastReadMessage = conversationBundle.findMessageById(conversation.getInRead());
                            conversation.setInRead(event.getLocalId());

                            long updateTime = System.currentTimeMillis();
                            conversationBundle.setUpdateTime(updateTime);
                            conversation.setUnreadCount(event.getUnreadCount());
                            updatedMap.put(event.getPeerId(), true);

                            Message newLastReadMessage = conversationBundle.findMessageById(event.getLocalId());
                            List<Message> messages = conversationBundle.getMessages();
                            int oldLastReadMessageIndex = messages.size()-1;
                            if(oldLastReadMessage!=null) {
                                oldLastReadMessageIndex = messages.indexOf(oldLastReadMessage);
                            }
                            int newLastReadMessageIndex = -1;
                            if(newLastReadMessage!=null) {
                                newLastReadMessageIndex = messages.indexOf(newLastReadMessage);
                            }
                            if(newLastReadMessageIndex>=0){
                                for (int i=newLastReadMessageIndex;i<=oldLastReadMessageIndex;i++){
                                    Message message = messages.get(i);
                                    if(!message.isOut()) {
                                        message.setUpdateTime(updateTime);
                                    }
                                }
                            }
                        }
                    }
                }

                List<MessagesOutReadEvent> messagesOutReadEvents = userLongPollEventsResponse.getMessagesOutReadEvents();
                if(!messagesOutReadEvents.isEmpty()){
                    Log.d(LOG_TAG,"MessagesOutReadEvent applying");
                    for (MessagesOutReadEvent event : messagesOutReadEvents){
                        ConversationBundle conversationBundle = conversationBundleMap.get(event.getPeerId());
                        if(conversationBundle!=null){
                            Conversation conversation = conversationBundle.getConversation();
                            Message oldLastReadMessage = conversationBundle.findMessageById(conversation.getOutRead());
                            conversation.setOutRead(event.getLocalId());

                            long updateTime = System.currentTimeMillis();
                            conversationBundle.setUpdateTime(updateTime);
                            conversation.setUnreadCount(event.getUnreadCount());
                            updatedMap.put(event.getPeerId(), true);

                            Message newLastReadMessage = conversationBundle.findMessageById(event.getLocalId());
                            List<Message> messages = conversationBundle.getMessages();
                            int oldLastReadMessageIndex = messages.size()-1;
                            if(oldLastReadMessage!=null) {
                                oldLastReadMessageIndex = messages.indexOf(oldLastReadMessage);
                            }
                            int newLastReadMessageIndex = -1;
                            if(newLastReadMessage!=null) {
                                newLastReadMessageIndex = messages.indexOf(newLastReadMessage);
                            }
                            if(newLastReadMessageIndex>=0){
                                for (int i=newLastReadMessageIndex;i<=oldLastReadMessageIndex;i++){
                                    Message message = messages.get(i);
                                    if(message.isOut()) {
                                        message.setUpdateTime(updateTime);
                                    }
                                }
                            }
                        }
                    }
                }

                List<MessageEditEvent> messageEditEvents = userLongPollEventsResponse.getMessageEditEvents();
                if (!messageEditEvents.isEmpty()) {
                    Single<Object> single = Single.fromCallable((Callable<Object>) () -> {
                        Log.d(LOG_TAG,"MessageEditEvent applying");
                        List<String> existsConversationsMessageIds = new ArrayList<>();
                        for (MessageEditEvent event : messageEditEvents){
                            if(conversationBundleMap.containsKey(event.getPeerId())){
                                updatedMap.put(event.getPeerId(), true);
                                existsConversationsMessageIds.add(event.getMessageId());
                            }
                        }

                        if(!existsConversationsMessageIds.isEmpty()) {
                            final String messageIds = String.join(",", existsConversationsMessageIds);
                            MessagesByIdResponse messagesByIdResponse =
                                    MessagesByIdResponse.call(messageIds);
                            mergeExtendedArrays(messagesByIdResponse.getExtendedArrays());
                            long updateTime = userLongPollEventsResponse.getTs();
                            for (Message message : messagesByIdResponse.getMessages()) {
                                ConversationBundle conversationBundle = conversationBundleMap.get(message.getPeerId());
                                if (conversationBundle != null) {
                                    if (conversationBundle.updateMessage(message)) {
                                        conversationBundle.setUpdateTime(updateTime);
                                    }
                                }
                            }
                        }

                        return true;
                    }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
                    sequenceOfSingle.addUnder(lastSingle, single, o -> {}, throwable -> {});
                    lastSingle = single;
                }

                List<MessageAddEvent> messageAddEvents = userLongPollEventsResponse.getMessageAddEvents();
                if (!messageAddEvents.isEmpty()) {
                    Single<Object> single = Single.fromCallable((Callable<Object>) () -> {
                        Log.d(LOG_TAG,"MessageAddEvent applying");
                        List<String> nonExistsConversationsIds = new ArrayList<>();
                        List<String> existsConversationsMessageIds = new ArrayList<>();
                        for (MessageAddEvent event : messageAddEvents){
                            if(conversationBundleMap.containsKey(event.getPeerId())){
                                updatedMap.put(event.getPeerId(), true);
                                existsConversationsMessageIds.add(event.getMessageId());
                            } else if(listIsLoaded()){
                                nonExistsConversationsIds.add(event.getPeerId());
                            }
                        }

                        boolean somethingChanged = false;

                        if(!nonExistsConversationsIds.isEmpty()) {
                            for (String id:nonExistsConversationsIds) {
                                updatedMap.put(id, true);
                            }
                            somethingChanged = true;
                            final String conversationIds = String.join(",", nonExistsConversationsIds);
                            ConversationsByIdResponse conversationsByIdResponse =
                                    ConversationsByIdResponse.callWithMessages(conversationIds);
                            mergeExtendedArrays(conversationsByIdResponse.getExtendedArrays());
                            mergeConversations(conversationsByIdResponse.getConversationBundles());
                        }

                        if(!existsConversationsMessageIds.isEmpty()) {
                            for (String id:existsConversationsMessageIds) {
                                updatedMap.put(id, true);
                            }
                            somethingChanged = true;
                            final String messageIds = String.join(",", existsConversationsMessageIds);
                            MessagesByIdResponse messagesByIdResponse =
                                    MessagesByIdResponse.call(messageIds);
                            mergeExtendedArrays(messagesByIdResponse.getExtendedArrays());
                            long updateTime = userLongPollEventsResponse.getTs();
                            for (Message message : messagesByIdResponse.getMessages()) {
                                ConversationBundle conversationBundle = conversationBundleMap.get(message.getPeerId());
                                if (conversationBundle != null) {
                                    if(conversationBundle.addMessage(message)){
                                        if(conversationBundle.listIsLoaded()){
                                            conversationBundle.setCount(conversationBundle.getCount()+1);
                                            conversationBundle.sortMessagesById();
                                        }
                                        conversationBundle.setUpdateTime(updateTime);
                                        ChatActions chatActions = conversationBundle.getChatActions();
                                        if(chatActions!=null){
                                            chatActions.removeMemberId(message.getFromId(), ChatActions.Type.MIXED);
                                            if(chatActions.getAverageType()== ChatActions.Type.EMPTY){
                                                conversationBundle.setChatAction(null);
                                            }
                                        }
                                        Conversation conversation  = conversationBundle.getConversation();
                                        conversation.setLastConversationMessageId(message.getConversationMessageId());
                                        conversation.setLastMessageId(message.getId());
                                        conversation.setUnreadCount(message.isOut()?0:conversation.getUnreadCount() + 1);
                                    }
                                }
                            }
                        }

                        if(somethingChanged) {
                            sortConversationByLastMessageDate();
                        }

                        return true;
                    }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
                    sequenceOfSingle.addUnder(lastSingle, single, o -> {}, throwable -> {});
                    lastSingle = single;
                }

                List<TypingMessageEvent> typingMessageEvents = userLongPollEventsResponse.getTypingMessageEvents();
                for (TypingMessageEvent event : typingMessageEvents){
                    Log.d(LOG_TAG,"TypingMessageEvent applying");
                    ConversationBundle conversationBundle = conversationBundleMap.get(event.getPeerId());
                    if (conversationBundle != null) {
                        long currentTime = System.currentTimeMillis();
                        long delay = 5600;

                        ChatActions chatActions = conversationBundle.getChatActions();
                        if(chatActions==null){
                            chatActions = new ChatActions();
                            conversationBundle.setChatAction(chatActions);
                        }
                        if(chatActions.addMemberIds(event.getUserIds(), ChatActions.Type.TYPING_MESSAGE)){
                            conversationBundle.setUpdateTime(currentTime);
                            notifyAllSubscribersAboutSuccess(event.getPeerId());
                        }

                        String key = "typing_message_"+event.getPeerId()+"_"+String.join("_",event.getUserIds());

                        Completable completable = Completable.timer(delay, TimeUnit.MILLISECONDS)
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread());

                        Action action = () -> {
                            conversationActionsDisposableMap.remove(key);

                            sequenceOfSingle.addToTop(Single.just((Object) true)
                                .observeOn(AndroidSchedulers.mainThread()),
                                    ignore12 -> {
                                        if(!isEmpty()){
                                            ConversationBundle conversationBundle1 = conversationBundleMap.get(event.getPeerId());
                                            if (conversationBundle1 != null) {
                                                ChatActions chatActions1 = conversationBundle1.getChatActions();
                                                if(chatActions1 !=null){
                                                    if(chatActions1.removeMemberIds(event.getUserIds(),ChatActions.Type.TYPING_MESSAGE)){
                                                        if(chatActions1.getAverageType()==ChatActions.Type.EMPTY){
                                                            conversationBundle1.setChatAction(null);
                                                        }
                                                        conversationBundle1.setUpdateTime(System.currentTimeMillis());
                                                        notifyAllSubscribersAboutSuccess(event.getPeerId());
                                                    }
                                                }
                                            }
                                        }
                                    }, throwable -> {});
                        };

                        Disposable disposable = conversationActionsDisposableMap.put(key,completable.subscribe(action));
                        if(disposable!=null&&!disposable.isDisposed()){
                            disposable.dispose();
                        }
                    }
                }

                List<RecordingVoiceMessageEvent> recordingVoiceMessageEvents = userLongPollEventsResponse.getRecordingVoiceMessageEvents();
                for (RecordingVoiceMessageEvent event : recordingVoiceMessageEvents){
                    Log.d(LOG_TAG,"RecordingVoiceMessageEvent applying");
                    ConversationBundle conversationBundle = conversationBundleMap.get(event.getPeerId());
                    if (conversationBundle != null) {
                        long currentTime = System.currentTimeMillis();
                        long delay = 5600;

                        ChatActions chatActions = conversationBundle.getChatActions();
                        if(chatActions==null){
                            chatActions = new ChatActions();
                            conversationBundle.setChatAction(chatActions);
                        }
                        if(chatActions.addMemberIds(event.getUserIds(), ChatActions.Type.RECORDING_VOICE_MESSAGE)){
                            conversationBundle.setUpdateTime(currentTime);
                            notifyAllSubscribersAboutSuccess(event.getPeerId());
                        }

                        String key = "recording_voice_message_"+event.getPeerId()+"_"+String.join("_",event.getUserIds());

                        Completable completable = Completable.timer(delay, TimeUnit.MILLISECONDS)
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread());

                        Action action = () -> {
                            conversationActionsDisposableMap.remove(key);

                            sequenceOfSingle.addToTop(Single.just((Object) true)
                                            .observeOn(AndroidSchedulers.mainThread()),
                                    ignore12 -> {
                                        if(!isEmpty()){
                                            ConversationBundle conversationBundle1 = conversationBundleMap.get(event.getPeerId());
                                            if (conversationBundle1 != null) {
                                                ChatActions chatActions1 = conversationBundle1.getChatActions();
                                                if(chatActions1 !=null){
                                                    if(chatActions1.removeMemberIds(event.getUserIds(),ChatActions.Type.RECORDING_VOICE_MESSAGE)){
                                                        if(chatActions1.getAverageType()==ChatActions.Type.EMPTY){
                                                            conversationBundle1.setChatAction(null);
                                                        }
                                                        conversationBundle1.setUpdateTime(System.currentTimeMillis());
                                                        notifyAllSubscribersAboutSuccess(event.getPeerId());
                                                    }
                                                }
                                            }
                                        }
                                    }, throwable -> {});
                        };

                        Disposable disposable = conversationActionsDisposableMap.put(key,completable.subscribe(action));
                        if(disposable!=null&&!disposable.isDisposed()){
                            disposable.dispose();
                        }
                    }
                }

                Single<Object> finallySingle = Single.fromCallable(()->(Object)true)
                        .observeOn(AndroidSchedulers.mainThread());
                sequenceOfSingle.addUnder(lastSingle, finallySingle, o -> {
                    if(!updatedMap.isEmpty()){
                        Log.d(LOG_TAG,"Есть изменения");
                        notifyAllSubscribersAboutSuccess(updatedMap);
                        sequenceOfSingle.addUnder(finallySingle, saveConversationsToMemory(),
                                ignore1 -> Log.d(LOG_TAG,"Изменения сохранены в память"), throwable -> {});
                    } else {
                        Log.d(LOG_TAG,"Изменений нет");
                    }
                }, throwable -> {});
                lastSingle = finallySingle;
                sequenceOfSingle.addUnder(lastSingle, success, ignore1 -> {}, throwable -> {});

            }
        }, throwable -> {});
    }

    //--------------------------------------------------//

    private Single<Object> loadConversationsFromMemorySingle(){
        return Single.fromCallable((Callable<Object>) () ->
                        CachedConversationsResponse.callFromMemory(userDir))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Consumer<Object> loadConversationsFromMemoryConsumer(Single<Object> single){
        return object -> {
            CachedConversationsResponse response = (CachedConversationsResponse) object;
            count = response.getCount();
            extendedArrays = response.getExtendedArrays();
            conversationBundles = response.getConversationBundles();
            conversationBundleMap = response.getConversationBundleMap();

            Single<Object> prevSingle = single;

            if(!specificSubscribersMap.isEmpty()){
                for (Map.Entry<String,?> entry: specificSubscribersMap.entrySet()){
                    if(isEmpty()||!conversationBundleMap.containsKey(entry.getKey())) {
                        specificLoadingMap.put(entry.getKey(), true);
                        Single<Object> single1 = loadSpecificConversationFromServerSingle(entry.getKey());
                        Consumer<Object> consumer = loadSpecificConversationFromServerConsumer(single1, entry.getKey());
                        Consumer<Throwable> onError = loadSpecificConversationFromServerOnError(single1, entry.getKey());
                        sequenceOfSingle.addUnder(prevSingle, single1, consumer, onError);
                        prevSingle = single1;
                    } else {
                        notifySpecificSubscribersAboutSuccess(entry.getKey());
                    }
                }
            }

            if(listIsLoaded()){
                loading = false;
                if(!subscribers.isEmpty()){
                    notifySubscribersAboutSuccess();
                }
            } else {
                if(!subscribers.isEmpty()){
                    loading = true;
                    Single<Object> single1 = loadConversationsFromServerSingle();
                    Consumer<Object> consumer = loadConversationsFromServerConsumer(single1);
                    Consumer<Throwable> onError = loadConversationsFromServerOnError(single1);
                    sequenceOfSingle.addUnder(prevSingle, single1, consumer, onError);
                }
            }
        };
    }

    private Consumer<Throwable> loadConversationsFromMemoryOnError(Single<Object> single){
        return throwable -> {
            Log.d(LOG_TAG,"Невозможно выгрузить переписки из памяти");

            Single<Object> prevSingle = single;

            if(!specificSubscribersMap.isEmpty()){
                for (Map.Entry<String,?> entry: specificSubscribersMap.entrySet()){
                    specificLoadingMap.put(entry.getKey(), true);
                    Single<Object> single1 = loadSpecificConversationFromServerSingle(entry.getKey());
                    Consumer<Object> consumer = loadSpecificConversationFromServerConsumer(single1, entry.getKey());
                    Consumer<Throwable> onError = loadSpecificConversationFromServerOnError(single1, entry.getKey());
                    sequenceOfSingle.addUnder(prevSingle, single1, consumer, onError);
                    prevSingle = single1;
                }
            }

            if(subscribers.isEmpty()){
                loading = false;
            } else {
                Single<Object> single1 = loadConversationsFromServerSingle();
                Consumer<Object> consumer = loadConversationsFromServerConsumer(single1);
                Consumer<Throwable> onError = loadConversationsFromServerOnError(single1);
                sequenceOfSingle.addUnder(prevSingle, single1, consumer, onError);
            }
        };
    }

    //++++++++++++++++++++++++++++++++++++++++++++++//

    private Single<Object> loadConversationsFromServerSingle(){
        int offset = isEmpty()?0:conversationBundles.size();
        return Single.fromCallable((Callable<Object>) () ->
                        ConversationsResponse.call(offset, step, null, "all"))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Consumer<Object> loadConversationsFromServerConsumer(Single<Object> single){
        return object -> {
            Log.d(LOG_TAG,"Переписки загружены с сервера");
            ConversationsResponse response = (ConversationsResponse) object;
            count = response.getCount();
            mergeExtendedArrays(response.getExtendedArrays());
            mergeConversations(response.getConversationBundles());
            loading = false;
            notifySubscribersAboutSuccess();
            sequenceOfSingle.addUnder(single, saveConversationsToMemory(),
                    o -> Log.d(LOG_TAG,"Изменения сохранены в память"), throwable -> {});
        };
    }

    private Consumer<Throwable> loadConversationsFromServerOnError(Single<Object> single){
        return throwable -> {
            loading = false;
            notifySubscribersAboutError(throwable);
        };
    }

    //++++++++++++++++++++++++++++++++++++++++++++++//

    private Single<Object> loadSpecificConversationFromServerSingle(String peerId){
        return Single.fromCallable((Callable<Object>) () -> {
            if(isEmpty()){
                return MessagesHistoryResponse.call(peerId,0,step,null);
            } else {
                ConversationBundle conversationBundle = conversationBundleMap.get(peerId);
                String startMessageId = null;
                if(conversationBundle!=null){
                    List<Message> messages = conversationBundle.getMessages();
                    Message message = messages.get(messages.size() - 1);
                    startMessageId = message.getId();
                }
                int offset = startMessageId==null?0:1;
                return MessagesHistoryResponse.call(peerId,offset,step,startMessageId);
            }
       }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

    private Consumer<Object> loadSpecificConversationFromServerConsumer(Single<Object> single, String peerId){
        return object -> {
            Log.d(LOG_TAG,"Переписка "+peerId+" загружена с сервера");
            MessagesHistoryResponse response = (MessagesHistoryResponse) object;
            mergeExtendedArrays(response.getExtendedArrays());
            if(isEmpty()){
                conversationBundles = new ArrayList<>();
                conversationBundleMap = new ArrayMap<>();
                conversationBundleMap.put(peerId, new ConversationBundle(response));
            } else {
                ConversationBundle conversationBundle = conversationBundleMap.get(peerId);
                if(conversationBundle!=null){
                    conversationBundle.mergeMessages(response.getMessages());
                    conversationBundle.setConversation(response.getConversation());
                    conversationBundle.setCount(response.getCount());
                    conversationBundle.setUpdateTime(System.currentTimeMillis());
                } else {
                    conversationBundleMap.put(peerId, new ConversationBundle(response));
                }
            }
            specificLoadingMap.put(peerId, false);
            notifySpecificSubscribersAboutSuccess(peerId);
            sequenceOfSingle.addUnder(single, saveConversationsToMemory(),
                    o -> Log.d(LOG_TAG,"Изменения сохранены в память"), throwable -> {});
        };
    }

    private Consumer<Throwable> loadSpecificConversationFromServerOnError(Single<Object> single, String peerId){
        return throwable -> {
            specificLoadingMap.put(peerId, false);
            notifySpecificSubscribersAboutError(peerId, throwable);
        };
    }

    //++++++++++++++++++++++++++++++++++++++++++++++//

    private Single<Object> saveConversationsToMemory(){
        return Single.fromCallable((Callable<Object>) () -> {
            UsersStorage usersStorage = UsersStorage.get();
                    CachedConversationsResponse conversationsResponse =
                    new CachedConversationsResponse(count, conversationBundleMap, conversationBundles, extendedArrays);
            usersStorage.saveConversationsCache(conversationsResponse, userDir);
            return true;
        }).subscribeOn(Schedulers.newThread())
          .observeOn(AndroidSchedulers.mainThread());
    }

    private Single<Object> removeConversationsFromMemory(){
        return Single.fromCallable((Callable<Object>) () -> {
                    UsersStorage usersStorage = UsersStorage.get();
                    usersStorage.clearConversationsCache(userDir);
                    return true;
                }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    //--------------------------------------------------//

    private boolean isEmpty(){
        return conversationBundles ==null||conversationBundleMap ==null||extendedArrays==null;
    }

    public boolean finallyLoaded(){
        return !isEmpty()&&listIsLoaded()&&conversationBundles.size()>=count;
    }

    public boolean listIsLoaded(){
        return count>=0;
    }

    private void sortConversationByLastMessageDate(){
        conversationBundles.sort((o1, o2) -> Long.compare(o2.getLastMessageDate(), o1.getLastMessageDate()));
    }

    private void mergeConversations(List<ResponseConversationBundle> responseConversationBundles){
        if(this.conversationBundles ==null||conversationBundleMap ==null){
            conversationBundles = new ArrayList<>();
            conversationBundleMap = new ArrayMap<>();
        }
        for (ResponseConversationBundle bundle:responseConversationBundles) {
            ConversationBundle previous = conversationBundleMap.get(bundle.getPeerId());
            if(previous==null){
                ConversationBundle conversationBundle = new ConversationBundle(bundle);
                conversationBundles.add(conversationBundle);
                conversationBundleMap.put(bundle.getPeerId(), conversationBundle);
            } else {
                if (!conversationBundles.contains(previous)) {
                    conversationBundles.add(previous);
                }
            }
        }
    }

    private void mergeExtendedArrays(ExtendedArrays extendedArrays){
        if(this.extendedArrays==null){
            this.extendedArrays = extendedArrays;
        } else {
            this.extendedArrays.merge(extendedArrays);
        }
    }

    //--------------------------------------------------//

    private void notifyAllSubscribersAboutSuccess(Map<String,Boolean> updated){
        boolean listNotified = false;
        for (Map.Entry<String,Boolean> entry: updated.entrySet()) {
            ConversationBundle conversationBundle = conversationBundleMap.get(entry.getKey());
            if(conversationBundle!=null){
                if(!subscribers.isEmpty()&&listIsLoaded()&&!listNotified
                        &&conversationBundles.contains(conversationBundle)){
                    listNotified = true;
                    ListConversationsResponse conversationsResponse = createResponse();
                    for (PublishSubject<ListConversationsResponse> subscriber:subscribers) {
                        subscriber.onNext(conversationsResponse);
                    }
                }
                List<PublishSubject<SpecificConversationResponse>> specificSubscribers =
                        specificSubscribersMap.get(entry.getKey());
                if(specificSubscribers!=null){
                    SpecificConversationResponse response = conversationBundle.createSpecificResponse(extendedArrays);
                    for (PublishSubject<SpecificConversationResponse> subscriber : specificSubscribers) {
                        subscriber.onNext(response);
                    }
                }
            }
        }
    }

    private void notifyAllSubscribersAboutSuccess(String peerId){
        ConversationBundle conversationBundle = conversationBundleMap.get(peerId);
        if(conversationBundle!=null){
            if(!subscribers.isEmpty()&&listIsLoaded()
                    &&conversationBundles.contains(conversationBundle)){
                ListConversationsResponse conversationsResponse = createResponse();
                for (PublishSubject<ListConversationsResponse> subscriber:subscribers) {
                    subscriber.onNext(conversationsResponse);
                }
            }
            List<PublishSubject<SpecificConversationResponse>> specificSubscribers =
                    specificSubscribersMap.get(peerId);
            if(specificSubscribers!=null){
                SpecificConversationResponse response = conversationBundle.createSpecificResponse(extendedArrays);
                for (PublishSubject<SpecificConversationResponse> subscriber : specificSubscribers) {
                    subscriber.onNext(response);
                }
            }
        }
    }

    //--------------------------------------------------//

    private ListConversationsResponse createResponse(){
        List<ListConversationBundle> listConversationBundles = new ArrayList<>();
        for (ConversationBundle c: conversationBundles) {
            listConversationBundles.add(c.createListBundle());
        }
        return new ListConversationsResponse(count, listConversationBundles,extendedArrays);
    }

    private void notifySubscribersAboutSuccess(){
        if(!subscribers.isEmpty()) {
            ListConversationsResponse conversationsResponse = createResponse();
            for (PublishSubject<ListConversationsResponse> subscriber:subscribers) {
                subscriber.onNext(conversationsResponse);
            }
        }
    }

    private void notifySubscribersAboutError(Throwable throwable){
        if(!subscribers.isEmpty()) {
            for (PublishSubject<ListConversationsResponse> subscriber:subscribers) {
                subscriber.onError(throwable);
            }
        }
    }
    
    public void subscribe(PublishSubject<ListConversationsResponse> subscriber) {
        subscribers.add(subscriber);
        if(!isEmpty()&&listIsLoaded()) {
            subscriber.onNext(createResponse());
        } else {
            needNext();
        }
    }

    public void unsubscribe(PublishSubject<ListConversationsResponse> publishSubject){
        subscribers.remove(publishSubject);
    }

    public boolean needNext(){
        if(!loading){
            if(memoryIsChecked){
                if(!finallyLoaded()){
                    loading = true;
                    Single<Object> single = loadConversationsFromServerSingle();
                    Consumer<Object> consumer = loadConversationsFromServerConsumer(single);
                    Consumer<Throwable> onError = loadConversationsFromServerOnError(single);
                    sequenceOfSingle.addToTop(single, consumer, onError);
                    return true;
                }
            } else {
                memoryIsChecked = true;
                loading = true;
                Single<Object> single = loadConversationsFromMemorySingle();
                Consumer<Object> consumer = loadConversationsFromMemoryConsumer(single);
                Consumer<Throwable> onError = loadConversationsFromMemoryOnError(single);
                sequenceOfSingle.add(single,consumer,onError);
                return true;
            }
        }
        return false;
    }

    //--------------------------------------------------//

    private void notifySpecificSubscribersAboutSuccess(String peerId){
        List<PublishSubject<SpecificConversationResponse>> specificSubscribers = specificSubscribersMap.get(peerId);
        if(specificSubscribers!=null){
            ConversationBundle conversationBundle = conversationBundleMap.get(peerId);
            if(conversationBundle!=null){
                SpecificConversationResponse response = conversationBundle.createSpecificResponse(extendedArrays);
                for (PublishSubject<SpecificConversationResponse> subscriber : specificSubscribers) {
                    subscriber.onNext(response);
                }
            }
        }
    }

    private void notifySpecificSubscribersAboutError(String peerId, Throwable throwable){
        List<PublishSubject<SpecificConversationResponse>> specificSubscribers = specificSubscribersMap.get(peerId);
        if(specificSubscribers!=null){
            for (PublishSubject<SpecificConversationResponse> subscriber : specificSubscribers) {
                subscriber.onError(throwable);
            }
        }
    }

    public void subscribe(PublishSubject<SpecificConversationResponse> subscriber, String peerId){
        List<PublishSubject<SpecificConversationResponse>> specificSubscribers = specificSubscribersMap.get(peerId);
        if(specificSubscribers==null) {
            specificSubscribers = new ArrayList<>();
            specificSubscribersMap.put(peerId, specificSubscribers);
        }
        specificSubscribers.add(subscriber);
        if(!isEmpty()) {
            ConversationBundle conversationBundle = conversationBundleMap.get(peerId);
            if(conversationBundle!=null&&conversationBundle.listIsLoaded()){
                subscriber.onNext(conversationBundle.createSpecificResponse(extendedArrays));
                return;
            }
        }
        needNext(peerId);
    }

    public void unsubscribe(PublishSubject<SpecificConversationResponse> subscriber, String peerId){
        List<PublishSubject<SpecificConversationResponse>> specificSubscribers = specificSubscribersMap.get(peerId);
        if(specificSubscribers!=null){
            specificSubscribers.remove(subscriber);
        }
    }

    public boolean needNext(String peerId){
        if(isEmpty()){
            if(memoryIsChecked){
                Boolean loading = specificLoadingMap.get(peerId);
                if(loading == Boolean.TRUE) {
                    return false;
                }

                specificLoadingMap.put(peerId, true);
                Single<Object> single = loadSpecificConversationFromServerSingle(peerId);
                Consumer<Object> consumer = loadSpecificConversationFromServerConsumer(single,peerId);
                Consumer<Throwable> onError = loadSpecificConversationFromServerOnError(single,peerId);
                sequenceOfSingle.addToTop(single,consumer,onError);
                return true;
            } else {
                if(!loading) {
                    memoryIsChecked = true;
                    loading = true;
                    Single<Object> single = loadConversationsFromMemorySingle();
                    Consumer<Object> consumer = loadConversationsFromMemoryConsumer(single);
                    Consumer<Throwable> onError = loadConversationsFromMemoryOnError(single);
                    sequenceOfSingle.add(single, consumer, onError);
                    return true;
                }
            }
        } else {
            ConversationBundle conversationBundle = conversationBundleMap.get(peerId);
            if (conversationBundle != null && conversationBundle.finallyLoaded()) {
                return false;
            }
            Boolean loading = specificLoadingMap.get(peerId);
            if(loading == Boolean.TRUE) {
                return false;
            }

            specificLoadingMap.put(peerId, true);
            Single<Object> single = loadSpecificConversationFromServerSingle(peerId);
            Consumer<Object> consumer = loadSpecificConversationFromServerConsumer(single,peerId);
            Consumer<Throwable> onError = loadSpecificConversationFromServerOnError(single,peerId);
            sequenceOfSingle.addToTop(single,consumer,onError);
            return true;
        }
        return false;
    }
}
