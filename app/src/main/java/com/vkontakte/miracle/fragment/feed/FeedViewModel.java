package com.vkontakte.miracle.fragment.feed;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.vkontakte.miracle.model.general.ExtendedArrays;
import com.vkontakte.miracle.model.wall.Post;
import com.vkontakte.miracle.response.wall.FeedResponse;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FeedViewModel extends ViewModel {

    private Disposable disposable;

    private static final int step = 25;
    private String nextFrom = "";
    private boolean loading = true;
    private boolean finallyLoaded = false;

    private final ArrayList<Post> posts = new ArrayList<>();
    private final ExtendedArrays extendedArrays = new ExtendedArrays();

    public final MutableLiveData<LoadingType> loadingType = new MutableLiveData<>(LoadingType.FIRST_TIME);
    public final MutableLiveData<ViewState> viewState = new MutableLiveData<>(new ViewState.Loading());

    { load(); }

    public boolean tryRefresh(){
        if(checkAndStartLoading(LoadingType.REFRESHING)){
            clearData();
            load();
            return true;
        }
        return false;
    }

    private void clearData(){
        nextFrom = "";
        finallyLoaded = false;
        extendedArrays.clear();
        posts.clear();
        viewState.setValue(new ViewState.Loading());
    }

    public boolean tryLoadMore(){
        if(!finallyLoaded&&checkAndStartLoading(LoadingType.LOADING_NEXT)){
            load();
            return true;
        }
        return false;
    }

    private void load(){
        disposable = Single.fromCallable(() -> FeedResponse.call(nextFrom, step))
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(feedResponse -> {
                    endLoading();
                    extendedArrays.merge(feedResponse.getExtendedArrays());
                    posts.addAll(feedResponse.getPosts());
                    nextFrom = feedResponse.getNextFrom();
                    finallyLoaded = nextFrom.isEmpty();
                    if(posts.isEmpty()){
                        viewState.setValue(new ViewState.Empty());
                    } else {
                        viewState.setValue(new ViewState.Success(!finallyLoaded,posts,extendedArrays));
                    }
                }, throwable -> {
                    endLoading();
                    finallyLoaded = true;
                    viewState.setValue(new ViewState.Error(throwable.getMessage()));
                });
    }

    private boolean checkAndStartLoading(LoadingType loadingType){
        if(loading) return false;
        loading = true;
        this.loadingType.setValue(loadingType);
        return true;
    }

    private void endLoading(){
        loading = false;
        loadingType.setValue(LoadingType.NO_LOADING);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if(disposable!=null&&!disposable.isDisposed()){
            disposable.dispose();
        }
    }

    enum LoadingType{
        FIRST_TIME,
        REFRESHING,
        LOADING_NEXT,
        NO_LOADING
    }

    public static class ViewState {

        public static class Loading extends ViewState { }

        public static class Success extends ViewState {

            private final boolean hasNext;

            private final ArrayList<Post> posts;

            private final ExtendedArrays extendedArrays;

            public Success(boolean hasNext, ArrayList<Post> posts, ExtendedArrays extendedArrays) {
                this.hasNext = hasNext;
                this.posts = posts;
                this.extendedArrays = extendedArrays;
            }

            public boolean hasNext() {
                return hasNext;
            }

            public ArrayList<Post> getPosts() {
                return posts;
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
