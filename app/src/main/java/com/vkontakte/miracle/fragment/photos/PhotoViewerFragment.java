package com.vkontakte.miracle.fragment.photos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.fragment.menu.MenuViewModel;

public class PhotoViewerFragment extends Fragment {

    private MenuViewModel menuViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menuViewModel = new ViewModelProvider(this).get(MenuViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.photo_viewer_fragment, container, false);

        return rootView;
    }


}
