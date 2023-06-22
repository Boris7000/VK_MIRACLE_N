package com.miracle.engine.fragment.appbar;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.miracle.engine.R;
import com.miracle.engine.activity.tabs.TabsActivityController;
import com.miracle.engine.fragment.MiracleFragment;
import com.miracle.engine.fragment.MiracleFragmentController;

public class AppBarFragmentController<T extends MiracleFragment> extends MiracleFragmentController<T> {

    private AppBarLayout appBarLayout;
    private Toolbar toolBar;
    private TextView title;

    protected AppBarFragmentController(T fragment) {
        super(fragment);
    }

    public void findViews(@NonNull View rootView){
        appBarLayout = rootView.findViewById(R.id.appbar);
        toolBar = appBarLayout.findViewById(R.id.toolbar);
        title = appBarLayout.findViewById(R.id.title);
    }

    public void setTitleText(String titleText){
        if(titleText==null){
            titleText = "";
        }
        if(title!=null) {
            title.setText(titleText);
        } else {
            if(toolBar!=null){
                toolBar.setTitle(titleText);
            }
        }
    }

    public void setTitleTextResId(@StringRes int resId){
        if(resId!=0){
            Context context = getFragment().getContext();
            if(context!=null){
                setTitleText(context.getString(resId));
            }
        }
    }

    public void setUpBackClick(){
        MiracleFragment fragment = getFragment();
        if(fragment.isCanBackClick()) {
            toolBar.setNavigationIcon(R.drawable.ic_back_28);
            toolBar.setNavigationContentDescription(android.R.string.cancel);
            toolBar.setNavigationOnClickListener(v -> {
                TabsActivityController tabsActivityController =
                MiracleFragment.findTabsNavigation(fragment);
                if(tabsActivityController!=null){
                    tabsActivityController.stepBack();
                }
            });
        }
    }

    public AppBarLayout getAppBarLayout() {
        return appBarLayout;
    }

    public Toolbar getToolBar() {
        return toolBar;
    }

    public TextView getTitle() {
        return title;
    }
}
