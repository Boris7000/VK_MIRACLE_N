package com.vkontakte.miracle.fragment.messages;

import android.os.Bundle;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.miracle.engine.activity.tabs.TabsActivityController;
import com.miracle.engine.fragment.FragmentFabric;
import com.miracle.engine.fragment.MiracleFragment;
import com.miracle.engine.recyclerview.CustomAdapterListUpdateCallback;
import com.miracle.engine.recyclerview.LoadableAdapter;
import com.miracle.engine.recyclerview.TypedData;
import com.miracle.engine.recyclerview.viewholder.bundle.ViewHolderBundle;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.model.general.Error;
import com.vkontakte.miracle.model.general.ExtendedArrays;
import com.vkontakte.miracle.model.messages.ListConversationBundle;
import com.vkontakte.miracle.util.constants.TypedDataConstants;
import com.vkontakte.miracle.viewholder.general.ErrorViewHolderBundle;
import com.vkontakte.miracle.viewholder.messages.ConversationViewHolderBundle;

import java.util.ArrayList;
import java.util.Map;

public class FragmentConversations extends MiracleFragment {

    private ConversationsViewModel conversationsViewModel;
    private ExtendedArrays extendedArrays;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        conversationsViewModel = new ViewModelProvider(this).get(ConversationsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.conversations_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindAppbar(view);
        bindContent(view);
    }

    private void bindAppbar(View view){
        AppBarLayout appBarLayout = view.findViewById(R.id.appbarLayout);
        Toolbar toolbar = appBarLayout.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.dialogs);
    }

    private void bindContent(View view){
        ProgressBar progressBar = view.findViewById(R.id.circleProgressBar);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        Map<Integer, ViewHolderBundle<RecyclerView.ViewHolder, Object>> bundles = getAdapterBundles();

        LoadableAdapter adapter = new LoadableAdapter(bundles);
        adapter.setListUpdateCallback(new CustomAdapterListUpdateCallback(adapter));
        adapter.setLastItemCountFromEnd(4);
        adapter.setOnLastItemBindListener(() -> conversationsViewModel.tryLoadMore());
        recyclerView.setAdapter(adapter);

        conversationsViewModel.loadingType.observe(requireActivity(), loadingType -> {
            switch (loadingType){
                case NO_LOADING:
                case LOADING_NEXT: {
                    progressBar.setVisibility(View.GONE);
                    break;
                }
                case FIRST_TIME:{
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                }
            }
        });

        conversationsViewModel.viewState.observe(requireActivity(), viewState -> {
            if(viewState instanceof ConversationsViewModel.ViewState.Loading){
                clearAdapter(adapter);
            } else if(viewState instanceof ConversationsViewModel.ViewState.Success){
                ConversationsViewModel.ViewState.Success success = (ConversationsViewModel.ViewState.Success)viewState;
                extendedArrays = success.getExtendedArrays();
                bindAdapter(adapter, success);
            } else if(viewState instanceof ConversationsViewModel.ViewState.Empty){
                bindAdapterWidthPlaceHolder(adapter);
            } else if(viewState instanceof ConversationsViewModel.ViewState.Error){
                ConversationsViewModel.ViewState.Error error = (ConversationsViewModel.ViewState.Error)viewState;
                bindAdapter(adapter, error);
            }
        });
    }

    private void bindAdapter(LoadableAdapter adapter, ConversationsViewModel.ViewState.Success success){
        ArrayList<TypedData> items = new ArrayList<>();
        //TODO добавить список историй
        items.addAll(success.getConversations());
        //////////////////////////////////////////////
        adapter.setItems(items);
        adapter.setLoading(success.hasNext());
    }

    private void bindAdapter(LoadableAdapter adapter, ConversationsViewModel.ViewState.Error error){
        ArrayList<TypedData> items = new ArrayList<>();
        items.add(new Error(error.getErrorMessage()));
        adapter.setItems(items);
        adapter.setLoading(false);
    }

    private void bindAdapterWidthPlaceHolder(LoadableAdapter adapter){
        ArrayList<TypedData> items = new ArrayList<>();
        items.add(() -> TypedDataConstants.TYPE_PLACEHOLDER);
        adapter.setItems(items);
        adapter.setLoading(false);
    }

    private void clearAdapter(LoadableAdapter adapter){
        adapter.setItems(null);
        adapter.setLoading(false);
    }

    private Map<Integer, ViewHolderBundle<RecyclerView.ViewHolder, Object>> getAdapterBundles(){
        Map<Integer, ViewHolderBundle<RecyclerView.ViewHolder, Object>> bundles = new ArrayMap<>();

        ConversationViewHolderBundle conversationViewHolderBundle = new ConversationViewHolderBundle() {

            @Override
            public void onClick(ListConversationBundle listConversationBundle) {
                FragmentChat fragmentChat = new FragmentChat();
                Bundle args = new Bundle();
                args.putString("peerId", listConversationBundle.getPeerId());
                fragmentChat.setArguments(args);
                TabsActivityController tabsActivityController =
                        findTabsNavigation(FragmentConversations.this);
                if(tabsActivityController!=null){
                    tabsActivityController.addFragment(fragmentChat);
                }
            }

            @Override
            public ExtendedArrays requestExtendedArrays() {
                return extendedArrays;
            }
        };
        bundles.put(TypedDataConstants.TYPE_CONVERSATION,conversationViewHolderBundle);

        ErrorViewHolderBundle errorViewHolderBundle = new ErrorViewHolderBundle() {};
        bundles.put(TypedDataConstants.TYPE_ERROR,errorViewHolderBundle);

        return bundles;
    }

    public static class Fabric implements FragmentFabric {
        @Override
        public Fragment create() {
            return new FragmentConversations();
        }
    }

}
