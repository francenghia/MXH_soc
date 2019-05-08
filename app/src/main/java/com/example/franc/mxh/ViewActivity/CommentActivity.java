package com.example.franc.mxh.ViewActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.franc.mxh.Model.Comments;
import com.example.franc.mxh.R;
import com.example.franc.mxh.ViewHolder.CommentsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CommentActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ImageButton imgBtnSentComment;
    private EditText edtComment;
    private DatabaseReference UsersRef,PostsRef;
    private String currentUserId,postKey;
    String saveCurrentDate;
    String saveCurrentTime;
    String postRandomName;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setFontAttrId(R.attr.fontPath)
                .setDefaultFontPath("fonts/Merienda-Regular.ttf")
                .build());
        setContentView(R.layout.activity_comment);
        recyclerView=findViewById(R.id.recyclerComment);
        imgBtnSentComment=findViewById(R.id.imgBtnSendComment);
        edtComment=findViewById(R.id.edtComment);

        postKey =getIntent().getExtras().get("postKey").toString();
        //Firebase
        currentUserId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(postKey).child("Comments");

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        imgBtnSentComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline()) {
                    UsersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                {
                                    String userName = dataSnapshot.child("name").getValue().toString();
                                    String imageProfile = dataSnapshot.child("image").getValue().toString();
                                    DisplayComment(userName, imageProfile);
                                    edtComment.setText("");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    Toast.makeText(CommentActivity.this, "Disconnected. Please check internet again !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void DisplayComment(String userName,String imageProfile) {
        String comment = edtComment.getText().toString();
        if (TextUtils.isEmpty(comment)) {
            Toast.makeText(this, "Please write your comment ...", Toast.LENGTH_SHORT).show();
        } else {
            Calendar calendarDate = Calendar.getInstance();
            SimpleDateFormat simpleDateDate = new SimpleDateFormat("dd-MMMM-yyyy");
            saveCurrentDate = simpleDateDate.format(calendarDate.getTime());

            Calendar calendarTime = Calendar.getInstance();
            SimpleDateFormat simpleDateTime = new SimpleDateFormat("HH:mm");
            saveCurrentTime = simpleDateTime.format(calendarTime.getTime());

            postRandomName = currentUserId + saveCurrentDate + saveCurrentTime;


            HashMap commentHashMap = new HashMap();
            commentHashMap.put("uid", currentUserId);
            commentHashMap.put("comment", comment);
            commentHashMap.put("date", saveCurrentDate);
            commentHashMap.put("time", saveCurrentTime);
            commentHashMap.put("name", userName);
            commentHashMap.put("image", imageProfile);

            PostsRef.child(postRandomName).updateChildren(commentHashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(CommentActivity.this, "Comment post successfully !", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CommentActivity.this, "Error , please try again !", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Comments,CommentsViewHolder> adapter = new FirebaseRecyclerAdapter<Comments, CommentsViewHolder>(
                Comments.class,
                R.layout.display_comment_layout,
                CommentsViewHolder.class,
                PostsRef
        ) {
            @Override
            protected void populateViewHolder(CommentsViewHolder viewHolder, Comments model, int position) {
                viewHolder.comment_name.setText(model.getName());
                viewHolder.comment_text.setText(model.getComment());
                viewHolder.comment_time.setText(model.getTime()+" / "+model.getDate());

                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imgCircleComment);
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        if(netInfo!=null && netInfo.isConnectedOrConnecting()){
            return true;
        }else {
            return false;
        }
    }
}
