package com.example.franc.mxh.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.franc.mxh.R;

public class PhotoViewHolder extends RecyclerView.ViewHolder {
    public ImageView photo;
    public TextView txtTimePhoto;

    public PhotoViewHolder(View itemView) {
        super(itemView);

        photo =itemView.findViewById(R.id.photo);
        txtTimePhoto = itemView.findViewById(R.id.txtTimePhoto);
    }
}
