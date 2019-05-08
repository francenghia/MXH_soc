package com.example.franc.mxh.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.franc.mxh.FindFriendsByMap.FindFriendOnMapActivity;
import com.example.franc.mxh.Model.FriendList;
import com.example.franc.mxh.Model.User;
import com.example.franc.mxh.R;
import com.example.franc.mxh.ViewHolder.DefaultViewHolder;
import com.example.franc.mxh.ViewHolder.ListFriendViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class FindFriendByMapFragment extends Fragment {
    public FindFriendByMapFragment() {
        // Required empty public constructor
    }

    private DatabaseReference friendListOnl, userIsFriend, counterRef, currentUserRef, onlineRef;
    private RecyclerView recyclerShareLocation;
    private FirebaseRecyclerAdapter<User, ListFriendViewHolder> adapter;
    private MaterialAnimatedSwitch animatedSwitch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_find_friend_by_map, container, false);
        friendListOnl = FirebaseDatabase.getInstance().getReference("Friends")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userIsFriend = FirebaseDatabase.getInstance().getReference("User");

        animatedSwitch = v.findViewById(R.id.location_switch);

        currentUserRef = FirebaseDatabase.getInstance().getReference("Online")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        counterRef = FirebaseDatabase.getInstance().getReference("Online");

        onlineRef = FirebaseDatabase.getInstance().getReference().child(".info/connected");

        recyclerShareLocation = v.findViewById(R.id.recyclerUserShareLocation);
        recyclerShareLocation.setLayoutManager(new LinearLayoutManager(getContext()));

        setupSystem();
        loadFriendShareLocation();
        return v;
    }

    private void setupSystem() {
        onlineRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Boolean.class)) {
                    currentUserRef.onDisconnect().removeValue();
                    counterRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(new User(FirebaseAuth.getInstance().getCurrentUser().getEmail(), "Online"));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        counterRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    User user = postSnapShot.getValue(User.class);
                    Log.d("Kiem tra status", user.getEmail() + "/" + user.getStatus());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        animatedSwitch.setOnCheckedChangeListener(new MaterialAnimatedSwitch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean isShare) {
                if (isShare) {
                    currentUserRef.removeValue();
                } else {
                    counterRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(new User(FirebaseAuth.getInstance().getCurrentUser().getEmail(), "Online"));
                }
            }
        });
    }

    private void loadFriendShareLocation() {
        adapter = new FirebaseRecyclerAdapter<User, ListFriendViewHolder>(
                User.class,
                R.layout.dislayout_friendlist,
                ListFriendViewHolder.class,
                counterRef
        ) {
            @Override
            protected void populateViewHolder(ListFriendViewHolder viewHolder, User model, int position) {
                viewHolder.name_friends.setText(model.getEmail());

            }
        };
        adapter.notifyDataSetChanged();
        recyclerShareLocation.setAdapter(adapter);
    }
}
