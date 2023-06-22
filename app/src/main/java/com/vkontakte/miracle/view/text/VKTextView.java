package com.vkontakte.miracle.view.text;

import static androidx.core.util.PatternsCompat.AUTOLINK_WEB_URL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.vkontakte.miracle.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VKTextView extends AppCompatTextView {

    private OnUrlClickListener onUrlClickListener;
    private OnHashtagClickListener onHashtagClickListener;
    private OnDogClickListener onDogClickListener;
    private OnTopicOwnerClickListener onTopicOwnerClickListener;
    private OnOwnerClickListener onOwnerClickListener;

    @SuppressLint("RestrictedApi") //fucking shit, why
    private static final Pattern urlPattern = AUTOLINK_WEB_URL;
    private static final Pattern hashTagPattern = Pattern.compile("#([^@#\\s]\\S+)");
    private static final Pattern dogPatter = Pattern.compile("@(all|online|[^@#\\s]+)");
    private static final Pattern ownerPattern = Pattern.compile("\\[((id|club)(\\d+)|(\\S+))\\|([^\\[\\]]+)]");
    private static final Pattern topicCommentPattern = Pattern.compile("\\[((id|club)(\\d+)|(\\S+)):bp(-\\d+)_(\\d+)\\|([^\\[\\]]+)]");

    private final int highlightColor;

    public VKTextView(@NonNull Context context) {
        this(context, null);
    }

    public VKTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public VKTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Resources.Theme theme = context.getTheme();

        final TypedArray a = theme.obtainStyledAttributes(attrs, R.styleable.VKTextView, defStyleAttr, R.style.VKTextView);

        highlightColor = a.getColor(R.styleable.VKTextView_highlightColor, Color.BLUE);

        a.recycle();

    }

    @Override
    public void setText(CharSequence originalText, BufferType type) {
        if (originalText != null && originalText.length() > 0) {

            super.setText(findAndReplaceTextLinks(originalText), type);

        } else {
            super.setText(originalText, type);
        }
    }


    private Spannable findAndReplaceTextLinks(CharSequence input){

        SpannableStringBuilder builder = SpannableStringBuilder.valueOf(input);

        List<TextLink> links  = new ArrayList<>();

        Matcher matcher = topicCommentPattern.matcher(builder);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            String spanText = matcher.group(7);
            if(spanText!=null){
                String url = matcher.group(4);
                if(url!=null){
                    Matcher urlMatcher = urlPattern.matcher(url);
                    if(urlMatcher.find()){
                        url = urlMatcher.group();
                        links.add(new UrlTextLink(url,spanText,start,end));
                    }
                } else {
                    String type = matcher.group(2);
                    if(type!=null){
                        String  id = matcher.group(3);
                        if (type.equals("club")){
                            id='-'+id;
                        }
                        String groupOwnerId = matcher.group(5);
                        if(groupOwnerId!=null){
                            String topicId = matcher.group(6);
                            if(topicId!=null){
                                links.add(new TopicOwnerTextLink(groupOwnerId,topicId,id,spanText,start,end));
                            } else {
                                links.add(new OwnerTextLink(id,spanText,start,end));
                            }
                        } else {
                            links.add(new OwnerTextLink(id,spanText,start,end));
                        }
                    }
                }
            }
        }
        builder = replaceAndBind(builder, links);

        links.clear();
        matcher = ownerPattern.matcher(builder);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            String spanText = matcher.group(5);
            if(spanText!=null) {
                String url = matcher.group(4);
                if (url != null) {
                    Matcher urlMatcher = urlPattern.matcher(url);
                    if(urlMatcher.find()){
                        url = urlMatcher.group();
                        links.add(new UrlTextLink(url,spanText,start,end));
                    }
                } else {
                    String type = matcher.group(2);
                    if (type != null) {
                        String id = matcher.group(3);
                        if (type.equals("club")) {
                            id='-'+id;
                        }
                        links.add(new OwnerTextLink(id,spanText,start,end));
                    }
                }
            }
        }
        builder = replaceAndBind(builder, links);

        links.clear();
        matcher = urlPattern.matcher(builder);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            String spanText = matcher.group();
            String url = matcher.group(1);
            if(url!=null){
                links.add(new UrlTextLink(url,spanText,start,end));
            }
        }
        builder = replaceAndBind(builder, links);

        links.clear();
        matcher = hashTagPattern.matcher(builder);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            String spanText = matcher.group();
            String hashtag = matcher.group(1);
            if(hashtag!=null){
                links.add(new HashTagTextLink(hashtag,spanText,start,end));
            }
        }
        builder = replaceAndBind(builder, links);

        links.clear();
        matcher = dogPatter.matcher(builder);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            String spanText = matcher.group();
            String text = matcher.group(1);
            if(text!=null){
                links.add(new DogTextLink(text,spanText,start,end));
            }
        }
        builder = replaceAndBind(builder, links);

        return builder;
    }

    private SpannableStringBuilder replaceAndBind(SpannableStringBuilder input, List<? extends TextLink> links) {
        if (links == null || links.isEmpty()) {
            return input;
        }
        for (int y = 0; y < links.size(); y++) {
            TextLink link = links.get(y);
            int origLength = link.end - link.start;
            int newLength = link.spanText.length();
            int count = origLength - newLength;
            for (int x = y+1; x < links.size(); x++){
                TextLink nextLink = links.get(x);
                nextLink.start-=count;
                nextLink.end-=count;
            }
            input.replace(link.start, link.end, link.spanText);
            link.end-=count;
            bind(input, link);
        }
        return input;
    }

    private void bind(SpannableStringBuilder input, TextLink link){

        CharacterStyle span = null;

        if(link instanceof UrlTextLink) {
            final UrlTextLink urlTextLink = (UrlTextLink)link;
            ClickableForegroundColorSpan.OnClickListener onClickListener = s -> {
                if(onUrlClickListener!=null){
                    onUrlClickListener.onUrlClick(urlTextLink.url);
                }
            };
            span = new ClickableForegroundColorSpan(highlightColor, true, onClickListener);
        } else if(link instanceof HashTagTextLink) {
            final HashTagTextLink hashTagTextLink = (HashTagTextLink)link;
            ClickableForegroundColorSpan.OnClickListener onClickListener = s -> {
                if(onHashtagClickListener!=null){
                    onHashtagClickListener.onHashtagClick(hashTagTextLink.hasTag);
                }
            };
            span = new ClickableForegroundColorSpan(highlightColor, true, onClickListener);
        } else if(link instanceof DogTextLink) {
            final DogTextLink dogTextLink = (DogTextLink)link;
            ClickableForegroundColorSpan.OnClickListener onClickListener = s -> {
                if(onDogClickListener!=null){
                    onDogClickListener.onDogClick(dogTextLink.text);
                }
            };
            span = new ClickableForegroundColorSpan(highlightColor, false, onClickListener);
        } else if(link instanceof TopicOwnerTextLink) {
            final TopicOwnerTextLink topicOwnerTextLink = (TopicOwnerTextLink)link;
            ClickableForegroundColorSpan.OnClickListener onClickListener = s -> {
                if(onTopicOwnerClickListener!=null){
                    onTopicOwnerClickListener.onTopicOwnerClick(topicOwnerTextLink.ownerId,
                            topicOwnerTextLink.groupOwnerId, topicOwnerTextLink.topicId);
                }
            };
            span = new ClickableForegroundColorSpan(highlightColor, false, onClickListener);
        } else if(link instanceof OwnerTextLink) {
            final OwnerTextLink ownerTextLink = (OwnerTextLink)link;
            ClickableForegroundColorSpan.OnClickListener onClickListener = s -> {
                if(onOwnerClickListener!=null){
                    onOwnerClickListener.onOwnerClick(ownerTextLink.ownerId);
                }
            };
            span = new ClickableForegroundColorSpan(highlightColor, false, onClickListener);
        }
        if(span!=null){
            input.setSpan(span, link.start, link.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

    }

    //---------------------------------------------------------------------//

    public void setOnUrlClickListener(OnUrlClickListener onUrlClickListener) {
        this.onUrlClickListener = onUrlClickListener;
    }

    public void setOnHashtagClickListener(OnHashtagClickListener onHashtagClickListener) {
        this.onHashtagClickListener = onHashtagClickListener;
    }

    public void setOnDogClickListener(OnDogClickListener onDogClickListener) {
        this.onDogClickListener = onDogClickListener;
    }

    public void setOnTopicOwnerClickListener(OnTopicOwnerClickListener onTopicOwnerClickListener) {
        this.onTopicOwnerClickListener = onTopicOwnerClickListener;
    }

    public void setOnOwnerClickListener(OnOwnerClickListener onOwnerClickListener) {
        this.onOwnerClickListener = onOwnerClickListener;
    }

    public interface OnUrlClickListener{
        void onUrlClick(String url);
    }

    public interface OnHashtagClickListener{
        void onHashtagClick(String hashtag);
    }

    public interface OnDogClickListener{
        void onDogClick(String dogText);
    }

    public interface OnTopicOwnerClickListener{
        void onTopicOwnerClick(String ownerId, String groupOwnerId, String topicId);
    }

    public interface OnOwnerClickListener{
        void onOwnerClick(String ownerId);
    }

    //---------------------------------------------------------------------//

    private static class TextLink {
        final String spanText;
        int start;
        int end;
        private TextLink(String spanText, int start, int end) {
            this.spanText = spanText;
            this.start = start;
            this.end = end;
        }
    }

    private static class OwnerTextLink extends TextLink {
        final String ownerId;
        private OwnerTextLink(String ownerId, String spanText, int start, int end) {
            super(spanText, start, end);
            this.ownerId = ownerId;
        }
    }

    private static class HashTagTextLink extends TextLink {
        final String hasTag;
        private HashTagTextLink(String hasTag, String spanText, int start, int end) {
            super(spanText, start, end);
            this.hasTag = hasTag;
        }
    }

    private static class UrlTextLink extends TextLink {
        final String url;
        private UrlTextLink(String url, String spanText, int start, int end) {
            super(spanText, start, end);
            this.url = url;
        }
    }

    private static class DogTextLink extends TextLink {
        final String text;
        private DogTextLink(String text, String spanText, int start, int end) {
            super(spanText, start, end);
            this.text = text;
        }
    }

    private static class TopicOwnerTextLink extends OwnerTextLink {
        final String groupOwnerId;
        final String topicId;
        private TopicOwnerTextLink(String groupOwnerId, String topicId, String ownerId, String spanText, int start, int end) {
            super(ownerId, spanText, start, end);
            this.groupOwnerId = groupOwnerId;
            this.topicId = topicId;
        }
    }

    //---------------------------------------------------------------------//

    private static class ClickableForegroundColorSpan extends ClickableSpan {

        private final int color;
        private final boolean underlineText;
        private final OnClickListener onClickListener;

        public ClickableForegroundColorSpan(@ColorInt int color, boolean underlineText, OnClickListener onClickListener) {
            this.color = color;
            this.underlineText = underlineText;
            this.onClickListener = onClickListener;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(color);
            ds.setUnderlineText(underlineText);
        }

        @Override
        public void onClick(View widget) {
            if (onClickListener != null) {
                onClickListener.onClick(this);
            }
        }

        public interface OnClickListener {
            void onClick(ClickableSpan clickableSpan);
        }
    }

}
