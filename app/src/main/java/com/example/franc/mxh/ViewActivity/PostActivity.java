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
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.franc.mxh.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class PostActivity extends AppCompatActivity {
    private Toolbar mToobar;
    private ImageView imagePost;
    private EditText edtContent;
    final static int Gallery_Pick = 1;
    private Uri imageUri;
    private String description_post;
    private String saveCurrentDate, saveCurrentTime, postRandomName, downloadUri, current_user_id;
    private long countPost = 0;
    //Firebase
    private StorageReference storageReference;
    private FirebaseDatabase database;
    private DatabaseReference users, postUser, photo;
    private FirebaseAuth auth;

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
        setContentView(R.layout.activity_post);


        imagePost = findViewById(R.id.imagePost);
        edtContent = findViewById(R.id.edtContentPost);
        //Firebase
        storageReference = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        users = database.getReference().child("Users");
        postUser = database.getReference().child("Posts");
        photo = database.getReference().child("Photos");
        auth = FirebaseAuth.getInstance();
        current_user_id = auth.getCurrentUser().getUid();

        mToobar = findViewById(R.id.toolbarPost);
        setSupportActionBar(mToobar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Post");


        imagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imagePost.setImageURI(imageUri);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                UserPostToActivity();
                break;
            case R.id.post_content:
                if(isOnline()){
                    UpdatePost();
                }else{
                    Toast.makeText(PostActivity.this, "Disconnected. Please check internet again !", Toast.LENGTH_SHORT).show();

                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post, menu);
        return true;
    }

    private void UserPostToActivity() {
        Intent intent = new Intent(PostActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void UpdatePost() {
        description_post = edtContent.getText().toString();
        if (imageUri == null) {
            Toast.makeText(this, "Please select image to update !", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(description_post)) {
            Toast.makeText(this, "Please enter your content ", Toast.LENGTH_SHORT).show();
        } else {
            StorageImageToFirebase();
        }
    }

    private void StorageImageToFirebase() {
        Calendar calendarDate = Calendar.getInstance();
        SimpleDateFormat simpleDateDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = simpleDateDate.format(calendarDate.getTime());

        Calendar calendarTime = Calendar.getInstance();
        SimpleDateFormat simpleDateTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = simpleDateTime.format(calendarTime.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;

        StorageReference filePath = storageReference.child("Post Images")
                .child(imageUri.getLastPathSegment() + postRandomName + ".jpg");

        final android.app.AlertDialog waitingDialog = new SpotsDialog(PostActivity.this);
        waitingDialog.show();

        filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isComplete()) {
                    waitingDialog.dismiss();
                    downloadUri = task.getResult().getDownloadUrl().toString();
                    SavingPostImageToFirebase();
                } else {
                    waitingDialog.dismiss();
                    String message = task.getException().getMessage();
                    Toast.makeText(PostActivity.this, "Error :" + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SavingPostImageToFirebase() {

        postUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    countPost = dataSnapshot.getChildrenCount();
                } else {
                    countPost = 0;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        users.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userFullName = dataSnapshot.child("name").getValue().toString();
                    String userProfileImage = dataSnapshot.child("image").getValue().toString();

                    HashMap postHashMap = new HashMap();
                    postHashMap.put("uid", current_user_id);
                    postHashMap.put("date", saveCurrentDate);
                    postHashMap.put("time", saveCurrentTime);
                    postHashMap.put("description", description_post);
                    postHashMap.put("postimage", downloadUri);
                    postHashMap.put("profileimage", userProfileImage);
                    postHashMap.put("name", userFullName);
                    postHashMap.put("counter", countPost);


                    postUser.child(current_user_id+"%%"+postRandomName).updateChildren(postHashMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                HashMap putPhoto = new HashMap();
                                putPhoto.put("photo", downloadUri);
                                putPhoto.put("date", saveCurrentDate);
                                putPhoto.put("time", saveCurrentTime);
                                putPhoto.put("uid", current_user_id);

                                photo.child(current_user_id).child(postRandomName).updateChildren(putPhoto).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()) {
                                            Log.d("Kiem tra","Successfully");
                                        } else {
                                            Toast.makeText(PostActivity.this, "Failed :" + task.getException()
                                                    , Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("Kiem Tra", "" + e.getMessage());
                                    }
                                });
                            } else {
                                String message = task.getException().getMessage();
                                Toast.makeText(PostActivity.this, "Error :" + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
