package com.example.franc.mxh.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.example.franc.mxh.Model.FriendList;

import com.example.franc.mxh.R;
import com.example.franc.mxh.ViewHolder.ListFriendViewHolder;
import com.example.franc.mxh.ViewActivity.MessageActivity;
import com.example.franc.mxh.ViewActivity.ProfileActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {
    private RecyclerView recyclerFriends;
    private DatabaseReference FriendList,UserIsFriend;
    private FirebaseAuth auth;
    private String currentUserId;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_friends, container, false);
        auth = FirebaseAuth.getInstance();
        currentUserId =auth.getCurrentUser().getUid();
        FriendList = FirebaseDatabase.getInstance().getReference().child("Friends").child(currentUserId);
        UserIsFriend = FirebaseDatabase.getInstance().getReference().child("Users");

        recyclerFriends =v.findViewById(R.id.recyclerFriends);
//        recyclerFriends.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerFriends.setLayoutManager(linearLayoutManager);

        FriendDisplay();
        return v;
    }
    private void FriendDisplay() {
        FirebaseRecyclerAdapter<com.example.franc.mxh.Model.FriendList,ListFriendViewHolder> adapter = new FirebaseRecyclerAdapter<FriendList, ListFriendViewHolder>(
                FriendList.class,
                R.layout.dislayout_friendlist,
                ListFriendViewHolder.class,
                FriendList

        ) {
            @Override
            protected void populateViewHolder(final ListFriendViewHolder viewHolder, FriendList model, int position) {
                viewHolder.dateFriend.setText("Friend date : "+model.getDate());
                final String usersId=getRef(position).getKey();

                UserIsFriend.child(usersId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){

                            final String name = dataSnapshot.child("name").getValue().toString();
                            final String image = dataSnapshot.child("image").getValue().toString();

                            viewHolder.name_friends.setText(name);
                            Picasso.with(getContext()).load(image).into(viewHolder.circleFriends);
                            final String type;

                            if(dataSnapshot.hasChild("State")){
                                type = dataSnapshot.child("State").child("type").getValue().toString();

                                if(type.equals("online")){
                                    viewHolder.status_online_green.setVisibility(View.VISIBLE);
                                }
                                else {
                                    viewHolder.status_online_green.setVisibility(View.INVISIBLE);
                                }
                            }

                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder builder =new AlertDialog.Builder(getContext());
                                    builder.setTitle("Send Request ");
                                    builder.setMessage("Please select your request !");
                                    View layout_dialo = LayoutInflater.from(getContext()).inflate(R.layout.dialog_layout,null);
                                    final Button btnProfile =layout_dialo.findViewById(R.id.btnProfile);
                                    final Button btnMessage =layout_dialo.findViewById(R.id.btnMessage);

                                    btnProfile.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(getContext(),ProfileActivity.class);
                                            intent.putExtra("viewPersonalPage",usersId);
                                            startActivity(intent);
                                        }
                                    });
                                    btnMessage.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(getContext(),MessageActivity.class);
                                            intent.putExtra("viewPersonalPage",usersId);
                                            intent.putExtra("name",name);
                                            startActivity(intent);
                                        }
                                    });

                                    builder.setView(layout_dialo);

                                    builder.show();
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        };
        recyclerFriends.setAdapter(adapter);
    }
    private void statusUserOnlineOrOffline(String state){
        String saveCurrentDate,saveCurrentTime;

        Calendar calendarDate =Calendar.getInstance();
        SimpleDateFormat currentDate =new SimpleDateFormat("MMM dd,yyyy");
        saveCurrentDate = currentDate.format(calendarDate.getTime());

        Calendar calendarTime =Calendar.getInstance();
        SimpleDateFormat currentTime =new SimpleDateFormat("hh:mm a");
        saveCurrentTime= currentTime.format(calendarTime.getTime());

        Map currentStateMap = new HashMap();
        currentStateMap.put("time",saveCurrentTime);
        currentStateMap.put("date",saveCurrentDate);
        currentStateMap.put("type",state);

        UserIsFriend.child(currentUserId).child("State").updateChildren(currentStateMap);

    }

    @Override
    public void onStart() {
        super.onStart();
        statusUserOnlineOrOffline("online");
    }

    @Override
    public void onStop() {
        super.onStop();
        statusUserOnlineOrOffline("offline");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        statusUserOnlineOrOffline("offline");
    }
}
