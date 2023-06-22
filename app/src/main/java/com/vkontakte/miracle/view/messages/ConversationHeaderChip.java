package com.vkontakte.miracle.view.messages;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.view.ContextThemeWrapper;

import com.google.android.material.imageview.ShapeableImageView;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.view.virtual.VirtualImageView;
import com.vkontakte.miracle.view.virtual.VirtualLayoutParams;
import com.vkontakte.miracle.view.virtual.VirtualOnlineIcon;
import com.vkontakte.miracle.view.virtual.VirtualTextView;

public class ConversationHeaderChip extends ViewGroup {

    private static final int DEF_STYLE_RES = R.style.ConversationHeaderChip;

    private final ShapeableImageView avatarImage;
    private int avatarSize;

    private final VirtualTextView title;
    private final VirtualTextView subtitle;

    private final VirtualOnlineIcon onlineIcon;

    private final VirtualImageView mutedIcon;
    private final VirtualImageView verifiedIcon;

    private final VirtualImageView emojiIcon;

    private int spacingBetweenAvatarAndText;
    private int spacingBetweenText;

    public ConversationHeaderChip(Context context) {
        this(context, null);
    }

    public ConversationHeaderChip(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ConversationHeaderChip(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, DEF_STYLE_RES);
    }

    public ConversationHeaderChip(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        Resources.Theme theme = context.getTheme();

        final TypedArray a = theme.obtainStyledAttributes(attrs, R.styleable.ConversationHeaderChip, defStyleAttr, defStyleRes);

        spacingBetweenAvatarAndText = a.getDimensionPixelSize(R.styleable.ConversationHeaderChip_spacingBetweenAvatarAndText, 0);
        spacingBetweenText = a.getDimensionPixelSize(R.styleable.ConversationHeaderChip_spacingBetweenText, 0);

        title = new VirtualTextView(this, a, R.styleable.ConversationHeaderChip_titleTextAppearance);
        subtitle = new VirtualTextView(this, a, R.styleable.ConversationHeaderChip_subtitleTextAppearance);

        mutedIcon = new VirtualImageView(this, a,
                R.styleable.ConversationHeaderChip_mutedIconSize,
                R.styleable.ConversationHeaderChip_mutedIconSize,
                R.styleable.ConversationHeaderChip_mutedIcon,
                R.styleable.ConversationHeaderChip_mutedIconTint,
                R.styleable.ConversationHeaderChip_mutedIconTintMode);

        verifiedIcon = new VirtualImageView(this, a,
                R.styleable.ConversationHeaderChip_verifiedIconSize,
                R.styleable.ConversationHeaderChip_verifiedIconSize,
                R.styleable.ConversationHeaderChip_verifiedIcon,
                R.styleable.ConversationHeaderChip_verifiedIconTint,
                R.styleable.ConversationHeaderChip_verifiedIconTintMode);

        emojiIcon = new VirtualImageView(this, a,
                R.styleable.ConversationHeaderChip_emojiIconSize,
                R.styleable.ConversationHeaderChip_emojiIconSize,
                0, 0, 0);

        avatarSize = a.getDimensionPixelSize(R.styleable.ConversationHeaderChip_avatarImageSize, 0);

        avatarImage = new ShapeableImageView( new ContextThemeWrapper(context, a.getResourceId(R.styleable.ConversationHeaderChip_avatarImageStyle,
                R.style.ConversationHeaderChip_Avatar)));

        onlineIcon = new VirtualOnlineIcon(this, a,
                R.styleable.ConversationHeaderChip_onlineIconSize,
                R.styleable.ConversationHeaderChip_onlineIconTint,
                R.styleable.ConversationHeaderChip_onlineIconSubtractTint,
                R.styleable.ConversationHeaderChip_onlineIconTintMode,
                R.styleable.ConversationHeaderChip_onlineMobileIcon,
                R.styleable.ConversationHeaderChip_onlineMobileIconSubtract,
                R.styleable.ConversationHeaderChip_onlineIcon,
                R.styleable.ConversationHeaderChip_onlineIconSubtract);

        a.recycle();

        setWillNotDraw(false);

        addView(avatarImage);

        title.setVisibility(GONE);
        verifiedIcon.setVisibility(GONE);
        mutedIcon.setVisibility(GONE);
        emojiIcon.setVisibility(GONE);
        subtitle.setVisibility(GONE);

    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        int[] drawableState = getDrawableState();

        title.drawableStateChanged(drawableState);

        subtitle.drawableStateChanged(drawableState);

        verifiedIcon.drawableStateChanged(drawableState);

        mutedIcon.drawableStateChanged(drawableState);

        onlineIcon.drawableStateChanged(drawableState);

    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();

        verifiedIcon.jumpDrawablesToCurrentState();

        mutedIcon.jumpDrawablesToCurrentState();

        onlineIcon.jumpDrawablesToCurrentState();

    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);

        onlineIcon.drawOnCanvas(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        title.drawOnCanvas(canvas);

        subtitle.drawOnCanvas(canvas);

        verifiedIcon.drawOnCanvas(canvas);

        mutedIcon.drawOnCanvas(canvas);

        emojiIcon.drawOnCanvas(canvas);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int widthSize = getMeasuredWidth();
        int heightSize = getMeasuredHeight();

        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingStart = getPaddingStart();
        int paddingEnd = getPaddingEnd();

        int availableHeight = heightSize-(paddingTop+paddingBottom);
        int verticalCenterY = (availableHeight/2)+paddingTop;


        int avatarHalfSize = avatarSize/2;
        int avatarTop = verticalCenterY-avatarHalfSize;
        int avatarBottom = avatarTop+avatarSize;
        int avatarRight = avatarSize+paddingStart;

        LayoutParams avatarImageLayoutParams = avatarImage.getLayoutParams();
        avatarImageLayoutParams.height = avatarSize;
        avatarImageLayoutParams.width = avatarSize;
        avatarImage.layout(paddingStart,avatarTop,avatarRight,avatarBottom);

        if(onlineIcon.getVisibility()!=GONE){
            VirtualLayoutParams onlineLayoutParams = onlineIcon.getLayoutParams();
            onlineLayoutParams.bottom = avatarBottom;
            onlineLayoutParams.top = onlineLayoutParams.bottom-onlineIcon.getRawContentHeight();
            onlineLayoutParams.right = avatarRight;
            onlineLayoutParams.left = onlineLayoutParams.right-onlineIcon.getRawContentWidth();
        }

        int textStartX = avatarRight+spacingBetweenAvatarAndText;

        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//

        if(title.getVisibility()!=GONE){
            Paint.FontMetrics titleFontMetrics = title.getPaint().getFontMetrics();
            int titleBaselineY = verticalCenterY-(int)titleFontMetrics.bottom;
            int titleAscentY = titleBaselineY+(int)titleFontMetrics.ascent;
            int titleTextCenterY = (titleBaselineY+titleAscentY)/2;

            int topRowCurrentX = textStartX;

            VirtualLayoutParams titleLayoutParams = title.getLayoutParams();
            titleLayoutParams.bottom = verticalCenterY;
            titleLayoutParams.top = titleLayoutParams.bottom-title.getRawContentHeight();
            titleLayoutParams.left = topRowCurrentX;
            titleLayoutParams.right = titleLayoutParams.left+title.getRawContentWidth();
            topRowCurrentX = titleLayoutParams.right;

            if(verifiedIcon.getVisibility()!=GONE){
                VirtualLayoutParams layoutParams = verifiedIcon.getLayoutParams();
                int iconHeight = verifiedIcon.getRawContentHeight();
                int iconHalfHeight = iconHeight/2;
                int iconWidth = verifiedIcon.getRawContentWidth();

                layoutParams.top = titleTextCenterY-iconHalfHeight;
                layoutParams.bottom = layoutParams.top+iconHeight;
                layoutParams.left = topRowCurrentX+spacingBetweenText;
                layoutParams.right = layoutParams.left+iconWidth;
                topRowCurrentX = layoutParams.right;
            }
            if(mutedIcon.getVisibility()!=GONE){
                VirtualLayoutParams layoutParams = mutedIcon.getLayoutParams();
                int iconHeight = mutedIcon.getRawContentHeight();
                int iconHalfHeight = iconHeight/2;
                int iconWidth = mutedIcon.getRawContentWidth();

                layoutParams.top = titleTextCenterY-iconHalfHeight;
                layoutParams.bottom = layoutParams.top+iconHeight;
                layoutParams.left = topRowCurrentX+spacingBetweenText;
                layoutParams.right = layoutParams.left+iconWidth;
                topRowCurrentX = layoutParams.right;
            }
            if(emojiIcon.getVisibility()!=GONE){
                VirtualLayoutParams layoutParams = emojiIcon.getLayoutParams();
                int iconHeight = emojiIcon.getRawContentHeight();
                int iconHalfHeight = iconHeight/2;
                int iconWidth = emojiIcon.getRawContentWidth();

                layoutParams.top = titleTextCenterY-iconHalfHeight;
                layoutParams.bottom = layoutParams.top+iconHeight;
                layoutParams.left = topRowCurrentX+spacingBetweenText;
                layoutParams.right = layoutParams.left+iconWidth;
                topRowCurrentX = layoutParams.right;
            }
        } else {
            int highestIconHeight = 0;

            if(verifiedIcon.getVisibility()!=GONE){
                highestIconHeight = Math.max(highestIconHeight,verifiedIcon.getRawContentHeight());
            }
            if(mutedIcon.getVisibility()!=GONE){
                highestIconHeight = Math.max(highestIconHeight,mutedIcon.getRawContentHeight());
            }
            if(emojiIcon.getVisibility()!=GONE){
                highestIconHeight = Math.max(highestIconHeight,emojiIcon.getRawContentHeight());
            }

            int topRowVerticalCenterY = verticalCenterY-highestIconHeight/2;

            int topRowCurrentX = textStartX;

            if(verifiedIcon.getVisibility()!=GONE){
                VirtualLayoutParams layoutParams = verifiedIcon.getLayoutParams();
                int iconHeight = verifiedIcon.getRawContentHeight();
                int iconHalfHeight = iconHeight/2;
                int iconWidth = verifiedIcon.getRawContentWidth();

                layoutParams.top = topRowVerticalCenterY-iconHalfHeight;
                layoutParams.bottom = layoutParams.top+iconHeight;
                layoutParams.left = topRowCurrentX+spacingBetweenText;
                layoutParams.right = layoutParams.left+iconWidth;
                topRowCurrentX = layoutParams.right;
            }
            if(mutedIcon.getVisibility()!=GONE){
                VirtualLayoutParams layoutParams = mutedIcon.getLayoutParams();
                int iconHeight = mutedIcon.getRawContentHeight();
                int iconHalfHeight = iconHeight/2;
                int iconWidth = mutedIcon.getRawContentWidth();

                layoutParams.top = topRowVerticalCenterY-iconHalfHeight;
                layoutParams.bottom = layoutParams.top+iconHeight;
                layoutParams.left = topRowCurrentX+spacingBetweenText;
                layoutParams.right = layoutParams.left+iconWidth;
                topRowCurrentX = layoutParams.right;
            }
            if(emojiIcon.getVisibility()!=GONE){
                VirtualLayoutParams layoutParams = emojiIcon.getLayoutParams();
                int iconHeight = emojiIcon.getRawContentHeight();
                int iconHalfHeight = iconHeight/2;
                int iconWidth = emojiIcon.getRawContentWidth();

                layoutParams.top = topRowVerticalCenterY-iconHalfHeight;
                layoutParams.bottom = layoutParams.top+iconHeight;
                layoutParams.left = topRowCurrentX+spacingBetweenText;
                layoutParams.right = layoutParams.left+iconWidth;
                topRowCurrentX = layoutParams.right;
            }
        }

        if(subtitle.getVisibility()!=GONE){

            int bottomRowCurrentX = textStartX;

            VirtualLayoutParams subtitleLayoutParams = subtitle.getLayoutParams();
            subtitleLayoutParams.top = verticalCenterY;
            subtitleLayoutParams.bottom = subtitleLayoutParams.top+subtitle.getRawContentHeight();
            subtitleLayoutParams.left = bottomRowCurrentX;
            subtitleLayoutParams.right = subtitleLayoutParams.left+subtitle.getRawContentWidth();
            bottomRowCurrentX = subtitleLayoutParams.right;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);

        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingStart = getPaddingEnd();
        int paddingEnd = getPaddingStart();

        int availableWidth = widthSize-(paddingStart+paddingEnd);

        int minTop = 0;
        int minLeft = 0;
        int maxBottom = 0;
        int maxRight = 0;

        maxBottom+=avatarSize;
        maxRight+=avatarSize+spacingBetweenAvatarAndText;

        int verticalCenterY = avatarSize/2;

        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//

        int topRowMaxRight = maxRight;
        int topRowMinTop = verticalCenterY;
        int topRowMaxBottom = verticalCenterY;

        if(title.getVisibility()!=GONE){
            int titleHeight = title.getRawContentHeight();
            topRowMinTop = verticalCenterY-titleHeight;

            Paint.FontMetrics titleFontMetrics = title.getPaint().getFontMetrics();
            int titleBaselineY = verticalCenterY-(int)titleFontMetrics.bottom;
            int titleAscentY = titleBaselineY+(int)titleFontMetrics.ascent;
            int titleTextCenterY = (titleBaselineY+titleAscentY)/2;

            int visibleCount = 0;

            if(verifiedIcon.getVisibility()!=GONE) {
                int halfIconHeight = verifiedIcon.getRawContentHeight()/2;
                topRowMinTop = Math.min(topRowMinTop,titleTextCenterY-halfIconHeight);
                topRowMaxBottom = Math.max(topRowMaxBottom, titleTextCenterY+halfIconHeight);
                topRowMaxRight+=verifiedIcon.getRawContentWidth();
                visibleCount++;
            }

            if(mutedIcon.getVisibility()!=GONE) {
                int halfIconHeight = mutedIcon.getRawContentHeight()/2;
                topRowMinTop = Math.min(topRowMinTop,titleTextCenterY-halfIconHeight);
                topRowMaxBottom = Math.max(topRowMaxBottom, titleTextCenterY+halfIconHeight);
                topRowMaxRight+=mutedIcon.getRawContentWidth();
                visibleCount++;
            }

            if(emojiIcon.getVisibility()!=GONE) {
                int halfIconHeight = emojiIcon.getRawContentHeight()/2;
                topRowMinTop = Math.min(topRowMinTop,titleTextCenterY-halfIconHeight);
                topRowMaxBottom = Math.max(topRowMaxBottom, titleTextCenterY+halfIconHeight);
                topRowMaxRight+=emojiIcon.getRawContentWidth();
                visibleCount++;
            }

            topRowMaxRight+=spacingBetweenText*visibleCount;

            int titleWidth = title.getRawOriginalMeasuredWidth();
            if(titleWidth>0) {
                int topRowRightWithFullTitle = topRowMaxRight + titleWidth;
                if (topRowRightWithFullTitle > availableWidth) {
                    int desiredWidth = Math.max(0,availableWidth-topRowMaxRight);
                    title.trimText(desiredWidth);
                    titleWidth = title.getRawContentWidth();
                } else {
                    title.restoreText();
                }
                topRowMaxRight+=titleWidth;
            }

        } else {
            int visibleCount = 0;

            if(verifiedIcon.getVisibility()!=GONE) {
                topRowMinTop = Math.min(topRowMinTop, verticalCenterY-verifiedIcon.getRawContentHeight());
                topRowMaxRight+=verifiedIcon.getRawContentWidth();
                visibleCount++;
            }

            if(mutedIcon.getVisibility()!=GONE) {
                topRowMinTop = Math.min(topRowMinTop, verticalCenterY-mutedIcon.getRawContentHeight());
                topRowMaxRight+=mutedIcon.getRawContentWidth();
                visibleCount++;
            }

            if(emojiIcon.getVisibility()!=GONE) {
                topRowMinTop = Math.min(topRowMinTop,verticalCenterY-emojiIcon.getRawContentHeight());
                topRowMaxRight+=emojiIcon.getRawContentWidth();
                visibleCount++;
            }

            topRowMaxRight+=spacingBetweenText*(visibleCount-1);
        }

        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//

        int bottomRowMaxRight = maxRight;
        int bottomRowMinTop = verticalCenterY;
        int bottomRowMaxBottom = verticalCenterY;

        if(subtitle.getVisibility()!=GONE){
            int subtitleHeight = subtitle.getRawContentHeight();
            bottomRowMaxBottom = verticalCenterY+subtitleHeight;

            int subtitleWidth = subtitle.getRawOriginalMeasuredWidth();
            if(subtitleWidth>0) {
                int bottomRowRightWithFullSubtitle = bottomRowMaxRight + subtitleWidth;
                if (bottomRowRightWithFullSubtitle > availableWidth) {
                    int desiredWidth = Math.max(0,availableWidth-bottomRowMaxRight);
                    subtitle.trimText(desiredWidth);
                    subtitleWidth = subtitle.getRawContentWidth();
                } else {
                    subtitle.restoreText();
                }
                bottomRowMaxRight+=subtitleWidth;
            }
        }


        int minRowsTop = Math.min(topRowMinTop,bottomRowMinTop);
        int maxRowsBottom = Math.max(topRowMaxBottom,bottomRowMaxBottom);
        int maxRowsRight = Math.max(topRowMaxRight, bottomRowMaxRight);

        minTop = Math.min(minTop, minRowsTop);
        maxBottom = Math.max(maxBottom, maxRowsBottom);
        maxRight = Math.max(maxRight, maxRowsRight);

        int contentWidth = Math.abs(minLeft)+maxRight+paddingStart+paddingEnd;
        int contentHeight = Math.abs(minTop)+maxBottom+paddingTop+paddingBottom;


        int wMeasureSpec = MeasureSpec.makeMeasureSpec(avatarSize, MeasureSpec.EXACTLY);
        int hMeasureSpec = MeasureSpec.makeMeasureSpec(avatarSize, MeasureSpec.EXACTLY);
        avatarImage.measure(wMeasureSpec, hMeasureSpec);


        setMeasuredDimension(resolveSizeAndState(contentWidth,widthMeasureSpec,0),
                resolveSizeAndState(contentHeight,heightMeasureSpec,0));

    }

    public void setTitleText(String titleText) {
        if(titleText!=null&&!titleText.isEmpty()) {
            title.setText(titleText);
            title.setVisibility(VISIBLE);
            //requestLayout();
        } else {
            if (title.getVisibility()!=GONE) {
                title.setVisibility(GONE);
                //requestLayout();
            }
        }
    }

    public void setSubtitleText(String subtitleText) {
        if(subtitleText!=null&&!subtitleText.isEmpty()) {
            subtitle.setText(subtitleText);
            subtitle.setVisibility(VISIBLE);
            //requestLayout();
        } else {
            if (subtitle.getVisibility()!=GONE) {
                subtitle.setVisibility(GONE);
                //requestLayout();
            }
        }
    }

    public void setMuted(boolean muted) {
        if(muted) {
            if (mutedIcon.getVisibility() != VISIBLE) {
                mutedIcon.setVisibility(VISIBLE);
                //requestLayout();
            }
        } else {
            if (mutedIcon.getVisibility() != GONE) {
                mutedIcon.setVisibility(GONE);
                //requestLayout();
            }
        }
    }

    public void setVerified(boolean verified) {
        if(verified) {
            if (verifiedIcon.getVisibility() != VISIBLE) {
                verifiedIcon.setVisibility(VISIBLE);
                //requestLayout();
            }
        } else {
            if (verifiedIcon.getVisibility() != GONE) {
                verifiedIcon.setVisibility(GONE);
                //requestLayout();
            }
        }
    }

    public void setOnline(boolean online, boolean mobile){
        if(online){
            if(mobile){
                onlineIcon.setState(VirtualOnlineIcon.State.STATE_MOBILE);
            } else {
                onlineIcon.setState(VirtualOnlineIcon.State.STATE_ONLINE);
            }
        } else {
            onlineIcon.setState(VirtualOnlineIcon.State.STATE_OFFLINE);
        }
    }

    public ShapeableImageView getAvatarImage() {
        return avatarImage;
    }

}
