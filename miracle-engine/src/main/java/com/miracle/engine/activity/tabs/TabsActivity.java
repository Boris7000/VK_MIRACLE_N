package com.miracle.engine.activity.tabs;

import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.miracle.engine.R;
import com.miracle.engine.activity.MiracleActivity;

public class TabsActivity extends MiracleActivity implements ITabsActivity{

    private TabsActivityController tabsFragmentController;

    @Override
    public TabsActivityController getTabsController() {
        return tabsFragmentController;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tabsFragmentController = new TabsActivityController(this,
                R.id.fragmentContainer, savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if (!getTabsController().stepBack()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getTabsController().saveState(outState);
    }

}
