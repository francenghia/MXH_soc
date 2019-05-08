package com.example.franc.mxh.ViewActivity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.franc.mxh.Fragment.FindFriendByMapFragment;
import com.example.franc.mxh.Fragment.FriendsFragment;
import com.example.franc.mxh.Fragment.PhotoFragment;
import com.example.franc.mxh.Fragment.ProfileFragment;

import com.example.franc.mxh.R;
import com.example.franc.mxh.Utils.BottomNavigationBehavior;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProfileSetting extends AppCompatActivity {
    private Toolbar mToobar;

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
        setContentView(R.layout.activity_profile_setting);

        mToobar =findViewById(R.id.toolbarPost);
        setSupportActionBar(mToobar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        loadFragment(new ProfileFragment());

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportActionBar().setTitle("Profile");
        //hide/show
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navigation.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());


    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId())
                    {
                        case R.id.navigation_profile:
                            mToobar.setTitle("Profile");
                            loadFragment(new ProfileFragment());
                            return true;
                        case R.id.navigation_photo:
                            mToobar.setTitle("Photo");
                            loadFragment(new PhotoFragment());
                            return true;
                        case R.id.navigation_video:
                            mToobar.setTitle("Location");
                            loadFragment(new FindFriendByMapFragment());
                            return true;
                        case R.id.navigation_friends:
                            mToobar.setTitle("Friends");
                            loadFragment(new FriendsFragment());
                            return true;
                    }
                    return false;
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                UserProfileToActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserProfileToActivity() {
        Intent intent = new Intent(ProfileSetting.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
