package com.vkontakte.miracle.viewholder.general;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.model.general.Error;

public class ErrorViewHolder extends RecyclerView.ViewHolder {

    private final TextView errorMessageTextView;

    public ErrorViewHolder(@NonNull View itemView) {
        super(itemView);
        errorMessageTextView = itemView.findViewById(R.id.errorMessage);
    }

    public void bind(Error error){
        errorMessageTextView.setText(error.getErrorMessage());
    }

}
