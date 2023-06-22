package com.miracle.engine.fragment;

import androidx.fragment.app.Fragment;

public class MiracleFragmentController<T extends Fragment> {

    private final T fragment;

    protected MiracleFragmentController(T fragment) {
        this.fragment = fragment;
    }

    public T getFragment() {
        return fragment;
    }

}