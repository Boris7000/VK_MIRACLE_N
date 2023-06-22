package com.vkontakte.miracle.viewholder.general;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.recyclerview.viewholder.bundle.ViewHolderBundle;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.model.general.Error;
import com.vkontakte.miracle.viewholder.general.ErrorViewHolder;

public abstract class ErrorViewHolderBundle extends ViewHolderBundle<RecyclerView.ViewHolder,Object>{

    @Override
    public void bind(RecyclerView.ViewHolder viewHolder, Object data) {
        if(viewHolder instanceof ErrorViewHolder && data instanceof Error){
            ErrorViewHolder errorViewHolder = (ErrorViewHolder) viewHolder;
            Error error = (Error) data;
            errorViewHolder.bind(error);
        }
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.general_ai_error;
    }

    @Override
    public RecyclerView.ViewHolder create(View view) {
        return new ErrorViewHolder(view);
    }

}
