package com.example.franc.mxh.ViewActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.franc.mxh.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EditProfileSettings extends AppCompatActivity {
    private MaterialEditText edtName, edtPhone, edtGender, edtCountry, edtDateOfBirth,edtStatus;
    private TextView txtUpdateCover;
    private Button btnUpdateProfileEdit;
    private ImageView ImagePhotoCover;
    private String saveCurrentDate, saveCurrentTime, postRandomName;
    private Toolbar toolbar;
    //Fireabase
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference Users;
    private String currentUserId;
    private Uri imageUri;
    private String downloadUri;
    private static final int requestcode = 100;

    private String name,phone,gender,country,dateofbirth,status ;

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
        setContentView(R.layout.activity_comment);
        setContentView(R.layout.activity_edit_profile_settings);
        //Firebase
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Card image");
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        Users = database.getReference("Users").child(currentUserId);
        storageReference = FirebaseStorage.getInstance().getReference();

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtGender = findViewById(R.id.edtGender);
        edtCountry = findViewById(R.id.edtCountry);
        edtStatus =findViewById(R.id.status);
        edtDateOfBirth = findViewById(R.id.edtDateOfBirth);
        txtUpdateCover = findViewById(R.id.txtUpdateCover);

        btnUpdateProfileEdit = findViewById(R.id.btnUpdateProfileEdit);
        ImagePhotoCover = findViewById(R.id.cover);
        txtUpdateCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseImageCover();
            }
        });
        btnUpdateProfileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOnline()){
                    name = edtName.getText().toString();
                    phone = edtPhone.getText().toString();
                    gender = edtGender.getText().toString();
                    country = edtCountry.getText().toString();
                    dateofbirth = edtDateOfBirth.getText().toString();
                    status = edtStatus.getText().toString();

                    if (imageUri == null) {
                        Toast.makeText(EditProfileSettings.this,
                                "Please select image to update !", Toast.LENGTH_SHORT).show();
                    }
                    else  if (TextUtils.isEmpty(name)) {
                        Toast.makeText(EditProfileSettings.this, "Please enter your name !", Toast.LENGTH_SHORT).show();
                    }
                    else  if (TextUtils.isEmpty(phone)) {
                        Toast.makeText(EditProfileSettings.this, "Please enter your phone !", Toast.LENGTH_SHORT).show();
                    }
                    else  if (TextUtils.isEmpty(gender)) {
                        Toast.makeText(EditProfileSettings.this, "Please enter your gender !", Toast.LENGTH_SHORT).show();
                    }
                    else  if (TextUtils.isEmpty(country)) {
                        Toast.makeText(EditProfileSettings.this, "Please enter your country !", Toast.LENGTH_SHORT).show();
                    }
                    else  if(TextUtils.isEmpty(dateofbirth)) {
                        Toast.makeText(EditProfileSettings.this, "Please enter your date of birth !", Toast.LENGTH_SHORT).show();
                    }
                    else if (TextUtils.isEmpty(status)) {
                        Toast.makeText(EditProfileSettings.this, "Please enter your status !", Toast.LENGTH_SHORT).show();

                    } else{
                        StorageImageToFirebase();
                    }
                }else{
                    Toast.makeText(EditProfileSettings.this, "Disconnected. Please check internet again !", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void ChooseImageCover() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, requestcode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestCode && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            ImagePhotoCover.setImageURI(imageUri);

        }
    }

    private void UpdateSettingProfile() {

        Users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Users.child("name").setValue(name);
                    Users.child("phone").setValue(phone);
                    Users.child("gender").setValue(gender);
                    Users.child("country").setValue(country);
                    Users.child("dateofbirth").setValue(dateofbirth);
                    Users.child("status").setValue(status);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void UserPostToActivity() {
        Intent intent = new Intent(EditProfileSettings.this, ProfileSetting.class);
        startActivity(intent);
        finish();
    }

    private void StorageImageToFirebase() {
        Calendar calendarDate = Calendar.getInstance();
        SimpleDateFormat simpleDateDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = simpleDateDate.format(calendarDate.getTime());

        Calendar calendarTime = Calendar.getInstance();
        SimpleDateFormat simpleDateTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = simpleDateTime.format(calendarTime.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;
        final android.app.AlertDialog waitingDialog = new SpotsDialog(EditProfileSettings.this);
        waitingDialog.show();
        StorageReference filePath = storageReference.child("Post Images Cover").child(imageUri.getLastPathSegment() + postRandomName + ".jpg");
        filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isComplete()) {
                    waitingDialog.dismiss();
                    downloadUri =task.getResult().getDownloadUrl().toString();
                    Toast.makeText(EditProfileSettings.this, "Saved Image Storage ", Toast.LENGTH_SHORT).show();
                    Users.child("cover").setValue(downloadUri);
                    UpdateSettingProfile();
                    UserPostToActivity();
                } else {
                    waitingDialog.dismiss();
                    String message =task.getException().getMessage();
                    Toast.makeText(EditProfileSettings.this, "Error :"+message, Toast.LENGTH_SHORT).show();
                }
            }
        });
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
