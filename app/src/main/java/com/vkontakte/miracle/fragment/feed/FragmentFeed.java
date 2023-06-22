package com.vkontakte.miracle.fragment.feed;

import android.os.Bundle;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.miracle.engine.fragment.FragmentFabric;
import com.miracle.engine.fragment.MiracleFragment;
import com.miracle.engine.recyclerview.LoadableAdapter;
import com.miracle.engine.recyclerview.TypedData;
import com.miracle.engine.recyclerview.viewholder.bundle.ViewHolderBundle;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.activity.main.MainActivity;
import com.vkontakte.miracle.model.general.Error;
import com.vkontakte.miracle.model.general.ExtendedArrays;
import com.vkontakte.miracle.model.photos.Photo;
import com.vkontakte.miracle.util.SwipeRefreshUtil;
import com.vkontakte.miracle.util.constants.TypedDataConstants;
import com.miracle.engine.recyclerview.viewholder.bundle.PlaceholderViewHolderBundle;
import com.vkontakte.miracle.viewholder.general.ErrorViewHolderBundle;
import com.vkontakte.miracle.viewholder.wall.PostViewHolderBundle;

import java.util.ArrayList;
import java.util.List;

public class FragmentFeed extends MiracleFragment {

    private FeedViewModel feedViewModel;
    private ExtendedArrays extendedArrays;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        feedViewModel = new ViewModelProvider(this).get(FeedViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.feed_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.refreshLayout);
        SwipeRefreshUtil.applyDefaultStyle(swipeRefreshLayout,requireContext());
        ProgressBar progressBar = view.findViewById(R.id.circleProgressBar);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        ArrayMap<Integer, ViewHolderBundle<RecyclerView.ViewHolder, Object>> bundles = new ArrayMap<>();

        ////////////////////////////////////////////////////////////////////////////////////////////
        PostViewHolderBundle postViewHolderBundle = new PostViewHolderBundle() {
            @Override
            public void onPostPhotoClick(Photo photo, List<Photo> photos, View view) {
                ((MainActivity)requireActivity()).openPhotoViewerFragment(view);
            }

            @Override
            public void onOwnerLinkClick(String ownerId) {

            }

            @Override
            public void onUrlLinkClick(String url) {

            }

            @Override
            public void onHashTagLinkClick(String hashTag) {

            }
            @Override
            public ExtendedArrays requestExtendedArrays() {
                return extendedArrays;
            }
        };
        bundles.put(TypedDataConstants.TYPE_POST, postViewHolderBundle);
        PlaceholderViewHolderBundle placeholderViewHolderBundle = new PlaceholderViewHolderBundle() {
            @Override
            public int getLayoutResourceId() {
                return R.layout.feed_ai_placeholder;
            }
        };
        bundles.put(TypedDataConstants.TYPE_PLACEHOLDER,placeholderViewHolderBundle);
        ErrorViewHolderBundle errorViewHolderBundle = new ErrorViewHolderBundle() {};
        bundles.put(TypedDataConstants.TYPE_ERROR,errorViewHolderBundle);
        ////////////////////////////////////////////////////////////////////////////////////////////

        LoadableAdapter adapter = new LoadableAdapter(bundles);
        adapter.setLastItemCountFromEnd(4);
        adapter.setOnLastItemBindListener(() -> feedViewModel.tryLoadMore());
        recyclerView.setAdapter(adapter);

        feedViewModel.loadingType.observe(requireActivity(), loadingType -> {
            switch (loadingType){
                case NO_LOADING:{
                    swipeRefreshLayout.setEnabled(true);
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                    break;
                }
                case REFRESHING:{
                    swipeRefreshLayout.setEnabled(true);
                    swipeRefreshLayout.setRefreshing(true);
                    progressBar.setVisibility(View.GONE);
                    break;
                }
                case LOADING_NEXT:{
                    swipeRefreshLayout.setEnabled(false);
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                    break;
                }
                case FIRST_TIME:{
                    swipeRefreshLayout.setEnabled(false);
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (!feedViewModel.tryRefresh()) swipeRefreshLayout.setRefreshing(false);});

        feedViewModel.viewState.observe(requireActivity(), viewState -> {
            if(viewState instanceof FeedViewModel.ViewState.Loading){
                adapter.setItems(null);
                adapter.setLoading(false);
            } else if(viewState instanceof FeedViewModel.ViewState.Success){
                FeedViewModel.ViewState.Success success = (FeedViewModel.ViewState.Success)viewState;
                extendedArrays = success.getExtendedArrays();
                ArrayList<TypedData> items = new ArrayList<>();
                //TODO добавить список историй
                items.addAll(success.getPosts());
                adapter.setItems(items);
                adapter.setLoading(success.hasNext());
            } else if(viewState instanceof FeedViewModel.ViewState.Empty){
                ArrayList<TypedData> items = new ArrayList<>();
                items.add(() -> TypedDataConstants.TYPE_PLACEHOLDER);
                adapter.setItems(items);
                adapter.setLoading(false);
            } else if(viewState instanceof FeedViewModel.ViewState.Error){
                FeedViewModel.ViewState.Error error = (FeedViewModel.ViewState.Error)viewState;
                ArrayList<TypedData> items = new ArrayList<>();
                items.add(new Error(error.getErrorMessage()));
                adapter.setItems(items);
                adapter.setLoading(false);
            }
        });
    }


    public static class Fabric implements FragmentFabric {
        @Override
        public Fragment create() {
            return new FragmentFeed();
        }
    }

}
