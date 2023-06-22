package com.vkontakte.miracle.fragment.messages;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.vkontakte.miracle.model.general.ExtendedArrays;
import com.vkontakte.miracle.model.messages.Conversation;
import com.vkontakte.miracle.model.messages.Message;
import com.vkontakte.miracle.model.messages.fields.ChatActions;
import com.vkontakte.miracle.response.messages.local.SpecificConversationResponse;
import com.vkontakte.miracle.service.user.ConversationsModel;

import java.util.List;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class ChatViewModel extends ViewModel {

    private Disposable disposable;

    private String peerId = null;

    private boolean finallyLoaded = false;

    public final MutableLiveData<LoadingType> loadingType = new MutableLiveData<>(LoadingType.FIRST_TIME);
    public final MutableLiveData<ViewState> viewState = new MutableLiveData<>(new ViewState.Loading());

    private final PublishSubject<SpecificConversationResponse> subscriber = PublishSubject.create();

    private void subscribe(){
        disposable = subscriber
                .subscribe(response -> {
                    finallyLoaded = response.getMessages().size()>=response.getCount();
                    loadingType.setValue(LoadingType.NO_LOADING);
                    viewState.setValue(new ViewState.Success(
                            !finallyLoaded,
                            response.getConversation(),
                            response.getChatAction(),
                            response.getMessages(),
                            response.getExtendedArrays()));
                }, throwable -> {
                    loadingType.setValue(LoadingType.NO_LOADING);
                    viewState.setValue(new ViewState.Error(throwable.getMessage()));
                });
        ConversationsModel.get().subscribe(subscriber, peerId);
    }

    public void setPeerId(String peerId) {
        if(this.peerId!=null) return;
        this.peerId = peerId;
        subscribe();
    }

    public String getPeerId() {
        return peerId;
    }

    public void tryLoadMore(){
        if(!finallyLoaded&&ConversationsModel.get().needNext(peerId)){
            loadingType.setValue(LoadingType.LOADING_NEXT);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        if(disposable!=null&&!disposable.isDisposed()){
            ConversationsModel.get().unsubscribe(subscriber,peerId);
            disposable.dispose();
        }
    }

    enum LoadingType{
        FIRST_TIME,
        LOADING_NEXT,
        NO_LOADING
    }

    public static class ViewState {

        public static class Loading extends ViewState { }

        public static class Success extends ViewState {

            private final boolean hasNext;

            private final Conversation conversation;
            private final ChatActions chatActions;
            private final List<Message> messages;
            private final ExtendedArrays extendedArrays;

            public Success(boolean hasNext,
                           Conversation conversation,
                           ChatActions chatActions,
                           List<Message> messages,
                           ExtendedArrays extendedArrays) {
                this.hasNext = hasNext;
                this.conversation = conversation;
                this.chatActions = chatActions;
                this.messages = messages;
                this.extendedArrays = extendedArrays;
            }

            public boolean hasNext() {
                return hasNext;
            }

            public Conversation getConversation() {
                return conversation;
            }

            public ChatActions getChatAction() {
                return chatActions;
            }

            public List<Message> getMessages() {
                return messages;
            }

            public ExtendedArrays getExtendedArrays() {
                return extendedArrays;
            }
        }

        public static class Empty extends ViewState { }

        public static class Error extends ViewState {

            private final String errorMessage;

            public Error(String errorMessage) {
                this.errorMessage = errorMessage;
            }

            public String getErrorMessage() {
                return errorMessage;
            }
        }

    }

}
