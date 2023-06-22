package com.vkontakte.miracle.viewholder.auth;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.recyclerview.viewholder.bundle.ViewHolderBundle;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.model.auth.User;
import com.vkontakte.miracle.viewholder.auth.AccountViewHolder;

public abstract class AccountViewHolderBundle extends ViewHolderBundle<RecyclerView.ViewHolder,Object>
                                              implements AccountViewHolder.OnAccountActionsListener{
    @Override
    public int getLayoutResourceId() {
        return R.layout.auth_ai_user;
    }

    @Override
    public RecyclerView.ViewHolder create(View view) {
        return new AccountViewHolder(view);
    }

    @Override
    public void bind(RecyclerView.ViewHolder viewHolder, Object data) {
        if(viewHolder instanceof AccountViewHolder && data instanceof User){
            AccountViewHolder accountViewHolder = (AccountViewHolder) viewHolder;
            User user = (User) data;
            accountViewHolder.bind(user);
            accountViewHolder.setOnAccountActionsListener(this);
        }
    }
    @Override
    public void onClick(User user) {}
    @Override
    public void onLongClick(User user) {}
}
