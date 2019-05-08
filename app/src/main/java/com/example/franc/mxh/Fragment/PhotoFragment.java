package com.example.franc.mxh.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.franc.mxh.Model.Photo;
import com.example.franc.mxh.R;
import com.example.franc.mxh.ViewActivity.CardSliderPhotoActivity;
import com.example.franc.mxh.ViewHolder.PhotoViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoFragment extends Fragment {

    private RecyclerView recyclerPhoto;

    private FirebaseDatabase database;
    private DatabaseReference photo,friends;
    private FirebaseAuth mAuth;

    private String current_user_id;

    private TextView txtPhotos,txtFriends;

    private int countPhotos = 0;
    private int countFriends = 0;

    public PhotoFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        mAuth = FirebaseAuth.getInstance();

        txtPhotos = view.findViewById(R.id.txtPhotos);
        txtFriends =view.findViewById(R.id.txtFriends);
        current_user_id = mAuth.getCurrentUser().getUid().toString();
        database = FirebaseDatabase.getInstance();

        photo = database.getReference("Photos").child(current_user_id);
        friends = database.getReference("Friends").child(current_user_id);

        recyclerPhoto = view.findViewById(R.id.recyclerPhoto);
        LinearLayoutManager layoutManager = new GridLayoutManager(getContext(),3);
        recyclerPhoto.setLayoutManager(layoutManager);

        countChildPhotos();
        countChildFriends();
        loadPhoto();



        return view;
    }

    private void countChildFriends() {
        friends.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                countFriends  = (int) dataSnapshot.getChildrenCount();
                txtFriends.setText(String.valueOf(countFriends));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void countChildPhotos() {
        photo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                countPhotos = (int) dataSnapshot.getChildrenCount();
                txtPhotos.setText(String.valueOf(countPhotos));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void loadPhoto() {

        FirebaseRecyclerAdapter<Photo, PhotoViewHolder> adapter = new FirebaseRecyclerAdapter<Photo, PhotoViewHolder>(
                Photo.class,
                R.layout.display_photo,
                PhotoViewHolder.class,
                photo
        ) {
            @Override
            protected void populateViewHolder(final PhotoViewHolder viewHolder, Photo model, int position) {
                viewHolder.txtTimePhoto.setText(model.getTime()+"\n"+model.getDate());
                Picasso.with(getContext()).load(model.getPhoto()).into(viewHolder.photo);
            }
        };

        recyclerPhoto.setAdapter(adapter);

    }

}
