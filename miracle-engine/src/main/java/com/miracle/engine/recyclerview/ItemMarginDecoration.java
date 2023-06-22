package com.miracle.engine.recyclerview;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemMarginDecoration extends RecyclerView.ItemDecoration {

    private final int itemMargin;
    private final int orientation;
    private final boolean includeFirstMargin;
    private final boolean includeLastMargin;

    public ItemMarginDecoration(int itemMargin, int orientation,
                                boolean includeFirstMargin,
                                boolean includeLastMargin) {
        this.itemMargin = itemMargin;
        this.orientation = orientation;
        this.includeFirstMargin = includeFirstMargin;
        this.includeLastMargin = includeLastMargin;
    }


    @Override
    public void getItemOffsets(@NonNull Rect outRect,
                               @NonNull View view,
                               @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view);

        if(position<0){
            return;
        }

        boolean first = position==0;
        boolean last = position==state.getItemCount()-1;

        if(orientation==RecyclerView.VERTICAL){
            if(first){
                if(includeFirstMargin){
                    outRect.top = itemMargin;
                }
            } else {
                outRect.top = itemMargin;
            }
            if(last&&includeLastMargin){
                outRect.bottom = itemMargin;
            }
        } else {
            if(first){
                if(includeFirstMargin){
                    outRect.left = itemMargin;
                }
            } else {
                outRect.left = itemMargin;
            }
            if(last&&includeLastMargin){
                outRect.right = itemMargin;
            }
        }

    }
}
