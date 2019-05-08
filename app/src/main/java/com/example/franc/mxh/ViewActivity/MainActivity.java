package com.example.franc.mxh.ViewActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.franc.mxh.Model.Posts;
import com.example.franc.mxh.R;
import com.example.franc.mxh.ViewHolder.PostUserViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private ActionBarDrawerToggle myToggle;
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private Toolbar mToolbar;
    private Button btnPostContent;
    private CircleImageView circleImageView, imagePost;


    private final static int Gallery_Pick = 1;

    private TextView txtName, txtEmail;
    //Firebase
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference getUser, PostUser, LikeRef, countRequest;
    private StorageReference storageReference;
    public String current_user_id ="";
    Boolean LikeChecker = false;

    private String countReceivedRequest = "";




    //notification
    private TextView friends, find_friends;


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
        setContentView(R.layout.activity_main);

        //Toolbar
        mToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        //Firebase
        auth = FirebaseAuth.getInstance();
        current_user_id = auth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        PostUser = database.getReference().child("Posts");
        LikeRef=database.getReference().child("Likes");
        countRequest = database.getReference().child("FriendRequests");
        getUser = database.getReference().child("Users").child(current_user_id);


        storageReference = FirebaseStorage.getInstance().getReference().child("Profile Image");

        drawerLayout = findViewById(R.id.myDrawer);
        myToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(myToggle);
        myToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);






        imagePost = findViewById(R.id.imageCirclePost);
        imagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenProfile();
            }
        });
        btnPostContent = findViewById(R.id.btnPostContent);
        btnPostContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PostActivity.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.all_user_post);
        recyclerView.setHasFixedSize(true);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        recyclerView.setLayoutManager(linearLayoutManager);


        //Notification Request

        navigationView = findViewById(R.id.navigationview);
        //Notification
        friends = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_friends));
        find_friends = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_find_friends));
        initializeCountDrawer();


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                UserMenuSelector(item);
                return false;


            }
        });


        ListPostOfUser();


        //set Inform
        View view_header = navigationView.getHeaderView(0);
        txtName = view_header.findViewById(R.id.txtName);
        txtEmail = view_header.findViewById(R.id.txtEmail);
        circleImageView = view_header.findViewById(R.id.circleImage);

        getUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                txtName.setText(name);
                txtEmail.setText(email);

                Picasso.with(getBaseContext()).load(image).into(circleImageView);
                Picasso.with(getBaseContext()).load(image).into(imagePost);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });


    }

    //set status Online/Offline
    private void statusUserOnlineOrOffline(String state) {
        String saveCurrentDate, saveCurrentTime;

        Calendar calendarDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
        saveCurrentDate = currentDate.format(calendarDate.getTime());

        Calendar calendarTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendarTime.getTime());

        Map currentStateMap = new HashMap();

        currentStateMap.put("time", saveCurrentTime);
        currentStateMap.put("date", saveCurrentDate);
        currentStateMap.put("type", state);

        getUser.child("State").updateChildren(currentStateMap);


    }


    private void initializeCountDrawer() {
        friends.setGravity(Gravity.CENTER_VERTICAL);
        friends.setTypeface(null, Typeface.BOLD);
        friends.setTextColor(getResources().getColor(R.color.colorAccent));

//        friends.setText("10+");


        find_friends.setGravity(Gravity.CENTER_VERTICAL);
        find_friends.setTypeface(null, Typeface.BOLD);
        find_friends.setTextColor(getResources().getColor(R.color.colorAccent));
//        find_friends.setText("2");


        countRequest.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    countReceivedRequest = String.valueOf(dataSnapshot.getChildrenCount());
                    find_friends.setText(new StringBuilder("+").append(countReceivedRequest));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void OpenProfile() {
        Intent intent = new Intent(MainActivity.this, ProfileSetting.class);
        startActivity(intent);
    }


    private void ListPostOfUser() {

        Query sortListPost = PostUser.orderByChild("counter");


        FirebaseRecyclerAdapter<Posts, PostUserViewHolder> adapter = new FirebaseRecyclerAdapter<Posts, PostUserViewHolder>(
                Posts.class,
                R.layout.cardlist_post_user,
                PostUserViewHolder.class,
                sortListPost

        ) {
            @Override
            protected void populateViewHolder(PostUserViewHolder viewHolder, Posts model, int position) {
                final String postKey = getRef(position).getKey();
                Picasso.with(getBaseContext()).load(model.getPostimage()).into(viewHolder.postImage);
                Picasso.with(getBaseContext()).load(model.getProfileimage()).into(viewHolder.postProfileImage);
                viewHolder.nameUser.setText(model.getName());
                viewHolder.time.setText(model.getTime() + " vào ngày: " + model.getDate());
                viewHolder.description.setText(model.getDescription());


                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, ItemPostEditAndDelete.class);
                        intent.putExtra("PostKey", postKey);
                        startActivity(intent);
                        finish();
                    }
                });

                viewHolder.setStatusLike(postKey);
                viewHolder.imgBtnLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LikeChecker = true ;

                        LikeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(LikeChecker.equals(true)){
                                    if(dataSnapshot.child(postKey).hasChild(current_user_id))
                                    {
                                        LikeRef.child(postKey).child(current_user_id).removeValue();
                                        LikeChecker =false;
                                    }
                                    else {
                                        LikeRef.child(postKey).child(current_user_id).setValue(true);
                                        LikeChecker =false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
                viewHolder.imgBtnComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this,CommentActivity.class);
                        intent.putExtra("postKey",postKey);
                        startActivity(intent);
                    }
                });



            }
        };
        recyclerView.setAdapter(adapter);

        statusUserOnlineOrOffline("online");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {

            Uri imageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();

                final String current_user = auth.getCurrentUser().getUid();
                StorageReference filePath = storageReference.child(current_user + ".ipg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isComplete()) {
                            Toast.makeText(MainActivity.this,
                                    "Saving your profile image to Firebase Storage !", Toast.LENGTH_LONG).show();


                            final String downloadUrl = task.getResult().getDownloadUrl().toString();
                            getUser.child("image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(MainActivity.this, "Profile Image Upload Successfully", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            Toast.makeText(MainActivity.this, "Error occured , while uploading your profile pic", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d("Kiem tra", error.getMessage() + "");
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (myToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_friends:
                FriendList();
                break;
            case R.id.nav_find_friends:
                FindFriends();
                break;
            case R.id.nav_settings:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
                statusUserOnlineOrOffline("offline");
                auth.signOut();
                UserToLogin();
                break;
        }
    }



    private void FriendList() {
        Intent intent = new Intent(MainActivity.this, FriendsActivity.class);
        startActivity(intent);
    }

    private void UserToLogin() {
        Intent intent = new Intent(MainActivity.this, Login_Register_Activity.class);
        startActivity(intent);
        finish();
    }

    private void FindFriends() {
        Intent intent = new Intent(MainActivity.this, FindFriendsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        statusUserOnlineOrOffline("online");
    }

    @Override
    protected void onResume() {
        super.onResume();
        statusUserOnlineOrOffline("online");
    }
}
