package com.miracle.engine.recyclerview.asymmetricgrid;

import static com.miracle.engine.recyclerview.asymmetricgrid.GridHelper.MASK_BOTTOM;
import static com.miracle.engine.recyclerview.asymmetricgrid.GridHelper.MASK_LEFT;
import static com.miracle.engine.recyclerview.asymmetricgrid.GridHelper.MASK_RIGHT;
import static com.miracle.engine.recyclerview.asymmetricgrid.GridHelper.MASK_TOP;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AsymmetricGridSpacingDecoration extends RecyclerView.ItemDecoration{

    private final int itemsSpacing;

    private int[] gridEdgesMasks;

    public AsymmetricGridSpacingDecoration(int itemsSpacing, int[] gridEdgesMasks) {
        this.itemsSpacing = itemsSpacing /2;
        setGridEdgesMasks(gridEdgesMasks);
    }

    public AsymmetricGridSpacingDecoration(int itemsSpacing) {
        this(itemsSpacing, null);
    }

    public void setGridEdgesMasks(int[] gridEdgesMasks) {
        this.gridEdgesMasks = gridEdgesMasks;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect,
                               @NonNull View view,
                               @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        if(gridEdgesMasks !=null){
            int index = parent.getChildAdapterPosition(view);
            if (index < 0 || index>= gridEdgesMasks.length) {
                return;
            }
            int mask = gridEdgesMasks[index];
            if((mask&MASK_TOP)==0){
                outRect.top = itemsSpacing;
            } else {
                outRect.top = 0;
            }
            if((mask&MASK_BOTTOM)==0){
                outRect.bottom = itemsSpacing;
            } else {
                outRect.bottom = 0;
            }
            if((mask&MASK_LEFT)==0){
                outRect.left = itemsSpacing;
            } else {
                outRect.left = 0;
            }
            if((mask&MASK_RIGHT)==0){
                outRect.right = itemsSpacing;
            } else {
                outRect.right = 0;
            }
        } else {
            outRect.top = 0;
            outRect.bottom = 0;
            outRect.left = 0;
            outRect.right = 0;
        }
    }
}
