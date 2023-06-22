package com.vkontakte.miracle.viewholder.wall;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.recyclerview.viewholder.bundle.ViewHolderBundle;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.model.general.ExtendedArrays;
import com.vkontakte.miracle.model.photos.Photo;
import com.vkontakte.miracle.model.wall.Post;
import com.vkontakte.miracle.util.constants.TypedDataConstants;
import com.vkontakte.miracle.viewholder.wall.PostViewHolder;

import java.util.List;

public abstract class PostViewHolderBundle extends ViewHolderBundle<RecyclerView.ViewHolder,Object>
                                           implements PostViewHolder.OnPostActionsListener{

    private final RecyclerView.RecycledViewPool photosRecycledViewPool = new RecyclerView.RecycledViewPool();
    {
        photosRecycledViewPool.setMaxRecycledViews(TypedDataConstants.TYPE_PHOTO,15);
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.wall_ai_post;
    }

    @Override
    public RecyclerView.ViewHolder create(View view) {
        return new PostViewHolder(view, photosRecycledViewPool);
    }

    @Override
    public void bind(RecyclerView.ViewHolder viewHolder, Object data) {
        if(viewHolder instanceof PostViewHolder && data instanceof Post){
            PostViewHolder postViewHolder = (PostViewHolder) viewHolder;
            Post post = (Post) data;
            postViewHolder.bind(post, requestExtendedArrays());
            postViewHolder.setOnPostActionsListener(this);
        }
    }

    //-------------------------------------------------------------------------//

    public void onBind(PostViewHolder postViewHolder, Post post){}

    //-------------------------------------------------------------------------//

    @Override
    public void onPostPhotoClick(Photo photo, List<Photo> photos, View view) {

    }

    @Override
    public void onOwnerLinkClick(String ownerId) {

    }

    @Override
    public void onUrlLinkClick(String url) {

    }

    @Override
    public void onHashTagLinkClick(String hashTag) {

    }

    //-------------------------------------------------------------------------//

    public abstract ExtendedArrays requestExtendedArrays();
}
