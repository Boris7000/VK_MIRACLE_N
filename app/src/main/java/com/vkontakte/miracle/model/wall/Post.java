package com.vkontakte.miracle.model.wall;

import androidx.annotation.Nullable;

import com.miracle.engine.annotation.MayBeEmpty;
import com.miracle.engine.recyclerview.TypedData;
import com.vkontakte.miracle.model.general.Attachments;
import com.vkontakte.miracle.model.general.Comments;
import com.vkontakte.miracle.model.general.Likes;
import com.vkontakte.miracle.model.general.Reposts;
import com.vkontakte.miracle.model.wall.fields.Views;
import com.vkontakte.miracle.util.constants.TypedDataConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class Post implements TypedData {

    private final String id;
    private final long date;

    private final String ownerId;
    private final String sourceId;
    private final String fromId;

    private final String text;
    private final boolean zoomText;

    private final Attachments attachments;

    private final Likes likes;
    private final Comments comments;
    private final Reposts reposts;
    private final Views views;


    public Post(JSONObject jsonObject) throws JSONException {

        id = jsonObject.optString("post_id", jsonObject.optString("id"));

        ownerId = jsonObject.optString("owner_id");

        sourceId = jsonObject.optString("source_id");

        fromId = jsonObject.optString("from_id");

        date = jsonObject.getLong("date");

        text = jsonObject.optString("text");

        zoomText = jsonObject.optBoolean("zoom_text");

        if(jsonObject.has("attachments")){
            attachments = new Attachments(jsonObject.getJSONArray("attachments"), true);
        } else {
            attachments = null;
        }

        likes = new Likes(jsonObject.getJSONObject("likes"));
        comments = new Comments(jsonObject.getJSONObject("comments"));
        reposts = new Reposts(jsonObject.getJSONObject("reposts"));

        if (jsonObject.has("views")){
            views = new Views(jsonObject.getJSONObject("views"));
        } else {
            views = null;
        }

    }

    public String getId() {
        return id;
    }

    public long getDate() {
        return date;
    }

    @MayBeEmpty
    public String getOwnerId() {
        return ownerId;
    }

    @MayBeEmpty
    public String getSourceId() {
        return sourceId;
    }

    @MayBeEmpty
    public String getFromId() {
        return fromId;
    }

    @MayBeEmpty
    public String getText() {
        return text;
    }

    public boolean zoomText() {
        return zoomText;
    }

    @Nullable
    public Attachments getAttachments() {
        return attachments;
    }

    public Likes getLikes() {
        return likes;
    }

    public Comments getComments() {
        return comments;
    }

    public Reposts getReposts() {
        return reposts;
    }

    @Nullable
    public Views getViews() {
        return views;
    }

    @Override
    public int getDataType() {
        return TypedDataConstants.TYPE_POST;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return id.equals(post.id) &&
                Objects.equals(ownerId, post.ownerId) &&
                Objects.equals(sourceId, post.sourceId) &&
                Objects.equals(fromId, post.fromId);
    }

    //TODO добавить нормальную проверку на эквивалентность контента
}
