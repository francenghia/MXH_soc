package com.example.franc.mxh.ViewActivity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.franc.mxh.Model.FriendList;
import com.example.franc.mxh.R;
import com.example.franc.mxh.ViewHolder.ListFriendViewHolder;
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

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FriendsActivity extends AppCompatActivity {
    private Toolbar mToobar;
    private RecyclerView recyclerFriends;
    private DatabaseReference FriendList,UserIsFriend;
    private FirebaseAuth auth;
    private String currentUserId;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setFontAttrId(R.attr.fontPath)
                .setDefaultFontPath("fonts/Merienda-Regular.ttf").build());
        setContentView(R.layout.activity_friends);

        mToobar =findViewById(R.id.toolbarFriend);
        setSupportActionBar(mToobar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("List Friends");

        auth = FirebaseAuth.getInstance();
        currentUserId =auth.getCurrentUser().getUid();
        FriendList = FirebaseDatabase.getInstance().getReference().child("Friends").child(currentUserId);
        UserIsFriend = FirebaseDatabase.getInstance().getReference().child("Users");



        recyclerFriends =findViewById(R.id.recyclerFriends);
        recyclerFriends.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerFriends.setLayoutManager(linearLayoutManager);

        FriendDisplay();




    }

    private void FriendDisplay() {
        FirebaseRecyclerAdapter<FriendList,ListFriendViewHolder> adapter = new FirebaseRecyclerAdapter<FriendList, ListFriendViewHolder>(
                FriendList.class,
                R.layout.dislayout_friendlist,
                ListFriendViewHolder.class,
                FriendList

        ) {
            @Override
            protected void populateViewHolder(final ListFriendViewHolder viewHolder, FriendList model, int position) {
                viewHolder.dateFriend.setText("Friend date : "+model.getDate());
                final String usersId = getRef(position).getKey();

                UserIsFriend.child(usersId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){

                            final String name = dataSnapshot.child("name").getValue().toString();
                            final String image = dataSnapshot.child("image").getValue().toString();
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


                            viewHolder.name_friends.setText(name);
                            Picasso.with(getApplicationContext()).load(image).into(viewHolder.circleFriends);

                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder builder =new AlertDialog.Builder(FriendsActivity.this);
                                    builder.setTitle("Send Request ");
                                    builder.setMessage("Please select your request !");
                                    View layout_dialog = LayoutInflater.from(FriendsActivity.this).inflate(R.layout.dialog_layout,null);
                                    final Button btnProfile =layout_dialog.findViewById(R.id.btnProfile);
                                    final Button btnMessage =layout_dialog.findViewById(R.id.btnMessage);

                                    btnProfile.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(FriendsActivity.this,ProfileActivity.class);
                                            intent.putExtra("viewPersonalPage",usersId);
                                            startActivity(intent);
                                        }
                                    });
                                    btnMessage.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(FriendsActivity.this,MessageActivity.class);
                                            intent.putExtra("viewPersonalPage",usersId);
                                            intent.putExtra("name",name);
                                            startActivity(intent);
                                        }
                                    });
                                    
                                    builder.setView(layout_dialog);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                UserPostToActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
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


    private void UserPostToActivity() {
        Intent intent = new Intent(FriendsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        statusUserOnlineOrOffline("online");
    }

    @Override
    protected void onStop() {
        super.onStop();
        statusUserOnlineOrOffline("offline");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        statusUserOnlineOrOffline("offline");
    }
}
