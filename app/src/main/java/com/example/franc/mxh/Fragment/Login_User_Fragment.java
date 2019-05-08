package com.example.franc.mxh.Fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.franc.mxh.ViewActivity.ForgetPassword;
import com.example.franc.mxh.ViewActivity.MainActivity;
import com.example.franc.mxh.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import dmax.dialog.SpotsDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class Login_User_Fragment extends Fragment {
    MaterialEditText edtEmail;
    MaterialEditText edtPassword;
    private Button btnLogin;
    private ImageView imgGoogle;
    private TextView txtForgetPassword;


    private RelativeLayout rootLayout;
    //Firebase
    FirebaseDatabase database;
    DatabaseReference users;
    FirebaseAuth auth;
    //Login Google
    private GoogleApiClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "Login with Google";

    public Login_User_Fragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_login_user_, container, false);

        //
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        auth = FirebaseAuth.getInstance();

        rootLayout = view.findViewById(R.id.rootLayout);
        btnLogin = view.findViewById(R.id.btnLogin);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPassword = view.findViewById(R.id.edtPassword);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOnline()){
                    LoginUser();
                }else{
                    Toast.makeText(getContext(), "Disconnected. Please check internet again!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        txtForgetPassword= view.findViewById(R.id.txtForgetPassword);
        txtForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),ForgetPassword.class);
                startActivity(intent);
            }
        });

        //Login with google
//        imgGoogle = view.findViewById(R.id.imgGoogle);
//
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//
//        mGoogleSignInClient = new GoogleApiClient.Builder(getContext())
//                .enableAutoManage((FragmentActivity) getContext(), new GoogleApiClient.OnConnectionFailedListener() {
//                    @Override
//                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//                        Toast.makeText(getContext(), "Failed " + connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();

//        imgGoogle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(isOnline()){
//                    LoginWithGoogleFirebase();
//                }else{
//                    Toast.makeText(getContext(), "Disconnected. Please check internet again!", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
        return view;
    }


    private void LoginWithGoogleFirebase() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        startActivityForResult(intent, RC_SIGN_IN);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == RC_SIGN_IN) {
//
//            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//
//            if (result.isSuccess()) {
//
//                GoogleSignInAccount account = result.getSignInAccount();
//                FirebaseAuthWithGoogle(account);
//            } else {
//                Toast.makeText(getContext(), "Can't get Auth result ", Toast.LENGTH_SHORT).show();
//            }
//        }else{
//            Toast.makeText(getContext(), "Can't get Auth result ", Toast.LENGTH_SHORT).show();
//        }
//    }

//    private void FirebaseAuthWithGoogle(GoogleSignInAccount acct) {
//        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
//        final android.app.AlertDialog waitingDialog = new SpotsDialog(getContext());
//        waitingDialog.show();
//
//        AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
//        auth.signInWithCredential(authCredential)
//                .addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            waitingDialog.dismiss();
//                            Log.d(TAG, "signInWithCredential:success");
//                            LoginToEmail();
//                        } else {
//                            waitingDialog.dismiss();
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            LoginToGoogleError();
//                            Toast.makeText(getContext(), "Error :"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getContext(), "Error :"+e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }

    private void LoginToEmail(){
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    private void LoginToGoogleError(){
        Intent intent = new Intent(getContext(), Register_User_Fragment.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void LoginUser() {

        if (TextUtils.isEmpty(edtEmail.getText().toString())) {
            Snackbar.make(rootLayout, "Please enter email address", Snackbar.LENGTH_SHORT).show();
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
        final android.app.AlertDialog waitingDialog = new SpotsDialog(getContext());
        waitingDialog.show();
        auth.signInWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        waitingDialog.dismiss();
                        LoginToEmail();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                waitingDialog.dismiss();
                Snackbar.make(rootLayout, "Failed " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
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
