package com.example.franc.mxh.ViewHolder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.franc.mxh.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class DefaultViewHolder extends RecyclerView.ViewHolder {
    public View mView;


    public DefaultViewHolder(View itemView) {
        super(itemView);
        mView = itemView;



    }
    public void setDetails(Context ctx, String userName, String userStatus, String userImage){

        TextView user_name = (TextView) mView.findViewById(R.id.name_text);
        TextView user_status = (TextView) mView.findViewById(R.id.status_text);
        CircleImageView user_image =  mView.findViewById(R.id.profile_image);


        user_name.setText(userName);
        user_status.setText(userStatus);

        Picasso.with(ctx).load(userImage).placeholder(R.drawable.image).into(user_image);


    }


}
