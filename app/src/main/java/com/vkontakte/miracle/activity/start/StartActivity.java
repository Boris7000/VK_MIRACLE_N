package com.vkontakte.miracle.activity.start;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.vkontakte.miracle.activity.auth.AuthActivity;
import com.vkontakte.miracle.activity.main.MainActivity;
import com.vkontakte.miracle.memory.storage.UsersStorage;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (UsersStorage.get().authorized()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, AuthActivity.class);
            startActivity(intent);
        }
        this.finish();
    }
}