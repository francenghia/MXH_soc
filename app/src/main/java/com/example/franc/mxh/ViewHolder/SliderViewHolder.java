package com.example.franc.mxh.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.franc.mxh.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class SliderViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageYourPhoto;
    public CircleImageView imageYourPosts;
    public TextView txtStatus,txtName,txtDate;
    public SliderViewHolder(View itemView) {
        super(itemView);
        imageYourPhoto = itemView.findViewById(R.id.imageYourPhoto);
        imageYourPosts = itemView.findViewById(R.id.imageYourPosts);
        txtStatus = itemView.findViewById(R.id.status_text);
        txtName=itemView.findViewById(R.id.name);
        txtDate= itemView.findViewById(R.id.date);


    }
}
