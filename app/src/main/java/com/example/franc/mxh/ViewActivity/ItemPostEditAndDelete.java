package com.example.franc.mxh.ViewActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.franc.mxh.FaceDetection.FaceDetetive;
import com.example.franc.mxh.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ItemPostEditAndDelete extends AppCompatActivity {
    private Toolbar mToobar;
    private String PostKey, currentUserId, databaseUser, description, image;
    private DatabaseReference postRef;
    private FirebaseAuth auth;
    private MenuItem itemEdit, itemDelete;
    private ImageView imageEdit;
    private TextView txtEditContent;
    private Button btnCardSliderPhoto;
    private String[] splitKey;

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
        setContentView(R.layout.activity_item_post_edit_and_delete);

        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();

        mToobar = findViewById(R.id.toolbarEdit);
        setSupportActionBar(mToobar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Post");

        btnCardSliderPhoto=findViewById(R.id.btnCardSliderPhoto);
        PostKey = getIntent().getExtras().get("PostKey").toString();
        splitKey = PostKey.split("%%");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey);
        imageEdit = findViewById(R.id.imagePostEdit);
        txtEditContent = findViewById(R.id.edtContentPostEdit);
        txtEditContent.setEnabled(false);

        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    description = dataSnapshot.child("description").getValue().toString();
                    image = dataSnapshot.child("postimage").getValue().toString();
                    databaseUser = dataSnapshot.child("uid").getValue().toString();
                    txtEditContent.setText(description);
                    Picasso.with(getBaseContext()).load(image).into(imageEdit);
                    imageEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ItemPostEditAndDelete.this, FaceDetetive.class);
                            intent.putExtra("image", image);
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        btnCardSliderPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ItemPostEditAndDelete.this,CardSliderPhotoActivity.class);
                intent.putExtra("uidKey",splitKey[0]);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                UserPostToActivity();
                break;
            case R.id.item_Edit:
                if (isOnline()) {
                    EditCurrentPost(description);
                } else {
                    Toast.makeText(ItemPostEditAndDelete.this, "Disconnected. Please check internet again !", Toast.LENGTH_SHORT).show();

                }

                break;
            case R.id.item_Delete:
                if (isOnline()) {
                    DeleteCurrentPost();
                } else {
                    Toast.makeText(ItemPostEditAndDelete.this, "Disconnected. Please check internet again !", Toast.LENGTH_SHORT).show();

                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void EditCurrentPost(String description) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Post");
        builder.setMessage("Please enter your description post !");

        final EditText editText = new EditText(ItemPostEditAndDelete.this);
        editText.setText(description);
        builder.setView(editText);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
                postRef.child("description").setValue(editText.getText().toString());
                Toast.makeText(ItemPostEditAndDelete.this, "Update Successful", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });

        builder.show();

    }


    private void DeleteCurrentPost() {
        postRef.removeValue();
        GoToBackActivity();
        Toast.makeText(this, "Delete Post Successful", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        itemEdit = menu.findItem(R.id.item_Edit);
        itemDelete = menu.findItem(R.id.item_Delete);
        itemEdit.setVisible(false);
        itemDelete.setVisible(false);
        if (currentUserId.equals(databaseUser)) {
            itemEdit.setVisible(true);
            itemDelete.setVisible(true);
        }
        return true;
    }

    private void UserPostToActivity() {
        Intent intent = new Intent(ItemPostEditAndDelete.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void GoToBackActivity() {
        Intent intent = new Intent(ItemPostEditAndDelete.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
