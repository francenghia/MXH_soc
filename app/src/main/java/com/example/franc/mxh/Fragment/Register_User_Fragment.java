package com.example.franc.mxh.Fragment;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.franc.mxh.Model.User;
import com.example.franc.mxh.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import dmax.dialog.SpotsDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class Register_User_Fragment extends Fragment {
    MaterialEditText edtEmail;
    MaterialEditText edtPassword;
    MaterialEditText edtPhone;
    MaterialEditText edtName;
    Button btnRegister;
    RelativeLayout rootLayout;

    //Firebase
    FirebaseDatabase database;
    DatabaseReference users;
    FirebaseAuth auth;

    public Register_User_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_register__user_, container, false);

        //Firebase
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        auth = FirebaseAuth.getInstance();


        edtEmail = view.findViewById(R.id.edtEmail);
        edtPassword = view.findViewById(R.id.edtPassword);
        edtPhone = view.findViewById(R.id.edtPhone);
        edtName = view.findViewById(R.id.edtName);
        btnRegister = view.findViewById(R.id.btnRegister);
        rootLayout = view.findViewById(R.id.rootLayout);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOnline()){
                    RegisterUser();
                }else{
                    Toast.makeText(getContext(), "Disconnected", Toast.LENGTH_SHORT).show();
                }

            }
        });
        return view;


    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void RegisterUser() {
        if (!isValidEmail(edtEmail.getText().toString())) {
            Snackbar.make(rootLayout, "Email invalid ! Please enter your email", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(edtPassword.getText().toString())) {
            Snackbar.make(rootLayout, "Please enter email address", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (edtPassword.getText().toString().length() < 6) {
            Snackbar.make(rootLayout, "Password too short", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(edtName.getText().toString())) {
            Snackbar.make(rootLayout, "Please enter your name", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(edtPhone.getText().toString())) {
            Snackbar.make(rootLayout, "Please enter your phone", Snackbar.LENGTH_SHORT).show();
            return;
        }

        final android.app.AlertDialog waitingDialog = new SpotsDialog(getContext());
        waitingDialog.show();

        auth.fetchProvidersForEmail(edtEmail.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                        boolean checkUserExist = !task.getResult().getProviders().isEmpty();

                        if (!checkUserExist) {
                            auth.createUserWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            User user = new User();
                                            user.setEmail(edtEmail.getText().toString());
                                            user.setPassword(edtPassword.getText().toString());
                                            user.setName(edtName.getText().toString());
                                            user.setPhone(edtPhone.getText().toString());
                                            user.setImage("https://cdn1.iconfinder.com/data/icons/user-pictures/100/unknown-512.png");
                                            user.setCountry("VietNam");
                                            user.setDateofbirth("1/1/2018");
                                            user.setGender("Nam/Nữ");
                                            user.setCover("https://cdn1.iconfinder.com/data/icons/user-pictures/100/unknown-512.png");
                                            user.setStatus("No status");


                                            users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .setValue(user)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            waitingDialog.dismiss();
                                                            Snackbar.make(rootLayout, "Register Successfully", Snackbar.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Snackbar.make(rootLayout, "Failed " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                                        }
                                                    });

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Snackbar.make(rootLayout, "Failed " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                        }
                                    });


                        } else {
                            waitingDialog.dismiss();
                            Snackbar.make(rootLayout, "User already presents", Snackbar.LENGTH_SHORT).show();

                        }
                    }
                });
    }
    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        if(netInfo!=null && netInfo.isConnectedOrConnecting()){
            return true;
        }else {
            return false;
        }
    }

}
