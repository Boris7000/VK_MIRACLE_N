package com.vkontakte.miracle.viewholder.photos;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.recyclerview.viewholder.bundle.ViewHolderBundle;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.model.photos.Photo;
import com.vkontakte.miracle.viewholder.photos.PhotoViewHolder;

public abstract class PhotoViewHolderBundle extends ViewHolderBundle<RecyclerView.ViewHolder,Object>
                                            implements PhotoViewHolder.OnPhotoActionsListener{

    @Override
    public void bind(RecyclerView.ViewHolder viewHolder, Object data) {
        if(viewHolder instanceof PhotoViewHolder && data instanceof Photo){
            PhotoViewHolder photoViewHolder = (PhotoViewHolder) viewHolder;
            Photo photo = (Photo) data;
            photoViewHolder.bind(photo);
            photoViewHolder.setOnPhotoActionsListener(this);
            onBind(photoViewHolder, photo);
        }
    }

    //-------------------------------------------------------------------------//

    public void onBind(PhotoViewHolder photoViewHolder, Photo photo){}

    //-------------------------------------------------------------------------//

    @Override
    public void onPhotoClick(Photo photo, View view) {}

    @Override
    public int getLayoutResourceId() {
        return R.layout.wall_ai_photo;
    }

    @Override
    public RecyclerView.ViewHolder create(View view) {
        return new PhotoViewHolder(view);
    }

}
