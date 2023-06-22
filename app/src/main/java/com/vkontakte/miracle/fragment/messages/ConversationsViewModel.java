package com.vkontakte.miracle.fragment.messages;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.vkontakte.miracle.model.general.ExtendedArrays;
import com.vkontakte.miracle.model.messages.ListConversationBundle;
import com.vkontakte.miracle.response.messages.local.ListConversationsResponse;
import com.vkontakte.miracle.service.user.ConversationsModel;

import java.util.List;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class ConversationsViewModel extends ViewModel {

    private final Disposable disposable;

    private boolean finallyLoaded = false;

    public final MutableLiveData<LoadingType> loadingType = new MutableLiveData<>(LoadingType.FIRST_TIME);
    public final MutableLiveData<ViewState> viewState = new MutableLiveData<>(new ViewState.Loading());

    private final PublishSubject<ListConversationsResponse> subscriber = PublishSubject.create();

    {
        disposable = subscriber
                .subscribe(response -> {
                    finallyLoaded = response.getConversationBundles().size()>=response.getCount();
                    loadingType.setValue(LoadingType.NO_LOADING);
                    viewState.setValue(new ViewState.Success(
                            !finallyLoaded,
                            response.getConversationBundles(),
                            response.getExtendedArrays()));
                }, throwable -> {
                    loadingType.setValue(LoadingType.NO_LOADING);
                    viewState.setValue(new ViewState.Error(throwable.getMessage()));
                });
        ConversationsModel.get().subscribe(subscriber);
    }

    public void tryLoadMore(){
        if(!finallyLoaded&& ConversationsModel.get().needNext()){
            loadingType.setValue(LoadingType.LOADING_NEXT);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        ConversationsModel.get().unsubscribe(subscriber);

        if(disposable!=null&&!disposable.isDisposed()){
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

            private final List<ListConversationBundle> conversations;

            private final ExtendedArrays extendedArrays;

            public Success(boolean hasNext, List<ListConversationBundle> conversations, ExtendedArrays extendedArrays) {
                this.hasNext = hasNext;
                this.conversations = conversations;
                this.extendedArrays = extendedArrays;
            }

            public boolean hasNext() {
                return hasNext;
            }

            public List<ListConversationBundle> getConversations() {
                return conversations;
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
