package com.miracle.engine.activity;

import androidx.appcompat.app.AppCompatActivity;

public abstract class MiracleActivityController<T extends AppCompatActivity> {

    private final T activity;

    protected MiracleActivityController(T activity) {
        this.activity = activity;
    }

    public T getActivity() {
        return activity;
    }


}