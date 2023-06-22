package com.vkontakte.miracle.viewholder.auth;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.memory.storage.UsersStorage;
import com.vkontakte.miracle.model.auth.User;

public class AccountViewHolder extends RecyclerView.ViewHolder {

    private OnAccountActionsListener onAccountActionsListener;

    private User user;

    private final TextView name;
    private final ImageView imageView;

    public AccountViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.title);
        imageView = itemView.findViewById(R.id.photo);

        itemView.setOnClickListener(v -> {
            if(user!=null&&onAccountActionsListener!=null){
                onAccountActionsListener.onClick(user);
            }
        });

        itemView.setOnLongClickListener(v -> {
            if(user!=null&&onAccountActionsListener!=null){
                onAccountActionsListener.onLongClick(user);
            }
            return true;
        });
    }

    public void bind(User user){
        this.user = user;
        name.setText(user.getFirstName());

        UsersStorage usersStorage = UsersStorage.get();
        Picasso.get().cancelRequest(imageView);
        Bitmap photo = usersStorage.loadBitmapForUser("userImage200.png", user.getId());
        if(photo==null){
            Picasso.get().load(user.getPhoto200()).into(imageView);
        } else {
            imageView.setImageBitmap(photo);
        }
    }

    public void setOnAccountActionsListener(OnAccountActionsListener onAccountActionsListener) {
        this.onAccountActionsListener = onAccountActionsListener;
    }

    public interface OnAccountActionsListener{
        void onClick(User user);
        void onLongClick(User user);
    }
}
