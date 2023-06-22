package com.miracle.engine.recyclerview;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.AdapterListUpdateCallback;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.R;
import com.miracle.engine.recyclerview.viewholder.PlaceholderViewHolder;
import com.miracle.engine.recyclerview.viewholder.bundle.ViewHolderBundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DynamicBindingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ListUpdateCallback listUpdateCallback = new AdapterListUpdateCallback(this);

    private final Map<Integer, ViewHolderBundle<RecyclerView.ViewHolder, Object>> bundles;

    private final List<Integer> contentHashCodes = new ArrayList<>();
    private final List<TypedData> items = new ArrayList<>();

    public DynamicBindingAdapter(Map<Integer, ViewHolderBundle<RecyclerView.ViewHolder, Object>> bundles) {
        this.bundles = bundles;
    }

    public void setListUpdateCallback(ListUpdateCallback listUpdateCallback) {
        this.listUpdateCallback = listUpdateCallback;
    }

    public void setItems(@Nullable List<TypedData> items) {
        if(items!=null) {
            List<Integer> newContentHashCodes = new ArrayList<>();
            for (TypedData typedData:items) {
                newContentHashCodes.add(typedData.contentHashCode());
            }
            DiffUtil.DiffResult diffResult = calculateDifference(this.items, this.contentHashCodes,
                    items, newContentHashCodes);

            this.items.clear();
            this.items.addAll(items);
            this.contentHashCodes.clear();
            this.contentHashCodes.addAll(newContentHashCodes);

            diffResult.dispatchUpdatesTo(listUpdateCallback);
        } else {
            int oldCount = this.items.size();
            this.items.clear();
            this.contentHashCodes.clear();
            notifyItemRangeRemoved(0, oldCount);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewHolderBundle<RecyclerView.ViewHolder, Object> bundle = bundles.get(viewType);
        if(bundle!=null){
            return bundle.create(inflater.inflate(bundle.getLayoutResourceId(), parent, false));
        } else {
            return new PlaceholderViewHolder(inflater.inflate(R.layout.ai_empty, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolderBundle<RecyclerView.ViewHolder, Object> bundle = bundles.get(getItemViewType(position));
        if(bundle!=null){
            bundle.bind(holder, items.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getDataType();
    }

    @Override
    public int getItemCount() {
        return getItemsSize();
    }

    List<TypedData> getItems(){
        return items;
    }

    public int getItemsSize(){
        return items.size();
    }

    public static DiffUtil.DiffResult calculateDifference(List<TypedData> data, List<Integer> contentHashCodes,
                                                          List<TypedData> newData, List<Integer> newContentHashCodes){
        final int oldSize = data.size();
        final int newSize = newData.size();
        return DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldSize;
            }

            @Override
            public int getNewListSize() {
                return newSize;
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                TypedData oldItem = data.get(oldItemPosition);
                TypedData newItem = newData.get(newItemPosition);
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return contentHashCodes.get(oldItemPosition).equals(newContentHashCodes.get(newItemPosition));
            }
        });
    }
}
