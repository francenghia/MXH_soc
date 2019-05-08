package com.example.franc.mxh.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.franc.mxh.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsViewHolder extends RecyclerView.ViewHolder {
    public CircleImageView imgCircleComment;
    public TextView comment_name,comment_text,comment_time;
    public CommentsViewHolder(View itemView) {
        super(itemView);
        comment_name=itemView.findViewById(R.id.comment_name);
        comment_text=itemView.findViewById(R.id.comment_text);
        comment_time=itemView.findViewById(R.id.comment_time);
        imgCircleComment =itemView.findViewById(R.id.circleComment);
    }
}
