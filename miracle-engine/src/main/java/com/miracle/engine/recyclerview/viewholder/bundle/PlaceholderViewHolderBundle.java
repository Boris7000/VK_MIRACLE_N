package com.miracle.engine.recyclerview.viewholder.bundle;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.recyclerview.viewholder.PlaceholderViewHolder;


public abstract class PlaceholderViewHolderBundle extends ViewHolderBundle<RecyclerView.ViewHolder,Object> {

    @Override
    public RecyclerView.ViewHolder create(View view) {
        return new PlaceholderViewHolder(view);
    }

    @Override
    public void bind(RecyclerView.ViewHolder viewHolder, Object data) {}
}
