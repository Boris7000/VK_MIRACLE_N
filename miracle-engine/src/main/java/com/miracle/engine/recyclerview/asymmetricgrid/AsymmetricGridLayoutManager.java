package com.miracle.engine.recyclerview.asymmetricgrid;

import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.recyclerview.asymmetricgrid.GridHelper.SizeItem;

public class AsymmetricGridLayoutManager extends RecyclerView.LayoutManager{

    private SizeItem[][] positions;

    private boolean mRecycleChildrenOnDetach = false;

    public AsymmetricGridLayoutManager() {
        this(null);
    }

    public AsymmetricGridLayoutManager(SizeItem[][] positions) {
        setPositions(positions);
    }

    public void setPositions(SizeItem[][] positions) {
        this.positions = positions;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * Returns whether LayoutManager will recycle its children when it is detached from
     * RecyclerView.
     *
     * @return true if LayoutManager will recycle its children when it is detached from
     * RecyclerView.
     */
    public boolean getRecycleChildrenOnDetach() {
        return mRecycleChildrenOnDetach;
    }

    /**
     * Set whether LayoutManager will recycle its children when it is detached from
     * RecyclerView.
     * <p>
     * If you are using a {@link RecyclerView.RecycledViewPool}, it might be a good idea to set
     * this flag to <code>true</code> so that views will be available to other RecyclerViews
     * immediately.
     * <p>
     * Note that, setting this flag will result in a performance drop if RecyclerView
     * is restored.
     *
     * @param recycleChildrenOnDetach Whether children should be recycled in detach or not.
     */
    public void setRecycleChildrenOnDetach(boolean recycleChildrenOnDetach) {
        mRecycleChildrenOnDetach = recycleChildrenOnDetach;
    }

    @Override
    public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(view, recycler);
        if (mRecycleChildrenOnDetach) {
            removeAndRecycleAllViews(recycler);
            recycler.clear();
        }
    }

    @Override
    public void onInitializeAccessibilityEvent(@NonNull AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        if (getChildCount() > 0) {
            event.setFromIndex(0);
            event.setToIndex(getChildCount()-1);
        }
    }

    @Override
    public boolean canScrollHorizontally() {
        return false;
    }

    @Override
    public boolean canScrollVertically() {
        return false;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {

        detachAndScrapAttachedViews(recycler);

        if(positions!=null) {
            int itemCount = getItemCount();
            if (itemCount == 0) return;
            int width = getWidth();

            int layoutCounter = 0;
            int marginTop = 0;
            layout:
            for (SizeItem[] row : positions) {
                int equalHeight = 1000;
                int rowWidthSum = 0;
                for (SizeItem position : row) {
                    float multiplier = (float) equalHeight / position.getHeight();
                    rowWidthSum += multiplier * position.getWidth();
                }
                float multiplier = (float) width / rowWidthSum;
                equalHeight *= multiplier;
                int marginLeft = 0;
                for (SizeItem position : row) {
                    View view = recycler.getViewForPosition(layoutCounter++);
                    addView(view);
                    measureChildWithMargins(view, 0, 0);
                    multiplier = (float) equalHeight / position.getHeight();
                    int multipliedWidth = (int) (position.getWidth() * multiplier);
                    layoutDecoratedWithMargins(view, marginLeft, marginTop,
                            marginLeft + multipliedWidth, marginTop + equalHeight);
                    if (layoutCounter >= itemCount) {
                        break layout;
                    }
                    marginLeft += multipliedWidth;
                }
                marginTop += equalHeight;
            }
        }
    }

    @Override
    public void onMeasure(@NonNull RecyclerView.Recycler recycler,
                          @NonNull RecyclerView.State state, int widthSpec, int heightSpec) {

        int widthSize = View.MeasureSpec.getSize(widthSpec);
        int heightSize = View.MeasureSpec.getSize(heightSpec);

        if(positions!=null) {
            int itemCount = getItemCount();
            if (itemCount == 0) return;
            int marginTop = 0;
            for (SizeItem[] row : positions) {
                int equalHeight = 1000;
                int rowWidthSum = 0;
                for (SizeItem position : row) {
                    float multiplier = (float) equalHeight / position.getHeight();
                    rowWidthSum += multiplier * position.getWidth();
                }
                float multiplier = (float) widthSize / rowWidthSum;
                equalHeight *= multiplier;
                marginTop += equalHeight;
            }
            heightSize = marginTop;
        }

        if(heightSize>widthSize){
            float multiplier = (float) widthSize / heightSize;
            widthSize*=multiplier;
            heightSize*=multiplier;
        }

        setMeasuredDimension(widthSize, heightSize);
    }

}
