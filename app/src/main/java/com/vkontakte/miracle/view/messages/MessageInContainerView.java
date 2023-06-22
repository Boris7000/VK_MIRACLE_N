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

public class MessageInContainerView extends ViewGroup {

    private static final int DEF_STYLE_RES = R.style.MessageInContainerView;

    private final VirtualTextView date;
    private final VirtualImageView inReadIcon;
    private final VirtualImageView deletedIcon;
    private final int markersSpacing;

    public MessageInContainerView(Context context) {
        this(context, null);
    }

    public MessageInContainerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MessageInContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, DEF_STYLE_RES);
    }

    public MessageInContainerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        Resources.Theme theme = context.getTheme();

        final TypedArray a = theme.obtainStyledAttributes(attrs, R.styleable.MessageInContainerView, defStyleAttr, defStyleRes);

        markersSpacing = a.getDimensionPixelSize(R.styleable.MessageInContainerView_markersSpacing, 0);

        date = new VirtualTextView(this, a, R.styleable.MessageInContainerView_dateTextAppearance);

        inReadIcon = new VirtualImageView(this, a,
                R.styleable.MessageInContainerView_inReadIconSize,
                R.styleable.MessageInContainerView_inReadIconSize,
                R.styleable.MessageInContainerView_inReadIcon,
                R.styleable.MessageInContainerView_inReadIconTint,
                R.styleable.MessageInContainerView_inReadIconTintMode);

        deletedIcon = new VirtualImageView(this, a,
                R.styleable.MessageInContainerView_deletedIconSize,
                R.styleable.MessageInContainerView_deletedIconSize,
                R.styleable.MessageInContainerView_deletedIcon,
                R.styleable.MessageInContainerView_deletedIconTint,
                R.styleable.MessageInContainerView_deletedIconTintMode);

        a.recycle();

        setWillNotDraw(false);

        date.setVisibility(GONE);
        inReadIcon.setVisibility(GONE);
        deletedIcon.setVisibility(GONE);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        int[] drawableState = getDrawableState();

        date.drawableStateChanged(drawableState);

        inReadIcon.drawableStateChanged(drawableState);

        deletedIcon.drawableStateChanged(drawableState);

    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();

        inReadIcon.jumpDrawablesToCurrentState();

        deletedIcon.jumpDrawablesToCurrentState();

    }

    @Override
    protected void onDraw(Canvas canvas) {

        date.drawOnCanvas(canvas);

        inReadIcon.drawOnCanvas(canvas);

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
        int inReadIconWidth = inReadIcon.getContentWidth();
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

        int indicatorsEndX = startPadding;

        if(getChildCount()>0){
            View child = getChildAt(0);
            if(child.getVisibility()!=GONE){
                final int childWidth = child.getMeasuredWidth();
                final int childHeight = child.getMeasuredHeight();
                final int childR = Math.min(width-usedWidth, startPadding+childWidth);
                child.layout(startPadding, topPadding, childR, topPadding+childHeight);
                indicatorsEndX = childR;
            }
        }

        if(dateWidth>0){
            VirtualLayoutParams layoutParams = date.getLayoutParams();
            layoutParams.bottom = height-(bottomPadding-(int)date.getPaint().getFontMetrics().descent);
            layoutParams.top = layoutParams.bottom-date.getRawContentHeight();
            layoutParams.left = indicatorsEndX+ markersSpacing;
            layoutParams.right = layoutParams.left+dateWidth;
            indicatorsEndX = layoutParams.right;
        }

        if(inReadIconWidth>0){
            VirtualLayoutParams layoutParams = inReadIcon.getLayoutParams();
            layoutParams.bottom = height-bottomPadding;
            layoutParams.top = layoutParams.bottom-inReadIcon.getRawContentHeight();
            layoutParams.left = indicatorsEndX+markersSpacing;
            layoutParams.right = layoutParams.left+inReadIconWidth;
            indicatorsEndX = layoutParams.right;
        }

        if(deletedIconWidth>0){
            VirtualLayoutParams layoutParams = deletedIcon.getLayoutParams();
            layoutParams.bottom = height-bottomPadding;
            layoutParams.top = layoutParams.bottom-deletedIcon.getRawContentHeight();
            layoutParams.left = indicatorsEndX+markersSpacing;
            layoutParams.right = layoutParams.left+deletedIconWidth;
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
        int inReadIconWidth = inReadIcon.getContentWidth();
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

    public void setInRead(boolean inRead) {
        if(inRead) {
            if (inReadIcon.getVisibility() != GONE) {
                inReadIcon.setVisibility(GONE);
                //requestLayout();
            }
        } else {
            if (inReadIcon.getVisibility() != VISIBLE) {
                inReadIcon.setVisibility(VISIBLE);
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
