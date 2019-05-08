package com.example.franc.mxh.ViewActivity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.franc.mxh.R;
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

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProfileActivity extends AppCompatActivity {
    private ImageView imageViewPhoto;

    private ImageView imageBackgroundCover;
    private TextView txtDescription;
    private TextView txtProfileName;
    private TextView txtProfileCountry;
    private TextView txtProfileDateOfBirth;
    private TextView txtProfilePhone;
    private TextView txtGender;
    private Button btnRequest, btnDecline;
    private FirebaseAuth mAuth;
    private String senderUserId, receiverUserId, CURRENT_STATE, saveCurrentDate;
    private DatabaseReference FriendRequestRefer, UserRef, FriendRef;

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
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        receiverUserId = getIntent().getExtras().get("viewPersonalPage").toString();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        senderUserId = mAuth.getCurrentUser().getUid();
        FriendRequestRefer = FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        FriendRef = FirebaseDatabase.getInstance().getReference().child("Friends");

        imageBackgroundCover = findViewById(R.id.cover);
        imageViewPhoto = findViewById(R.id.photo);
        txtDescription = findViewById(R.id.status_update);
        txtProfileName = findViewById(R.id.txtNameUser);
        txtProfileCountry = findViewById(R.id.txtCountry);
        txtProfileDateOfBirth = findViewById(R.id.txtDateOfBirth);
        txtProfilePhone = findViewById(R.id.txtPhone);
        txtGender = findViewById(R.id.txtGender);
        btnRequest = findViewById(R.id.btnRequest);
        btnDecline = findViewById(R.id.btnDecline);

        CURRENT_STATE = "not_friend";


        UserRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String phone = dataSnapshot.child("phone").getValue().toString();
                    String gender = dataSnapshot.child("gender").getValue().toString();
                    String dateofbirth = dataSnapshot.child("dateofbirth").getValue().toString();
                    String country = dataSnapshot.child("country").getValue().toString();
                    String image = dataSnapshot.child("image").getValue().toString();
                    String cover = dataSnapshot.child("cover").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();

                    Picasso.with(getApplicationContext()).load(image).into(imageViewPhoto);
                    Picasso.with(getApplicationContext()).load(cover).into(imageBackgroundCover);

                    txtProfileName.setText(name);
                    txtProfileCountry.setText(country);
                    txtProfileDateOfBirth.setText(dateofbirth);
                    txtProfilePhone.setText(phone);
                    txtGender.setText(gender);
                    txtDescription.setText(status);

                    ChangeButton();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnDecline.setVisibility(View.INVISIBLE);
        btnDecline.setEnabled(false);

        if (!senderUserId.equals(receiverUserId)) {
            btnRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnRequest.setEnabled(false);

                    if (CURRENT_STATE.equals("not_friend")) {
                        SentFriendRequest();
                    }
                    if (CURRENT_STATE.equals("request_sent")) {
                        CancleRequest();
                    }
                    if (CURRENT_STATE.equals("request_received")) {
                        AcceptFriendRequest();
                    }
                    if (CURRENT_STATE.equals("friends")) {
                        Unfriend();
                    }
                }
            });
        } else {
            btnDecline.setVisibility(View.INVISIBLE);
            btnRequest.setVisibility(View.INVISIBLE);
        }

    }

    private void Unfriend() {
        FriendRef.child(senderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FriendRef.child(receiverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                btnRequest.setEnabled(true);
                                                CURRENT_STATE = "not_friend";
                                                btnRequest.setText("Friend Request");

                                                btnDecline.setVisibility(View.INVISIBLE);
                                                btnDecline.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptFriendRequest() {
        Calendar calendarDate = Calendar.getInstance();
        SimpleDateFormat simpleDateDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = simpleDateDate.format(calendarDate.getTime());

        FriendRef.child(senderUserId).child(receiverUserId).child("date").setValue(saveCurrentDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FriendRef.child(receiverUserId).child(senderUserId).child("date").setValue(saveCurrentDate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                FriendRequestRefer.child(senderUserId).child(receiverUserId).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    FriendRequestRefer.child(receiverUserId).child(senderUserId).removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        btnRequest.setEnabled(true);
                                                                                        CURRENT_STATE = "friends";
                                                                                        btnRequest.setText("Unfriend");
                                                                                        btnDecline.setVisibility(View.INVISIBLE);
                                                                                        btnDecline.setEnabled(false);
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void CancleRequest() {
        FriendRequestRefer.child(senderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FriendRequestRefer.child(receiverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                btnRequest.setEnabled(true);
                                                CURRENT_STATE = "not_friend";
                                                btnRequest.setText("Friend Request");
                                                btnDecline.setVisibility(View.INVISIBLE);
                                                btnDecline.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void ChangeButton() {
        FriendRequestRefer.child(senderUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(receiverUserId)) {
                    String request_type = dataSnapshot.child(receiverUserId).child("request_type").getValue().toString();

                    if (request_type.equals("sent")) {
                        CURRENT_STATE = "request_sent";
                        btnRequest.setText("Cancle friend request");
                        btnDecline.setVisibility(View.INVISIBLE);
                        btnDecline.setEnabled(false);
                    } else if (request_type.equals("received")) {
                        CURRENT_STATE = "request_received";
                        btnRequest.setText("Accept friend request");

                        btnDecline.setVisibility(View.VISIBLE);
                        btnDecline.setEnabled(true);

                        btnDecline.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CancleRequest();
                            }
                        });
                    }
                } else {
                    FriendRef.child(senderUserId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(receiverUserId)) {
                                CURRENT_STATE = "friends";
                                btnRequest.setText("Unfriend");
                                btnDecline.setVisibility(View.INVISIBLE);
                                btnDecline.setEnabled(false);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void SentFriendRequest() {
        FriendRequestRefer.child(senderUserId).child(receiverUserId).child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FriendRequestRefer.child(receiverUserId).child(senderUserId).child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                btnRequest.setEnabled(true);
                                                CURRENT_STATE = "request_sent";
                                                btnRequest.setText("Cancle friend request");
                                                btnDecline.setVisibility(View.INVISIBLE);
                                                btnDecline.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}
