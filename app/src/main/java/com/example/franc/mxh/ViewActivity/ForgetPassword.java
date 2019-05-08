package com.example.franc.mxh.ViewActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.franc.mxh.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ForgetPassword extends AppCompatActivity {
    private Toolbar mToobar;
    private MaterialEditText edtEmailForget;
    private Button btnRecoverPass;
    FirebaseAuth auth;

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
        setContentView(R.layout.activity_forget_password);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        mToobar = findViewById(R.id.toolbarForget);
        setSupportActionBar(mToobar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Forget Passowrd");
        //auth

        auth = FirebaseAuth.getInstance();
        edtEmailForget = findViewById(R.id.edtEmailForget);
        btnRecoverPass = findViewById(R.id.btnRecoverPass);
        btnRecoverPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline()) {
                    String email = edtEmailForget.getText().toString();
                    if (TextUtils.isEmpty(email)) {
                        Toast.makeText(ForgetPassword.this, "Please write your email ...", Toast.LENGTH_SHORT).show();
                    } else {
                        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgetPassword.this, "Successfull ! Check email your account !", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ForgetPassword.this, Login_Register_Activity.class));
                                    finish();
                                } else {
                                    String message = task.getException().getMessage();
                                    Toast.makeText(ForgetPassword.this, "Error ! " + message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(ForgetPassword.this, "Disconnected. Please check internet again !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                UserForgetToActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserForgetToActivity() {
        Intent intent = new Intent(ForgetPassword.this, Login_Register_Activity.class);
        startActivity(intent);
        finish();
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
}
