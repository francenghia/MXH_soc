package com.example.franc.mxh.ViewHolder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.franc.mxh.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListFriendViewHolder extends RecyclerView.ViewHolder{
    public ImageView status_online_green;
    public TextView dateFriend, name_friends;
    public  CircleImageView circleFriends;
    public View mView;
    public ListFriendViewHolder(View itemView) {
        super(itemView);
        mView=itemView;
        dateFriend = mView.findViewById(R.id.txtDateTime);
        name_friends = mView.findViewById(R.id.name_friends);
        circleFriends =  mView.findViewById(R.id.circleFriends);
        status_online_green = mView.findViewById(R.id.status_online_green);


    }
}
