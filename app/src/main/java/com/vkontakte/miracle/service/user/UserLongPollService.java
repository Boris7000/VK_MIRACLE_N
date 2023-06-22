package com.vkontakte.miracle.service.user;

import android.util.Log;

import androidx.annotation.Nullable;

import com.vkontakte.miracle.memory.storage.UsersStorage;
import com.vkontakte.miracle.model.auth.User;
import com.vkontakte.miracle.response.longpoll.UserLongPollEventsResponse;
import com.vkontakte.miracle.response.longpoll.UserLongPollServerResponse;

import java.io.File;
import java.net.SocketTimeoutException;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UserLongPollService {

    private static UserLongPollService instance;
    private final static String LOG_TAG = "UserLongPollModel";

    //--------------------------------------------------//

    private final static int MODE_GET_ATTACHMENTS = 2;
    private final static int MODE_GET_EXTENDED_EVENTS = 8;
    private final static int MODE_GET_PTS = 32;
    private final static int MODE_GET_EXTRA_EXTENDED_EVENTS = 64;
    private final static int MODE_GET_RANDOM_ID = 128;

    private final static int MODE =
            MODE_GET_ATTACHMENTS
                    +MODE_GET_EXTENDED_EVENTS
                    +MODE_GET_EXTRA_EXTENDED_EVENTS
                    +MODE_GET_RANDOM_ID;

    private final static int lpVersion = 10;

    private final static int FAILED_SUCCESS = 0;
    private final static int FAILED_DEPRECATED = 1;
    private final static int FAILED_KEY_DEPRECATED = 2;
    private final static int FAILED_USER_DEPRECATED = 3;
    private final static int FAILED_INVALID_VERSION = 4;

    //--------------------------------------------------//

    private String server;
    private String key;
    private long ts;

    //--------------------------------------------------//

    private User user;
    private File userDir;

    //--------------------------------------------------//

    private Disposable serverDisposable;
    private Disposable serverCacheDisposable;

    //--------------------------------------------------//

    public static UserLongPollService get(){
        if (null == instance){
            instance = new UserLongPollService();
        }
        return instance;
    }

    //--------------------------------------------------//

    public void changeUser(@Nullable User newUser, @Nullable File newUserDir){
        if (newUser != null && newUserDir != null) {
            if (user == null || !user.getId().equals(newUser.getId())) {
                if (serverDisposable != null) {
                    serverDisposable.dispose();
                }
                if (serverCacheDisposable != null) {
                    serverCacheDisposable.dispose();
                }
                user = newUser;
                userDir = newUserDir;
                serverDisposable = Single.fromCallable(() ->
                                UserLongPollServerResponse.callFromMemory(userDir))
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(cache -> {
                            server = cache.getServer();
                            key = cache.getKey();
                            ts = cache.getTs();
                            startUpdatesChecking();
                        }, throwable -> updateServer());
            }
        } else {
            if (serverDisposable != null) {
                serverDisposable.dispose();
            }
            if (serverCacheDisposable != null) {
                serverCacheDisposable.dispose();
            }
            user = null;
            userDir = null;
            server = null;
            key = null;
            ts = 0;
        }
    }

    //--------------------------------------------------//

    private void startUpdatesChecking(){
        serverDisposable = Single.fromCallable(() -> UserLongPollEventsResponse.call(server,key,ts,MODE,lpVersion))
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    switch (response.getFailed()){
                        case FAILED_SUCCESS:{
                            ts = response.getTs();
                            Single<Object> single = Single.fromCallable((Callable<Object>) () -> {
                                UserLongPollServerResponse serverCache =
                                        new UserLongPollServerResponse(server, key, response.getTs());
                                UsersStorage.get().saveUserLongPollServerCache(serverCache, userDir);
                                Log.d(LOG_TAG,"updates saved");
                                return true;
                            }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
                            ConversationsModel.get().applyUpdates(response, single);
                            startUpdatesChecking();
                            break;
                        }
                        case FAILED_DEPRECATED:{
                            ts = response.getTs();
                            ConversationsModel.get().clear();
                            startUpdatesChecking();
                            break;
                        }
                        case FAILED_KEY_DEPRECATED:
                        case FAILED_USER_DEPRECATED:{
                            ConversationsModel.get().clear();
                            updateServer();
                            break;
                        }
                        case FAILED_INVALID_VERSION:{
                            break;
                        }
                    }
                }, throwable -> {
                    if(throwable instanceof SocketTimeoutException) {
                        startUpdatesChecking();
                        Log.d(LOG_TAG,"time out");
                    } else {
                        Log.d(LOG_TAG,"startUpdatesChecking error "+throwable.toString());
                    }
                });
    }

    private void updateServer(){
        serverDisposable = Single.fromCallable(() -> UserLongPollServerResponse.call(0, lpVersion))
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    server = "https://"+response.getServer();
                    key = response.getKey();
                    ts = response.getTs();
                    saveServerCache();
                    startUpdatesChecking();
                }, throwable -> Log.d(LOG_TAG,"updateServer error "+throwable.toString()));
    }

    private void saveServerCache(){
        serverCacheDisposable = Completable.fromAction(() -> {
            UserLongPollServerResponse serverCache = new UserLongPollServerResponse(server, key, ts);
            UsersStorage.get().saveUserLongPollServerCache(serverCache, userDir);
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe();
    }

    //--------------------------------------------------//
}
