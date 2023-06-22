package com.vkontakte.miracle.view.messages;

import android.annotation.SuppressLint;
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

public class ConversationView extends ViewGroup {

    private static final int DEF_STYLE_RES = R.style.ConversationItemView;

    private final ShapeableImageView avatarImage;
    private int avatarSize;

    private final VirtualOnlineIcon onlineIcon;

    private final VirtualTextView title;
    private final VirtualTextView messageOwner;
    private final VirtualTextView message;
    private final VirtualTextView actionMessage;
    private final VirtualTextView date;

    private final VirtualTextView inReadCounter;
    private final VirtualImageView inReadCounterBackground;
    private int inReadCounterPaddingVertical;
    private int inReadCounterPaddingHorizontal;

    private final VirtualImageView outReadIcon;
    private final VirtualImageView mutedIcon;
    private final VirtualImageView verifiedIcon;

    private final VirtualImageView typingMessageIndeterminate;
    private final VirtualImageView recordingVoiceMessageIndeterminate;

    private final VirtualImageView emojiIcon;

    private int spacingBetweenAvatarAndText;
    private int readMarkerSpacing;
    private int spacingBetweenText;

    private boolean mAggregatedIsVisible = true;

    public ConversationView(Context context) {
        this(context, null);
    }

    public ConversationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ConversationView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, DEF_STYLE_RES);
    }

    @SuppressLint("RestrictedApi")
    public ConversationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        Resources.Theme theme = context.getTheme();

        final TypedArray a = theme.obtainStyledAttributes(attrs, R.styleable.ConversationView, defStyleAttr, defStyleRes);

        spacingBetweenAvatarAndText = a.getDimensionPixelSize(R.styleable.ConversationView_spacingBetweenAvatarAndText, 0);
        readMarkerSpacing = a.getDimensionPixelSize(R.styleable.ConversationView_readMarkerSpacing, 0);
        spacingBetweenText = a.getDimensionPixelSize(R.styleable.ConversationView_spacingBetweenText, 0);

        title = new VirtualTextView(this, a, R.styleable.ConversationView_titleTextAppearance);
        messageOwner = new VirtualTextView(this, a, R.styleable.ConversationView_messageOwnerTextAppearance);
        message = new VirtualTextView(this, a, R.styleable.ConversationView_messageTextAppearance);
        actionMessage = new VirtualTextView(this, a, R.styleable.ConversationView_actionMessageTextAppearance);
        date = new VirtualTextView(this, a, R.styleable.ConversationView_dateTextAppearance);

        inReadCounter = new VirtualTextView(this, a, R.styleable.ConversationView_inReadCounterTextAppearance);
        inReadCounterPaddingVertical = a.getDimensionPixelSize(R.styleable.ConversationView_inReadCounterPaddingVertical, 0);
        inReadCounterPaddingHorizontal = a.getDimensionPixelSize(R.styleable.ConversationView_inReadCounterPaddingHorizontal, 0);
        inReadCounterBackground = new VirtualImageView(this, a,
                0,0,
                R.styleable.ConversationView_inReadCounterBackground,
                R.styleable.ConversationView_inReadCounterBackgroundTint,
                R.styleable.ConversationView_inReadCounterBackgroundTintMode);

       outReadIcon = new VirtualImageView(this, a,
               R.styleable.ConversationView_outReadIconSize,
               R.styleable.ConversationView_outReadIconSize,
               R.styleable.ConversationView_outReadIcon,
               R.styleable.ConversationView_outReadIconTint,
               R.styleable.ConversationView_outReadIconTintMode);

        mutedIcon = new VirtualImageView(this, a,
                R.styleable.ConversationView_mutedIconSize,
                R.styleable.ConversationView_mutedIconSize,
                R.styleable.ConversationView_mutedIcon,
                R.styleable.ConversationView_mutedIconTint,
                R.styleable.ConversationView_mutedIconTintMode);

        verifiedIcon = new VirtualImageView(this, a,
                R.styleable.ConversationView_verifiedIconSize,
                R.styleable.ConversationView_verifiedIconSize,
                R.styleable.ConversationView_verifiedIcon,
                R.styleable.ConversationView_verifiedIconTint,
                R.styleable.ConversationView_verifiedIconTintMode);

        typingMessageIndeterminate = new VirtualImageView(this, a,
                R.styleable.ConversationView_typingMessageIndeterminateDrawableWidth,
                R.styleable.ConversationView_typingMessageIndeterminateDrawableHeight,
                R.styleable.ConversationView_typingMessageIndeterminateDrawable,
                R.styleable.ConversationView_typingMessageIndeterminateDrawableTint,
                R.styleable.ConversationView_typingMessageIndeterminateDrawableTintMode);

        recordingVoiceMessageIndeterminate = new VirtualImageView(this, a,
                R.styleable.ConversationView_recordingVoiceMessageIndeterminateDrawableWidth,
                R.styleable.ConversationView_recordingVoiceMessageIndeterminateDrawableHeight,
                R.styleable.ConversationView_recordingVoiceMessageIndeterminateDrawable,
                R.styleable.ConversationView_recordingVoiceMessageIndeterminateDrawableTint,
                R.styleable.ConversationView_recordingVoiceMessageIndeterminateDrawableTintMode);

        emojiIcon = new VirtualImageView(this, a,
                R.styleable.ConversationView_emojiIconSize,
                R.styleable.ConversationView_emojiIconSize,
                0, 0, 0);

        avatarSize = a.getDimensionPixelSize(R.styleable.ConversationView_avatarImageSize, 0);

        avatarImage = new ShapeableImageView( new ContextThemeWrapper(context, a.getResourceId(R.styleable.ConversationView_avatarImageStyle,
                R.style.ConversationItemView_Avatar)));

        onlineIcon = new VirtualOnlineIcon(this, a,
                R.styleable.ConversationView_onlineIconSize,
                R.styleable.ConversationView_onlineIconTint,
                R.styleable.ConversationView_onlineIconSubtractTint,
                R.styleable.ConversationView_onlineIconTintMode,
                R.styleable.ConversationView_onlineMobileIcon,
                R.styleable.ConversationView_onlineMobileIconSubtract,
                R.styleable.ConversationView_onlineIcon,
                R.styleable.ConversationView_onlineIconSubtract);

        a.recycle();

        setWillNotDraw(false);

        addView(avatarImage);

        outReadIcon.setVisibility(GONE);
        inReadCounter.setVisibility(GONE);
        inReadCounterBackground.setVisibility(GONE);
        title.setVisibility(GONE);
        verifiedIcon.setVisibility(GONE);
        mutedIcon.setVisibility(GONE);
        emojiIcon.setVisibility(GONE);
        messageOwner.setVisibility(GONE);
        message.setVisibility(GONE);
        actionMessage.setVisibility(GONE);
        typingMessageIndeterminate.setVisibility(GONE);
        recordingVoiceMessageIndeterminate.setVisibility(GONE);
        date.setVisibility(GONE);

    }

    //---------------------------------------------------------------------//

    @Override
    public void onVisibilityAggregated(boolean isVisible) {
        super.onVisibilityAggregated(isVisible);
        if (isVisible != mAggregatedIsVisible) {
            mAggregatedIsVisible = isVisible;

            typingMessageIndeterminate.onVisibilityAggregated(isVisible);
            recordingVoiceMessageIndeterminate.onVisibilityAggregated(isVisible);
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        int[] drawableState = getDrawableState();

        title.drawableStateChanged(drawableState);

        messageOwner.drawableStateChanged(drawableState);

        message.drawableStateChanged(drawableState);

        actionMessage.drawableStateChanged(drawableState);

        date.drawableStateChanged(drawableState);

        verifiedIcon.drawableStateChanged(drawableState);

        mutedIcon.drawableStateChanged(drawableState);

        outReadIcon.drawableStateChanged(drawableState);

        inReadCounter.drawableStateChanged(drawableState);

        inReadCounterBackground.drawableStateChanged(drawableState);

        typingMessageIndeterminate.drawableStateChanged(drawableState);

        recordingVoiceMessageIndeterminate.drawableStateChanged(drawableState);

        onlineIcon.drawableStateChanged(drawableState);

    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();

        verifiedIcon.jumpDrawablesToCurrentState();

        mutedIcon.jumpDrawablesToCurrentState();

        outReadIcon.jumpDrawablesToCurrentState();

        typingMessageIndeterminate.jumpDrawablesToCurrentState();

        recordingVoiceMessageIndeterminate.jumpDrawablesToCurrentState();

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

        verifiedIcon.drawOnCanvas(canvas);

        mutedIcon.drawOnCanvas(canvas);

        emojiIcon.drawOnCanvas(canvas);

        messageOwner.drawOnCanvas(canvas);

        message.drawOnCanvas(canvas);

        actionMessage.drawOnCanvas(canvas);

        date.drawOnCanvas(canvas);

        outReadIcon.drawOnCanvas(canvas);

        inReadCounterBackground.drawOnCanvas(canvas);

        inReadCounter.drawOnCanvas(canvas);

        typingMessageIndeterminate.drawOnCanvas(canvas);

        recordingVoiceMessageIndeterminate.drawOnCanvas(canvas);

    }

    //---------------------------------------------------------------------//

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
            VirtualLayoutParams layoutParams = onlineIcon.getLayoutParams();
            layoutParams.bottom = avatarBottom;
            layoutParams.top = layoutParams.bottom-onlineIcon.getRawContentHeight();
            layoutParams.right = avatarRight;
            layoutParams.left = layoutParams.right-onlineIcon.getRawContentWidth();
        }

        int textStartX = avatarRight+spacingBetweenAvatarAndText;

        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//

        if(outReadIcon.getVisibility()!=GONE){
            VirtualLayoutParams layoutParams = outReadIcon.getLayoutParams();
            layoutParams.top = verticalCenterY-outReadIcon.getRawContentHeight()/2;
            layoutParams.bottom = layoutParams.top+outReadIcon.getRawContentHeight();
            layoutParams.right = widthSize-paddingEnd;
            layoutParams.left = layoutParams.right-outReadIcon.getRawContentWidth();
        } else if(inReadCounter.getVisibility()!=GONE){
            Paint.FontMetrics fontMetrics = inReadCounter.getPaint().getFontMetrics();
            int counterTextWidth = inReadCounter.getRawContentWidth();
            int counterTextHeight = (int)(-fontMetrics.ascent);
            int counterWidth = counterTextWidth+inReadCounterPaddingHorizontal*2;
            int counterHeight = counterTextHeight+inReadCounterPaddingVertical*2;

            VirtualLayoutParams counterBgParams = inReadCounterBackground.getLayoutParams();
            counterBgParams.top = verticalCenterY-counterHeight/2;
            counterBgParams.bottom = counterBgParams.top+counterHeight;
            counterBgParams.right = widthSize-paddingEnd;
            counterBgParams.left = counterBgParams.right-counterWidth;

            VirtualLayoutParams counterTextParams = inReadCounter.getLayoutParams();
            counterTextParams.top = counterBgParams.top+inReadCounterPaddingVertical
                    +(int)(fontMetrics.top-fontMetrics.ascent);
            counterTextParams.bottom = counterTextParams.top+counterTextHeight;
            counterTextParams.right = counterBgParams.right-inReadCounterPaddingHorizontal;
            counterTextParams.left = counterTextParams.right-counterTextWidth;
        }

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

        VirtualTextView highestText = null;
        int highestTextHeight = 0;

        if(messageOwner.getVisibility()!=GONE) {
            if(messageOwner.getRawContentHeight()>highestTextHeight){
                highestTextHeight = messageOwner.getRawContentHeight();
                highestText = messageOwner;
            }
        }
        if(message.getVisibility()!=GONE) {
            if(message.getRawContentHeight()>highestTextHeight){
                highestTextHeight = message.getRawContentHeight();
                highestText = message;
            }
        }
        if(actionMessage.getVisibility()!=GONE) {
            if(actionMessage.getRawContentHeight()>highestTextHeight){
                highestTextHeight = actionMessage.getRawContentHeight();
                highestText = actionMessage;
            }
        }
        if(date.getVisibility()!=GONE) {
            if(date.getRawContentHeight()>highestTextHeight){
                highestTextHeight = date.getRawContentHeight();
                highestText = date;
            }
        }

        if(highestText!=null) {

            Paint.FontMetrics highestTextFontMetrics = highestText.getPaint().getFontMetrics();
            int highestTextBaselineY = verticalCenterY - (int) highestTextFontMetrics.top;
            int highestTextAscentY = highestTextBaselineY + (int) highestTextFontMetrics.ascent;
            int highestTextCenterY = (highestTextBaselineY + highestTextAscentY) / 2;

            int bottomRowCurrentX = textStartX;

            if(messageOwner.getVisibility()!=GONE) {
                VirtualLayoutParams layoutParams = messageOwner.getLayoutParams();
                layoutParams.top = highestTextBaselineY+(int)messageOwner.getPaint().getFontMetrics().top;
                layoutParams.bottom = layoutParams.top+messageOwner.getRawContentHeight();
                layoutParams.left = bottomRowCurrentX;
                layoutParams.right = layoutParams.left+messageOwner.getRawContentWidth();
                bottomRowCurrentX = layoutParams.right+spacingBetweenText;
            }

            if(message.getVisibility()!=GONE) {
                VirtualLayoutParams layoutParams = message.getLayoutParams();
                layoutParams.top = highestTextBaselineY+(int)message.getPaint().getFontMetrics().top;
                layoutParams.bottom = layoutParams.top+message.getRawContentHeight();
                layoutParams.left = bottomRowCurrentX;
                layoutParams.right = layoutParams.left+message.getRawContentWidth();
                bottomRowCurrentX = layoutParams.right+spacingBetweenText;
            }

            if(actionMessage.getVisibility()!=GONE) {
                VirtualLayoutParams layoutParams = actionMessage.getLayoutParams();
                layoutParams.top = highestTextBaselineY+(int)actionMessage.getPaint().getFontMetrics().top;
                layoutParams.bottom = layoutParams.top+actionMessage.getRawContentHeight();
                layoutParams.left = bottomRowCurrentX;
                layoutParams.right = layoutParams.left+actionMessage.getRawContentWidth();
                bottomRowCurrentX = layoutParams.right+spacingBetweenText;
            }

            if(typingMessageIndeterminate.getVisibility()!=GONE){
                VirtualLayoutParams layoutParams = typingMessageIndeterminate.getLayoutParams();
                int iconHeight = typingMessageIndeterminate.getRawContentHeight();
                int iconHalfHeight = iconHeight/2;
                int iconWidth = typingMessageIndeterminate.getRawContentWidth();

                layoutParams.top = highestTextCenterY-iconHalfHeight;
                layoutParams.bottom = layoutParams.top+iconHeight;
                layoutParams.left = bottomRowCurrentX+spacingBetweenText;
                layoutParams.right = layoutParams.left+iconWidth;
                bottomRowCurrentX = layoutParams.right;
            }

            if(recordingVoiceMessageIndeterminate.getVisibility()!=GONE){
                VirtualLayoutParams layoutParams = recordingVoiceMessageIndeterminate.getLayoutParams();
                int iconHeight = recordingVoiceMessageIndeterminate.getRawContentHeight();
                int iconHalfHeight = iconHeight/2;
                int iconWidth = recordingVoiceMessageIndeterminate.getRawContentWidth();

                layoutParams.top = highestTextCenterY-iconHalfHeight;
                layoutParams.bottom = layoutParams.top+iconHeight;
                layoutParams.left = bottomRowCurrentX+spacingBetweenText;
                layoutParams.right = layoutParams.left+iconWidth;
                bottomRowCurrentX = layoutParams.right;
            }

            if(date.getVisibility()!=GONE) {
                VirtualLayoutParams layoutParams = date.getLayoutParams();
                layoutParams.top = highestTextBaselineY+(int)date.getPaint().getFontMetrics().top;
                layoutParams.bottom = layoutParams.top+date.getRawContentHeight();
                layoutParams.left = bottomRowCurrentX;
                layoutParams.right = layoutParams.left+date.getRawContentWidth();
                bottomRowCurrentX = layoutParams.right+spacingBetweenText;
            }

        } else {

            int highestIconHeight = 0;

            if(typingMessageIndeterminate.getVisibility()!=GONE){
                highestIconHeight = Math.max(highestIconHeight,typingMessageIndeterminate.getRawContentHeight());
            }
            if(recordingVoiceMessageIndeterminate.getVisibility()!=GONE){
                highestIconHeight = Math.max(highestIconHeight,recordingVoiceMessageIndeterminate.getRawContentHeight());
            }

            int bottomRowVerticalCenterY = verticalCenterY+highestIconHeight/2;

            int bottomRowCurrentX = textStartX;

            if(typingMessageIndeterminate.getVisibility()!=GONE){
                VirtualLayoutParams layoutParams = typingMessageIndeterminate.getLayoutParams();
                int iconHeight = typingMessageIndeterminate.getRawContentHeight();
                int iconHalfHeight = iconHeight/2;
                int iconWidth = typingMessageIndeterminate.getRawContentWidth();

                layoutParams.top = bottomRowVerticalCenterY-iconHalfHeight;
                layoutParams.bottom = layoutParams.top+iconHeight;
                layoutParams.left = bottomRowCurrentX+spacingBetweenText;
                layoutParams.right = layoutParams.left+iconWidth;
                bottomRowCurrentX = layoutParams.right;
            }

            if(recordingVoiceMessageIndeterminate.getVisibility()!=GONE){
                VirtualLayoutParams layoutParams = recordingVoiceMessageIndeterminate.getLayoutParams();
                int iconHeight = recordingVoiceMessageIndeterminate.getRawContentHeight();
                int iconHalfHeight = iconHeight/2;
                int iconWidth = recordingVoiceMessageIndeterminate.getRawContentWidth();

                layoutParams.top = bottomRowVerticalCenterY-iconHalfHeight;
                layoutParams.bottom = layoutParams.top+iconHeight;
                layoutParams.left = bottomRowCurrentX+spacingBetweenText;
                layoutParams.right = layoutParams.left+iconWidth;
                bottomRowCurrentX = layoutParams.right;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);

        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingStart = getPaddingStart();
        int paddingEnd = getPaddingEnd();

        int availableWidth = widthSize-(paddingStart+paddingEnd);

        int minTop = 0;
        int minLeft = 0;
        int maxBottom = 0;
        int maxRight = 0;

        maxBottom+=avatarSize;
        maxRight+=avatarSize+spacingBetweenAvatarAndText;

        int verticalCenterY = avatarSize/2;

        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//

        int indicatorWidth = 0;
        int indicatorHeight = 0;

        if(outReadIcon.getVisibility()!=GONE){
            indicatorWidth = outReadIcon.getRawContentWidth();
            indicatorHeight = outReadIcon.getRawContentHeight();
        } else if(inReadCounter.getVisibility()!=GONE){
            Paint.FontMetrics fontMetrics = inReadCounter.getPaint().getFontMetrics();
            indicatorWidth = inReadCounter.getRawContentWidth()+(inReadCounterPaddingHorizontal*2);
            indicatorHeight = (int)(-fontMetrics.ascent)+(inReadCounterPaddingVertical*2);
        }

        int indicatorMinTop = verticalCenterY-(indicatorHeight/2);
        int indicatorMaxBottom = indicatorMinTop+indicatorHeight;

        availableWidth-=(indicatorWidth+readMarkerSpacing);

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

        VirtualTextView highestText = null;
        int highestTextHeight = 0;

        if(messageOwner.getVisibility()!=GONE) {
            if(messageOwner.getRawContentHeight()>highestTextHeight){
                highestTextHeight = messageOwner.getRawContentHeight();
                highestText = messageOwner;
            }
        }
        if(message.getVisibility()!=GONE) {
            if(message.getRawContentHeight()>highestTextHeight){
                highestTextHeight = message.getRawContentHeight();
                highestText = message;
            }
        }
        if(actionMessage.getVisibility()!=GONE) {
            if(actionMessage.getRawContentHeight()>highestTextHeight){
                highestTextHeight = actionMessage.getRawContentHeight();
                highestText = actionMessage;
            }
        }
        if(date.getVisibility()!=GONE) {
            if(date.getRawContentHeight()>highestTextHeight){
                highestTextHeight = date.getRawContentHeight();
                highestText = date;
            }
        }

        if(highestText!=null){
            bottomRowMaxBottom = verticalCenterY+highestTextHeight;

            Paint.FontMetrics highestTextFontMetrics = highestText.getPaint().getFontMetrics();
            int highestTextBaselineY = verticalCenterY-(int)highestTextFontMetrics.top;
            int highestTextAscentY = highestTextBaselineY+(int)highestTextFontMetrics.ascent;
            int highestTextCenterY = (highestTextBaselineY+highestTextAscentY)/2;

            int visibleCount = 0;

            if(messageOwner.getVisibility()!=GONE) {
                Paint.FontMetrics fontMetrics = messageOwner.getPaint().getFontMetrics();
                bottomRowMinTop = Math.min(bottomRowMinTop, highestTextBaselineY+(int)fontMetrics.top);
                bottomRowMaxBottom = Math.max(bottomRowMaxBottom, highestTextBaselineY+(int)fontMetrics.bottom);
                bottomRowMaxRight+=messageOwner.getRawContentWidth();
                visibleCount++;
            }
            if(message.getVisibility()!=GONE) {
                Paint.FontMetrics fontMetrics = message.getPaint().getFontMetrics();
                bottomRowMinTop = Math.min(bottomRowMinTop, highestTextBaselineY+(int)fontMetrics.top);
                bottomRowMaxBottom = Math.max(bottomRowMaxBottom, highestTextBaselineY+(int)fontMetrics.bottom);
                //bottomRowMaxRight = message.getRawOriginalMeasuredWidth();
                visibleCount++;
            }
            if(actionMessage.getVisibility()!=GONE) {
                Paint.FontMetrics fontMetrics = actionMessage.getPaint().getFontMetrics();
                bottomRowMinTop = Math.min(bottomRowMinTop, highestTextBaselineY+(int)fontMetrics.top);
                bottomRowMaxBottom = Math.max(bottomRowMaxBottom, highestTextBaselineY+(int)fontMetrics.bottom);
                //bottomRowMaxRight = actionMessage.getRawOriginalMeasuredWidth();
                visibleCount++;
            }

            if(typingMessageIndeterminate.getVisibility()!=GONE) {
                int halfIconHeight = typingMessageIndeterminate.getRawContentHeight()/2;
                bottomRowMinTop = Math.min(topRowMinTop,highestTextCenterY-halfIconHeight);
                bottomRowMaxBottom = Math.max(topRowMaxBottom, highestTextCenterY+halfIconHeight);
                bottomRowMaxRight+=typingMessageIndeterminate.getRawContentWidth();
                visibleCount++;
            }

            if(recordingVoiceMessageIndeterminate.getVisibility()!=GONE) {
                int halfIconHeight = recordingVoiceMessageIndeterminate.getRawContentHeight()/2;
                bottomRowMinTop = Math.min(topRowMinTop,highestTextCenterY-halfIconHeight);
                bottomRowMaxBottom = Math.max(topRowMaxBottom, highestTextCenterY+halfIconHeight);
                bottomRowMaxRight+=recordingVoiceMessageIndeterminate.getRawContentWidth();
                visibleCount++;
            }

            if(date.getVisibility()!=GONE) {
                Paint.FontMetrics fontMetrics = date.getPaint().getFontMetrics();
                bottomRowMinTop = Math.min(bottomRowMinTop, highestTextBaselineY+(int)fontMetrics.top);
                bottomRowMaxBottom = Math.max(bottomRowMaxBottom, highestTextBaselineY+(int)fontMetrics.bottom);
                bottomRowMaxRight+=date.getRawContentWidth();
                visibleCount++;
            }

            bottomRowMaxRight+=spacingBetweenText*(visibleCount-1);

            if(message.getVisibility()!=GONE&&actionMessage.getVisibility()!=GONE){
                int messagesWidth = message.getRawOriginalMeasuredWidth()+actionMessage.getRawOriginalMeasuredWidth();
                if(messagesWidth>0) {
                    int bottomRowRightWithFullMessages = bottomRowMaxRight + messagesWidth;
                    if (bottomRowRightWithFullMessages > availableWidth) {
                        int desiredWidth = Math.max(0,(availableWidth-bottomRowMaxRight)/2);
                        message.trimText(desiredWidth);
                        actionMessage.trimText(desiredWidth);
                        messagesWidth = message.getRawContentWidth()+actionMessage.getRawContentWidth();
                    } else {
                        message.restoreText();
                        actionMessage.restoreText();
                    }
                    bottomRowMaxRight+=messagesWidth;
                }
            } else if(message.getVisibility()!=GONE) {
                int messageWidth = message.getRawOriginalMeasuredWidth();
                if(messageWidth>0) {
                    int bottomRowRightWithFullMessage = bottomRowMaxRight + messageWidth;
                    if (bottomRowRightWithFullMessage > availableWidth) {
                        int desiredWidth = Math.max(0,availableWidth-bottomRowMaxRight);
                        message.trimText(desiredWidth);
                        messageWidth = message.getRawContentWidth();
                    } else {
                        message.restoreText();
                    }
                    bottomRowMaxRight+=messageWidth;
                }
            } else if(actionMessage.getVisibility()!=GONE){
                int actionMessageWidth = actionMessage.getRawOriginalMeasuredWidth();
                if(actionMessageWidth>0) {
                    int bottomRowRightWithFullActionMessage = bottomRowMaxRight + actionMessageWidth;
                    if (bottomRowRightWithFullActionMessage > availableWidth) {
                        int desiredWidth = Math.max(0,availableWidth-bottomRowMaxRight);
                        actionMessage.trimText(desiredWidth);
                        actionMessageWidth = actionMessage.getRawContentWidth();
                    } else {
                        actionMessage.restoreText();
                    }
                    bottomRowMaxRight+=actionMessageWidth;
                }
            }

        } else {

            int visibleCount = 0;

            if(typingMessageIndeterminate.getVisibility()!=GONE) {
                bottomRowMaxBottom = Math.max(bottomRowMaxBottom,
                        verticalCenterY+typingMessageIndeterminate.getRawContentHeight());
                topRowMaxRight+=typingMessageIndeterminate.getRawContentWidth();
                visibleCount++;
            }

            if(recordingVoiceMessageIndeterminate.getVisibility()!=GONE) {
                bottomRowMaxBottom = Math.max(bottomRowMaxBottom,
                        verticalCenterY+recordingVoiceMessageIndeterminate.getRawContentHeight());
                topRowMaxRight+=recordingVoiceMessageIndeterminate.getRawContentWidth();
                visibleCount++;
            }

            bottomRowMaxRight+=spacingBetweenText*(visibleCount-1);
        }

        int minRowsTop = Math.min(topRowMinTop,bottomRowMinTop);
        int maxRowsBottom = Math.max(topRowMaxBottom,bottomRowMaxBottom);
        int maxRowsRight = Math.max(topRowMaxRight, bottomRowMaxRight);

        minTop = Math.min(minTop, minRowsTop);
        minTop = Math.min(minTop, indicatorMinTop);

        maxBottom = Math.max(maxBottom, maxRowsBottom);
        maxBottom = Math.max(maxBottom, indicatorMaxBottom);

        maxRight = Math.max(maxRight, maxRowsRight);

        int contentWidth = Math.abs(minLeft)+maxRight+indicatorWidth+paddingStart+paddingEnd;
        int contentHeight = Math.abs(minTop)+maxBottom+paddingTop+paddingBottom;


        int wMeasureSpec = MeasureSpec.makeMeasureSpec(avatarSize, MeasureSpec.EXACTLY);
        int hMeasureSpec = MeasureSpec.makeMeasureSpec(avatarSize, MeasureSpec.EXACTLY);
        avatarImage.measure(wMeasureSpec, hMeasureSpec);


        setMeasuredDimension(resolveSizeAndState(contentWidth,widthMeasureSpec,0),
                resolveSizeAndState(contentHeight,heightMeasureSpec,0));

    }

    //---------------------------------------------------------------------//

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

    public void setMessageOwnerText(String messageOwnerText) {
        if(messageOwnerText!=null&&!messageOwnerText.isEmpty()) {
            messageOwner.setText(messageOwnerText + ":");
            messageOwner.setVisibility(VISIBLE);
            //requestLayout();
        } else {
            if (messageOwner.getVisibility()!=GONE) {
                messageOwner.setVisibility(GONE);
                //requestLayout();
            }
        }
    }

    public void setMessageText(String messageText) {
        if(messageText!=null&&!messageText.isEmpty()) {
            message.setText(messageText);
            message.setVisibility(VISIBLE);
            //requestLayout();
        } else {
            if (message.getVisibility()!=GONE) {
                message.setVisibility(GONE);
                //requestLayout();
            }
        }
    }

    public void setActionMessageText(String actionMessageText) {
        if(actionMessageText!=null&&!actionMessageText.isEmpty()) {
            actionMessage.setText(actionMessageText);
            actionMessage.setVisibility(VISIBLE);
            //requestLayout();
        } else {
            if (actionMessage.getVisibility()!=GONE) {
                actionMessage.setVisibility(GONE);
                //requestLayout();
            }
        }
    }

    public void setTypingMessage(boolean typingMessage) {
        if(typingMessage) {
            typingMessageIndeterminate.setVisibility(VISIBLE);
            //requestLayout();
        } else {
            if (typingMessageIndeterminate.getVisibility()!=GONE) {
                typingMessageIndeterminate.setVisibility(GONE);
                //requestLayout();
            }
        }
    }

    public void setRecordingVoiceMessage(boolean recordingVoiceMessage) {
        if(recordingVoiceMessage) {
            recordingVoiceMessageIndeterminate.setVisibility(VISIBLE);
            //requestLayout();
        } else {
            if (recordingVoiceMessageIndeterminate.getVisibility()!=GONE) {
                recordingVoiceMessageIndeterminate.setVisibility(GONE);
                //requestLayout();
            }
        }
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

    public void setUnreadInCount(int unreadInCount) {
        if(unreadInCount>0) {
            inReadCounter.setText(String.valueOf(unreadInCount));
            inReadCounter.setVisibility(VISIBLE);
            inReadCounterBackground.setVisibility(VISIBLE);
            //requestLayout();
        } else {
            if (inReadCounterBackground.getVisibility()!=GONE) {
                inReadCounter.setVisibility(GONE);
                inReadCounterBackground.setVisibility(GONE);
                //requestLayout();
            }
        }
    }

    public void setUnreadOut(boolean unreadOut) {
        if(unreadOut) {
            if (outReadIcon.getVisibility() != VISIBLE) {
                outReadIcon.setVisibility(VISIBLE);
                //requestLayout();
            }
        } else {
            if (outReadIcon.getVisibility() != GONE) {
                outReadIcon.setVisibility(GONE);
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
