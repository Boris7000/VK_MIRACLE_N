package com.vkontakte.miracle.model.longpoll.action;

import android.util.Log;

import com.vkontakte.miracle.model.longpoll.LongPollEvent;

import org.json.JSONArray;
import org.json.JSONException;

public class UserOfflineEvent extends LongPollEvent {

    public UserOfflineEvent(JSONArray jsonArray) throws JSONException {
        super(jsonArray);
        Log.d("LongPollService3435","UserOfflineEvent "+ jsonArray);
    }

}
