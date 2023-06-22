package com.miracle.engine.recyclerview;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.recyclerview.viewholder.bundle.ViewHolderBundle;

import java.util.Map;

public class LoadableAdapter extends DynamicBindingAdapter {

    private boolean loading = false;

    private int lastItemCountFromEnd = 0;
    private OnLastItemBindListener onLastItemBindListener;

    public LoadableAdapter(Map<Integer, ViewHolderBundle<RecyclerView.ViewHolder, Object>> bundles) {
        super(bundles);
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        if(this.loading!=loading){
            int oldSize = getItemCount();
            this.loading = loading;
            if(loading){
                notifyItemInserted(oldSize+1);
            } else {
                notifyItemRemoved(oldSize+1);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
       super.onBindViewHolder(holder, position);
       if(onLastItemBindListener!=null) {
           if (position >= (getItemCount() - 1) - lastItemCountFromEnd) {
               onLastItemBindListener.onBind();
           }
       }
    }

    @Override
    public int getItemViewType(int position) {
        if(position<getItemsSize()){
            return super.getItemViewType(position);
        } else {
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        if(loading){
            return getItemsSize()+1;
        } else {
            return super.getItemCount();
        }
    }

    public void setOnLastItemBindListener(OnLastItemBindListener onLastItemBindListener) {
        this.onLastItemBindListener = onLastItemBindListener;
    }

    public void setLastItemCountFromEnd(@IntRange(from = 0) int lastItemCountFromEnd) {
        this.lastItemCountFromEnd = lastItemCountFromEnd;
    }
}
