package com.example.franc.mxh.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.franc.mxh.ViewActivity.EditProfileSettings;
import com.example.franc.mxh.R;
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
public class ProfileFragment extends Fragment {

    private boolean isOpen = false;
    private ConstraintSet layout1, layout2;
    private ConstraintLayout constraintLayout;
    private ImageView imageViewPhoto;

    //.......
    private ImageView imageBackgroundCover;
    private TextView txtDescription;
    private TextView txtProfileName;
    private TextView txtProfileCountry;
    private TextView txtProfileDateOfBirth;
    private TextView txtProfilePhone;
    private TextView txtGender;
    private TextView txtStatus;

    private Button btnUpdateProfile;
    //.......

    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private DatabaseReference profileUser;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        database = FirebaseDatabase.getInstance();

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        profileUser = database.getReference("Users").child(currentUserId);

        layout1 = new ConstraintSet();
        layout2 = new ConstraintSet();
        constraintLayout = v.findViewById(R.id.constraint_layout);

        //....
        imageBackgroundCover = v.findViewById(R.id.cover);
        imageViewPhoto = v.findViewById(R.id.photo);
        txtDescription = v.findViewById(R.id.status_update);
        txtProfileName = v.findViewById(R.id.txtNameUser);
        txtProfileCountry = v.findViewById(R.id.txtCountry);
        txtProfileDateOfBirth = v.findViewById(R.id.txtDateOfBirth);
        txtProfilePhone = v.findViewById(R.id.txtPhone);
        txtGender = v.findViewById(R.id.txtGender);
        txtStatus=v.findViewById(R.id.status_update);
        btnUpdateProfile = v.findViewById(R.id.btnUpdateProfile);

        profileUser.addValueEventListener(new ValueEventListener() {
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
                    String status =dataSnapshot.child("status").getValue().toString();

                    Picasso.with(getContext()).load(image).into(imageViewPhoto);
                    Picasso.with(getContext()).load(cover).into(imageBackgroundCover);

                    txtProfileName.setText(name);
                    txtProfileCountry.setText(country);
                    txtProfileDateOfBirth.setText(dateofbirth);
                    txtProfilePhone.setText(phone);
                    txtGender.setText(gender);
                    txtStatus.setText(status);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        layout2.clone(getContext(), R.layout.profile_expanded);
        layout1.clone(constraintLayout);
        imageViewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOpen) {
                    TransitionManager.beginDelayedTransition(constraintLayout);
                    layout2.applyTo(constraintLayout);
                    isOpen = !isOpen;

                } else {
                    TransitionManager.beginDelayedTransition(constraintLayout);
                    layout1.applyTo(constraintLayout);
                    isOpen = !isOpen;
                }
            }
        });

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditProfileSettings.class);
                startActivity(intent);
            }
        });
        return v;


    }

}
