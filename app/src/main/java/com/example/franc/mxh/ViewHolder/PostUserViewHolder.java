package com.example.franc.mxh.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.franc.mxh.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;


public class PostUserViewHolder extends RecyclerView.ViewHolder {
    public ImageView postImage;
    public CircleImageView postProfileImage;
    public TextView displayeLike;
    public ImageButton imgBtnLike,imgBtnComment,imgBtnShare;
    public TextView description,nameUser,time;

    public String currentUserId ="";
    DatabaseReference LikeRef;
    int countLikes;

    public PostUserViewHolder(View itemView) {
        super(itemView);
        postImage =itemView.findViewById(R.id.postImage);
        postProfileImage =itemView.findViewById(R.id.postProfileImage);
        description =itemView.findViewById(R.id.txtDescription);
        nameUser =itemView.findViewById(R.id.txtNameUserPost);
        time =itemView.findViewById(R.id.txtTime);
        imgBtnLike=itemView.findViewById(R.id.imgBtnLike);
        imgBtnComment=itemView.findViewById(R.id.imgBtnComment);
        displayeLike=itemView.findViewById(R.id.display_Like);

        LikeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    }

    public void setStatusLike(final String postKey) {
        LikeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postKey).hasChild(currentUserId)) {
                    countLikes = (int) dataSnapshot.child(postKey).getChildrenCount();
                    imgBtnLike.setImageResource(R.drawable.like);
                    displayeLike.setText(Integer.toString(countLikes) + (" likes"));
                } else {
                    countLikes = (int) dataSnapshot.child(postKey).getChildrenCount();
                    imgBtnLike.setImageResource(R.drawable.ic_thumb_up_white_24dp);
                    displayeLike.setText(Integer.toString(countLikes) + (" likes"));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
