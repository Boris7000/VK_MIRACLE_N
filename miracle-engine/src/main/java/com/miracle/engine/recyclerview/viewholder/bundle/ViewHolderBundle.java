package com.miracle.engine.recyclerview.viewholder.bundle;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public abstract class ViewHolderBundle<V extends RecyclerView.ViewHolder, D> {
    public abstract int getLayoutResourceId();
    public abstract V create(View view);
    public abstract void bind(V viewHolder, D data);
}
