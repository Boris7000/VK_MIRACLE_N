package com.vkontakte.miracle.view.messages;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.view.virtual.VirtualImageView;
import com.vkontakte.miracle.view.virtual.VirtualLayoutParams;
import com.vkontakte.miracle.view.virtual.VirtualTextView;

public class MessageOutContainerView extends ViewGroup {

    private static final int DEF_STYLE_RES = R.style.MessageOutContainerView;

    private final VirtualTextView date;
    private final VirtualImageView outReadIcon;
    private final VirtualImageView deletedIcon;
    private final int markersSpacing;

    public MessageOutContainerView(Context context) {
        this(context, null);
    }

    public MessageOutContainerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MessageOutContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, DEF_STYLE_RES);
    }

    public MessageOutContainerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        Resources.Theme theme = context.getTheme();

        final TypedArray a = theme.obtainStyledAttributes(attrs, R.styleable.MessageOutContainerView, defStyleAttr, defStyleRes);

        markersSpacing = a.getDimensionPixelSize(R.styleable.MessageOutContainerView_markersSpacing, 0);

        date = new VirtualTextView(this, a, R.styleable.MessageOutContainerView_dateTextAppearance);

        outReadIcon = new VirtualImageView(this, a,
                R.styleable.MessageOutContainerView_outReadIconSize,
                R.styleable.MessageOutContainerView_outReadIconSize,
                R.styleable.MessageOutContainerView_outReadIcon,
                R.styleable.MessageOutContainerView_outReadIconTint,
                R.styleable.MessageOutContainerView_outReadIconTintMode);

        deletedIcon = new VirtualImageView(this, a,
                R.styleable.MessageOutContainerView_deletedIconSize,
                R.styleable.MessageOutContainerView_deletedIconSize,
                R.styleable.MessageOutContainerView_deletedIcon,
                R.styleable.MessageOutContainerView_deletedIconTint,
                R.styleable.MessageOutContainerView_deletedIconTintMode);

        a.recycle();

        setWillNotDraw(false);

        date.setVisibility(GONE);
        outReadIcon.setVisibility(GONE);
        deletedIcon.setVisibility(GONE);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        int[] drawableState = getDrawableState();

        date.drawableStateChanged(drawableState);

        outReadIcon.drawableStateChanged(drawableState);

        deletedIcon.drawableStateChanged(drawableState);

    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();

        outReadIcon.jumpDrawablesToCurrentState();

        deletedIcon.jumpDrawablesToCurrentState();

    }

    @Override
    protected void onDraw(Canvas canvas) {

        date.drawOnCanvas(canvas);

        outReadIcon.drawOnCanvas(canvas);

        deletedIcon.drawOnCanvas(canvas);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int width = getWidth();
        int height = getHeight();

        int topPadding = getPaddingTop();
        int bottomPadding = getPaddingBottom();

        int startPadding = getPaddingStart();
        int endPadding = getPaddingEnd();

        int usedWidth = endPadding;

        int dateWidth = date.getContentWidth();
        int inReadIconWidth = outReadIcon.getContentWidth();
        int deletedIconWidth = deletedIcon.getContentWidth();
        int visibleCount = 0;

        if(deletedIconWidth>0){
            usedWidth+=deletedIconWidth;
            visibleCount++;
        }

        if(inReadIconWidth>0){
            usedWidth+=inReadIconWidth;
            visibleCount++;
        }

        if(dateWidth>0){
            usedWidth+=dateWidth;
            visibleCount++;
        }

        if(visibleCount>0){
            usedWidth+=markersSpacing*visibleCount;
        }

        int indicatorsEndX = width-endPadding;

        if(getChildCount()>0){
            View child = getChildAt(0);
            if(child.getVisibility()!=GONE){
                final int childWidth = child.getMeasuredWidth();
                final int childHeight = child.getMeasuredHeight();
                final int childL = Math.max(usedWidth, indicatorsEndX-childWidth);
                child.layout(childL, topPadding, indicatorsEndX, topPadding+childHeight);
                indicatorsEndX = childL;
            }
        }

        if(dateWidth>0){
            VirtualLayoutParams layoutParams = date.getLayoutParams();
            layoutParams.bottom = height-(bottomPadding-(int)date.getPaint().getFontMetrics().descent);
            layoutParams.top = layoutParams.bottom-date.getRawContentHeight();
            layoutParams.right = indicatorsEndX-markersSpacing;
            layoutParams.left = layoutParams.right-dateWidth;
            indicatorsEndX = layoutParams.left;
        }

        if(inReadIconWidth>0){
            VirtualLayoutParams layoutParams = outReadIcon.getLayoutParams();
            layoutParams.bottom = height-bottomPadding;
            layoutParams.top = layoutParams.bottom-outReadIcon.getRawContentHeight();
            layoutParams.right = indicatorsEndX-markersSpacing;
            layoutParams.left = layoutParams.right-inReadIconWidth;
            indicatorsEndX = layoutParams.left;
        }

        if(deletedIconWidth>0){
            VirtualLayoutParams layoutParams = deletedIcon.getLayoutParams();
            layoutParams.bottom = height-bottomPadding;
            layoutParams.top = layoutParams.bottom-deletedIcon.getRawContentHeight();
            layoutParams.right = indicatorsEndX-markersSpacing;
            layoutParams.left = layoutParams.right-deletedIconWidth;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);

        int topPadding = getPaddingTop();
        int bottomPadding = getPaddingBottom();

        int startPadding = getPaddingStart();
        int endPadding = getPaddingEnd();

        int usedWidth = 0;
        int usedHeight = 0;

        usedWidth+=startPadding+endPadding;
        usedHeight+=topPadding+bottomPadding;

        int dateWidth = date.getContentWidth();
        int outReadIconWidth = outReadIcon.getContentWidth();
        int deletedIconWidth = deletedIcon.getContentWidth();
        int visibleCount = 0;

        if(deletedIconWidth>0){
            usedWidth+=deletedIconWidth;
            visibleCount++;
        }

        if(outReadIconWidth>0){
            usedWidth+=outReadIconWidth;
            visibleCount++;
        }

        if(dateWidth>0){
            usedWidth+=dateWidth;
            visibleCount++;
        }

        if(visibleCount>0){
            usedWidth+= markersSpacing *(visibleCount+1);
        }

        int maxHeight = 0;
        int childState = 0;

        if(getChildCount()>0){

            View child = getChildAt(0);
            if(child.getVisibility()!=GONE){

                final LayoutParams lp = child.getLayoutParams();

                int wMeasureSpec;
                if (lp.width == FrameLayout.LayoutParams.MATCH_PARENT) {
                    final int width = Math.max(0, widthSize-usedWidth);
                    wMeasureSpec = MeasureSpec.makeMeasureSpec(
                            width, MeasureSpec.EXACTLY);
                } else {
                    wMeasureSpec = getChildMeasureSpec(widthMeasureSpec,usedWidth,lp.width);
                }

                final int hMeasureSpec;
                if (lp.height == FrameLayout.LayoutParams.MATCH_PARENT) {
                    final int height = Math.max(0, heightSize-usedHeight);
                    hMeasureSpec = MeasureSpec.makeMeasureSpec(
                            height, MeasureSpec.EXACTLY);
                } else {
                    hMeasureSpec = getChildMeasureSpec(heightMeasureSpec, usedHeight, lp.height);
                }

                child.measure(wMeasureSpec, hMeasureSpec);

                maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
                childState = combineMeasuredStates(childState, child.getMeasuredState());
            }
        }

        maxHeight+=usedHeight;

        setMeasuredDimension(widthSize, resolveSizeAndState(maxHeight, heightMeasureSpec,
                childState << MEASURED_HEIGHT_STATE_SHIFT));
    }

    public void setDateText(String dateText) {
        if(dateText!=null&&!dateText.isEmpty()) {
            date.setText(dateText);
            date.setVisibility(VISIBLE);
            //requestLayout();
        } else {
            if (date.getVisibility()!=GONE) {
                date.setVisibility(GONE);
                //requestLayout();
            }
        }
    }

    public void setOutRead(boolean outRead) {
        if(outRead) {
            if (outReadIcon.getVisibility() != GONE) {
                outReadIcon.setVisibility(GONE);
                //requestLayout();
            }
        } else {
            if (outReadIcon.getVisibility() != VISIBLE) {
                outReadIcon.setVisibility(VISIBLE);
                //requestLayout();
            }
        }
    }

    public void setDeleted(boolean deleted) {
        if(deleted) {
            if (deletedIcon.getVisibility() != VISIBLE) {
                deletedIcon.setVisibility(VISIBLE);
                //requestLayout();
            }
        } else {
            if (deletedIcon.getVisibility() != GONE) {
                deletedIcon.setVisibility(GONE);
                //requestLayout();
            }
        }
    }

}
