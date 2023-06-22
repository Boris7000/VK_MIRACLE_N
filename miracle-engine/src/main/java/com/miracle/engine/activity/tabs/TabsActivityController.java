package com.miracle.engine.activity.tabs;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.ArrayMap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.miracle.engine.fragment.FragmentFabric;
import com.miracle.engine.fragment.MiracleFragment;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class TabsActivityController {

    private static final String savedStateKey = "TabsSavedState";

    private final FragmentManager fragmentManager;

    private final int fragmentContainerId;

    private final ArrayMap<Integer, FragmentFabric> fabrics = new ArrayMap<>();

    private final ArrayMap<Integer, Tab> tabs = new ArrayMap<>();
    private final ArrayList<Integer> tabsHistory = new ArrayList<>();

    private OnSelfTabChangeListener onSelfTabChangeListener;

    protected TabsActivityController(AppCompatActivity activity, int fragmentContainerId, @Nullable Bundle savedInstanceState) {
        this.fragmentManager = activity.getSupportFragmentManager();
        this.fragmentContainerId = fragmentContainerId;
        if(savedInstanceState!=null) {
            restoreState(savedInstanceState);
        }
    }

    public void setFragmentsFabrics(ArrayMap<Integer, FragmentFabric> fragmentsFabrics){
        for (Map.Entry<Integer, FragmentFabric> set:fragmentsFabrics.entrySet()) {
            fabrics.put(set.getKey(),set.getValue());
            if(tabs.get(set.getKey())==null){
                tabs.put(set.getKey(), new Tab());
            }
        }
        clearTabs();
    }

    public Fragment getCurrentFragment(){
        if(tabsHistory.isEmpty()) return null;

        Tab tab = tabs.get(getLastInHistory());

        if(tab==null) return null;

        if(tab.getCount()==0) return null;

        return tab.getLastFragment();
    }

    public void addFragment(Fragment fragment){
        addFragment(fragment, false);
    }

    public String addFragment(Fragment fragment, boolean entryPoint){
        String entryPointId = null;

        Tab tab = tabs.get(getLastInHistory());

        if(tab==null) return null;

        if(fragment instanceof MiracleFragment){
            MiracleFragment miracleFragment = (MiracleFragment) fragment;
            miracleFragment.setCanBackClick(tab.getCount()>0);
            if(entryPoint){
                entryPointId = (UUID.randomUUID()).toString();
                ((MiracleFragment) fragment).setEntryPointId(entryPointId);
            }
        }

        Fragment whoNeedHide = tab.getLastFragment();
        tab.addFragment(fragment);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        add(fragment, fragmentTransaction);
        if(whoNeedHide!=null){
            hide(whoNeedHide, fragmentTransaction);
        }
        fragmentTransaction.commit();

        return entryPointId;
    }

    public boolean selectTab(int tabId){

        Tab whoNeedShowTab = tabs.get(tabId);

        if(whoNeedShowTab == null) return false;

        Fragment whoNeedHideFragment = null;

        if(!tabsHistory.isEmpty()){
            int lastIndex = getLastInHistory();
            if(lastIndex==tabId) return false;
            Tab whoNeedHideTab = tabs.get(lastIndex);
            if(whoNeedHideTab!=null){
                whoNeedHideFragment = whoNeedHideTab.getLastFragment();
            }
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (whoNeedShowTab.getCount() == 0) {
            FragmentFabric fabric = fabrics.get(tabId);
            if(fabric==null) return false;
            Fragment fragment = fabric.create();
            whoNeedShowTab.addFragment(fragment);
            add(fragment, fragmentTransaction);
        } else {
            Fragment whoNeedShow = whoNeedShowTab.getLastFragment();
            show(whoNeedShow, fragmentTransaction);
        }

        if (whoNeedHideFragment != null) {
            hide(whoNeedHideFragment, fragmentTransaction);
        }

        removeAllFromHistory(tabId);
        tabsHistory.add(tabId);
        fragmentTransaction.commit();

        return true;
    }

    public boolean stepBack(){
        return stepBack(null);
    }

    public boolean stepBack(@Nullable String entryPointId){
        if(tabsHistory.isEmpty()) return false;

        Tab tab = tabs.get(getLastInHistory());

        if(tab==null) return false;

        if(tab.getCount()==1&&tabsHistory.size()==1) return false;

        if(entryPointId!=null){
            ArrayList<Fragment> forDeletion = new ArrayList<>();
            MiracleFragment entryPoint = null;
            for (int i=tab.fragmentsStack.size()-1;i>=0;i--){
                Fragment fragment = tab.fragmentsStack.get(i);
                if(fragment instanceof MiracleFragment){
                    MiracleFragment miracleFragment = (MiracleFragment) fragment;
                    if(entryPointId.equals(miracleFragment.getEntryPointId())){
                        entryPoint = miracleFragment;
                        break;
                    }
                }
                forDeletion.add(fragment);
            }
            if(entryPoint!=null){
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                show(entryPoint, fragmentTransaction);
                for (Fragment fragment:forDeletion) {
                    remove(fragment, fragmentTransaction);
                }
                fragmentTransaction.commit();
                return true;
            }
        } else {
            Fragment whoNeedRemove = tab.getLastFragment();
            Fragment whoNeedShow;
            tab.removeLastFragment();

            if (tab.getCount() == 0) {
                removeAllFromHistory(getLastInHistory());
                int newItemId = getLastInHistory();
                tab = tabs.get(newItemId);
                if(tab!=null&&onSelfTabChangeListener!=null){
                    onSelfTabChangeListener.onChange(newItemId);
                }
            }

            if (tab != null) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                whoNeedShow = tab.getLastFragment();
                show(whoNeedShow, fragmentTransaction);
                remove(whoNeedRemove, fragmentTransaction);
                fragmentTransaction.commit();
                return true;
            }
        }

        return false;
    }

    public void goToFirstFragment(){

        Tab tab = tabs.get(getLastInHistory());

        if(tab==null || tab.getCount()==1) return;

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        show(tab.getFirstFragment(), fragmentTransaction);

        for (int i=tab.fragmentsStack.size()-1;i>0;i--){
            Fragment fragment = tab.fragmentsStack.get(1);
            tab.removeFragmentAt(1);
            remove(fragment, fragmentTransaction);
        }

        fragmentTransaction.commit();

    }

    private void hide(Fragment fragment, FragmentTransaction fragmentTransaction){
        if(!fragmentInFM(fragment)){
            //noinspection deprecation
            if(fragment.getFragmentManager()!=null) {
                fragment.getParentFragmentManager().beginTransaction().detach(fragment).commit();
            }
        }
        if(!fragment.isAdded()){
            fragmentManager.beginTransaction().add(fragmentContainerId, fragment).commit();
        }
        fragmentTransaction.hide(fragment);
    }

    private void show(Fragment fragment, FragmentTransaction fragmentTransaction){
        if(!fragmentInFM(fragment)){
            //noinspection deprecation
            if(fragment.getFragmentManager()!=null) {
                fragment.getParentFragmentManager().beginTransaction().detach(fragment).commit();
            }
        }
        if(!fragment.isAdded()){
            fragmentManager.beginTransaction().add(fragmentContainerId, fragment).commit();
        } else {
            fragmentTransaction.show(fragment);
        }
    }

    private void add(Fragment fragment, FragmentTransaction fragmentTransaction){
        if(!fragmentInFM(fragment)){
            //noinspection deprecation
            if(fragment.getFragmentManager()!=null) {
                fragment.getParentFragmentManager().beginTransaction().detach(fragment).commit();
            }
        }
        fragmentTransaction.add(fragmentContainerId, fragment);
    }

    private void remove(Fragment fragment, FragmentTransaction fragmentTransaction){
        if(fragmentInFM(fragment)){
            fragmentTransaction.remove(fragment);
        } else {
            //noinspection deprecation
            if(fragment.getFragmentManager()!=null) {
                fragment.getParentFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }
    }

    private void clearTabs(){
        for (Map.Entry<Integer, Tab> set:tabs.entrySet()) {
            if(fabrics.get(set.getKey())==null) {
                Tab tab = set.getValue();
                if (!tab.fragmentsStack.isEmpty()) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    for (Fragment fragment : tab.fragmentsStack) {
                        if (fragmentInFM(fragment)) {
                            fragmentTransaction.remove(fragment);
                        }
                    }
                    fragmentTransaction.commit();
                }
            }
        }
    }

    private int getLastInHistory(){
        return tabsHistory.get(tabsHistory.size() - 1);
    }

    private void removeAllFromHistory(int tabIndex){
        Integer object = tabIndex;
        while (tabsHistory.contains(object)){
            tabsHistory.remove(object);
        }
    }

    private boolean fragmentInFM(Fragment fragment){
        //noinspection deprecation
        return fragment.getFragmentManager() == fragmentManager;
    }

    private static class Tab {

        final ArrayList<Fragment> fragmentsStack = new ArrayList<>();

        public void addFragment(Fragment fragment){
            fragmentsStack.add(fragment);
        }

        public void addFragment(int index, Fragment fragment){
            fragmentsStack.add(index, fragment);
        }

        public void removeFragment(Fragment fragment){
            fragmentsStack.remove(fragment);
        }

        public Fragment getFragmentAt(int index){
            return fragmentsStack.get(index);
        }

        public void removeFragmentAt(int index){
            fragmentsStack.remove(index);
        }

        public Fragment getFirstFragment(){
            return getFragmentAt(0);
        }

        public Fragment getLastFragment(){
            return getFragmentAt(getLastFragmentIndex());
        }

        public void removeLastFragment(){
            removeFragmentAt(getLastFragmentIndex());
        }

        public int getLastFragmentIndex(){
            return getCount()-1;
        }

        public int getCount(){
            return fragmentsStack.size();
        }

    }

    public static class SavedState implements Parcelable {
        private final ArrayList<String> fragmentKeys;
        private final ArrayList<Integer> tabsHistory;
        public SavedState(ArrayList<String> fragmentKeys, ArrayList<Integer> tabsHistory){
            this.fragmentKeys = fragmentKeys;
            this.tabsHistory = tabsHistory;
        }

        protected SavedState(Parcel in) {
            fragmentKeys = in.createStringArrayList();
            tabsHistory = new ArrayList<>();
            in.readList(tabsHistory, Integer.class.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeStringList(fragmentKeys);
            parcel.writeList(tabsHistory);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

    }

    public void saveState(@Nullable Bundle outState){
        if(outState!=null) {
            if (!tabs.isEmpty()) {
                ArrayList<String> fragmentKeys = new ArrayList<>();

                for (Map.Entry<Integer, Tab> set : tabs.entrySet()) {
                    Tab tab = set.getValue();
                    Integer tabKey = set.getKey();
                    int counter = 0;
                    for (Fragment fragment : tab.fragmentsStack) {
                        if (fragmentInFM(fragment)) {
                            String key = "t:" + tabKey + "_f:" + counter++;
                            fragmentManager.putFragment(outState, key, fragment);
                            fragmentKeys.add(key);
                        }
                    }
                }
                outState.putParcelable(savedStateKey, new SavedState(fragmentKeys, tabsHistory));
            }
        }
    }

    public void restoreState(@Nullable Bundle savedInstanceState) {
        if(savedInstanceState!=null) {
            SavedState savedState = savedInstanceState.getParcelable(savedStateKey);
            if (savedState != null) {
                this.tabsHistory.addAll(savedState.tabsHistory);
                for (String key : savedState.fragmentKeys) {
                    Fragment fragment = fragmentManager.getFragment(savedInstanceState, key);
                    if (fragment != null) {
                        String find = "t:";
                        int index;
                        String m = key.substring(find.length());
                        find = "_f:";
                        index = m.indexOf(find);
                        String t = m.substring(0, index);
                        Integer tabKey = Integer.valueOf(t);
                        Tab tab = tabs.get(tabKey);
                        if (tab == null) {
                            tab = new Tab();
                            tabs.put(tabKey, tab);
                        }
                        tab.addFragment(fragment);
                    }
                }
            }
            savedInstanceState.remove(savedStateKey);
        }
    }

    public interface OnSelfTabChangeListener{
        void onChange(int tabId);
    }

    public OnSelfTabChangeListener getOnSelfTabChangeListener() {
        return onSelfTabChangeListener;
    }

    public void setOnSelfTabChangeListener(OnSelfTabChangeListener onSelfTabChangeListener) {
        this.onSelfTabChangeListener = onSelfTabChangeListener;
    }
}
