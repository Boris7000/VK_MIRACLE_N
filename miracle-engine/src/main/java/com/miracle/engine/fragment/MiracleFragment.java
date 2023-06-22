package com.miracle.engine.fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.miracle.engine.activity.tabs.ITabsActivity;
import com.miracle.engine.activity.tabs.TabsActivityController;

public class MiracleFragment extends Fragment {

    private String entryPointId = null;

    private boolean canBackClick = false;

    @Nullable
    public String getEntryPointId() {
        return entryPointId;
    }

    public void setEntryPointId(String entryPointId) {
        this.entryPointId = entryPointId;
    }

    public boolean isCanBackClick() {
        return canBackClick;
    }

    public void setCanBackClick(boolean canBackClick) {
        this.canBackClick = canBackClick;
    }

    @Nullable
    public static TabsActivityController findTabsNavigation(Fragment fragment){
        Activity activity = fragment.getActivity();
        if(activity instanceof ITabsActivity) {
            return ((ITabsActivity)activity).getTabsController();
        }
        return null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null&&!savedInstanceState.isEmpty()){
            canBackClick = savedInstanceState.getBoolean("canBackClick");
            entryPointId = savedInstanceState.getString("entryPointId");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(canBackClick) {
            outState.putBoolean("canBackClick", true);
        }
        if(entryPointId!=null) {
            outState.putString("entryPointId", entryPointId);
        }
    }
}
