package com.vkontakte.miracle;

import android.app.Application;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import com.miracle.engine.preferences.AppPreferences;
import com.miracle.engine.preferences.ThemePreferences;
import com.miracle.engine.storage.LargeDataStorage;
import com.vkontakte.miracle.memory.storage.UsersStorage;
import com.vkontakte.miracle.service.user.ConversationsModel;
import com.vkontakte.miracle.service.user.UserLongPollService;

import java.io.File;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public class MainApp extends Application {

    private static MainApp instance;

    @Override
    public void onCreate() {
        instance = this;

        RxJavaPlugins.setErrorHandler(throwable -> {
            Log.d("ehfuehfuehfef","global throwable"+throwable.getMessage());
        });

        new AppPreferences(this);

        ThemePreferences themePreferences = ThemePreferences.get();
        AppCompatDelegate.setDefaultNightMode(themePreferences.nightMode());
        setTheme(themePreferences.themeResourceId());

        UsersStorage usersStorage = new UsersStorage(this.getFilesDir());

        ConversationsModel conversationsModel = ConversationsModel.get();
        UserLongPollService userLongPollService = UserLongPollService.get();

        usersStorage.addOnCurrentUserChangeListener(user -> {
            if(user!=null){
                File userDir = usersStorage.getUsersCachesDir(user.getId());
                conversationsModel.changeUser(user, userDir);
                userLongPollService.changeUser(user, userDir);
            } else {
                userLongPollService.changeUser(null, null);
                conversationsModel.changeUser(null, null);
            }
        }, true);

        new LargeDataStorage();

        super.onCreate();
    }

    public static MainApp get(){
        MainApp localInstance = instance;
        if (localInstance == null) {
            synchronized (MainApp.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new MainApp();
                }
            }
        }
        return localInstance;
    }

}
