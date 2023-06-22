package com.vkontakte.miracle.viewholder.photos;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.model.general.Size;
import com.vkontakte.miracle.model.photos.Photo;

public class PhotoViewHolder extends RecyclerView.ViewHolder {

    private OnPhotoActionsListener onPhotoActionsListener;

    //------------------------------------------//

    private Photo photo;

    //------------------------------------------//

    private final ImageView imageView;

    public PhotoViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = (ImageView) itemView;

        itemView.setOnClickListener(v -> {
            if(photo!=null&& onPhotoActionsListener !=null){
                onPhotoActionsListener.onPhotoClick(photo, imageView);
            }
        });
    }

    public void bind(Photo photo){
        this.photo = photo;
        Picasso.get().cancelRequest(imageView);
        itemView.post(() -> {
            int width = itemView.getWidth();
            Size size = Size.getSizeForWidth(width, photo.getSizes(),false);
            if(size==null||size.getUrl().isEmpty()){
                imageView.setImageBitmap(null);
            } else {
                Picasso.get().load(size.getUrl()).into(imageView);
            }
        });
    }

    //------------------------------------------//

    public void setOnPhotoActionsListener(OnPhotoActionsListener onPhotoActionsListener) {
        this.onPhotoActionsListener = onPhotoActionsListener;
    }

    public interface OnPhotoActionsListener{
        void onPhotoClick(Photo photo, View view);
    }
}
