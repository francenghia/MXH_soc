package com.example.franc.mxh.ViewActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.franc.mxh.Adapter.MessageAdpater;
import com.example.franc.mxh.Model.Messages;
import com.example.franc.mxh.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MessageActivity extends AppCompatActivity {

    private Toolbar mToobar;
    private ImageButton btnImage, btnSendMessage;
    private EditText edtSendMessage;
    private RecyclerView recyclerSendMessage;
    private String messageReceiverId, messageReceiverName, messageSendId;
    private String saveCurrentDate,saveCurrentTime;
    private TextView txtNameChat;
    private CircleImageView circleImageUserChat;
    private FirebaseAuth mAuth;
    private List<Messages> messagesList =new ArrayList<>();
    private MessageAdpater messageAdpater;

    private DatabaseReference BarRef;

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
        setContentView(R.layout.activity_message);
        messageReceiverId = getIntent().getExtras().get("viewPersonalPage").toString();
        messageReceiverName = getIntent().getExtras().get("name").toString();


        BarRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        messageSendId = mAuth.getCurrentUser().getUid();
        mToobar = findViewById(R.id.toolbarMessage);
        setSupportActionBar(mToobar);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.chat_bar_layout, null);
        actionBar.setCustomView(view);


        btnImage = findViewById(R.id.btnImage);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        edtSendMessage = findViewById(R.id.edtSendMessage);



        txtNameChat = findViewById(R.id.txtNameChat);
        circleImageUserChat = findViewById(R.id.circleImageUserChat);
        txtNameChat.setText(messageReceiverName);

        BarRef.child("Users").child(messageReceiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final String image = dataSnapshot.child("image").getValue().toString();
                    Picasso.with(getApplicationContext()).load(image).into(circleImageUserChat);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });

        messageAdpater = new MessageAdpater(messagesList);
        recyclerSendMessage = findViewById(R.id.recyclerSendMessage);
        recyclerSendMessage.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerSendMessage.setLayoutManager(linearLayoutManager);


        recyclerSendMessage.setAdapter(messageAdpater);


        FetchMessage();
    }

    private void FetchMessage() {
        BarRef.child("Message").child(messageSendId).child(messageReceiverId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if(dataSnapshot.exists()){
                            Messages messages = dataSnapshot.getValue(Messages.class);
                            messagesList.add(messages);
                            messageAdpater.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void SendMessage() {
        final String message = edtSendMessage.getText().toString();
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, "Please write a message here ...", Toast.LENGTH_SHORT).show();
        } else {
            String message_send = "Message/" + messageSendId + "/" + messageReceiverId;
            String message_receive = "Message/" + messageReceiverId + "/" + messageSendId;

            DatabaseReference user_message_send = BarRef.child("Message").child(messageSendId).child(messageReceiverId)
                    .push();
            String message_push_id = user_message_send.getKey();
            Log.d("Kiemtra",user_message_send.getKey());

            Calendar calendarDate = Calendar.getInstance();
            SimpleDateFormat simpleDateDate = new SimpleDateFormat("dd-MMMM-yyyy");
            saveCurrentDate = simpleDateDate.format(calendarDate.getTime());

            Calendar calendarTime = Calendar.getInstance();
            SimpleDateFormat simpleDateTime = new SimpleDateFormat("HH:mm");
            saveCurrentTime = simpleDateTime.format(calendarTime.getTime());

            Map messageBody = new HashMap();
            messageBody.put("message",message);
            messageBody.put("date",saveCurrentDate);
            messageBody.put("time",saveCurrentTime);
            messageBody.put("type","text");
            messageBody.put("from",messageSendId);

            Map messageDetails = new HashMap();
            messageDetails.put(message_send+"/"+message_push_id,messageBody);
            messageDetails.put(message_receive+"/"+message_push_id,messageBody);

            BarRef.updateChildren(messageDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MessageActivity.this, "Message sent successfully", Toast.LENGTH_SHORT).show();
                            edtSendMessage.setText("");
                        }else {
                            Toast.makeText(MessageActivity.this, "Error !"+task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                }
            });

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                UserPostToActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserPostToActivity() {
        Intent intent = new Intent(MessageActivity.this, FriendsActivity.class);
        startActivity(intent);
        finish();
    }
}
