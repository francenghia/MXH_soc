package com.example.franc.mxh.ViewActivity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.franc.mxh.Model.Friends;
import com.example.franc.mxh.R;
import com.example.franc.mxh.ViewHolder.DefaultViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FindFriendsActivity extends AppCompatActivity {
    private Toolbar mToobar;
    private RecyclerView recycler_view_find;
    private EditText edtSearchUser;
    private ImageButton imageSearch;


    private DatabaseReference DefaultOrFriends;

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
        setContentView(R.layout.activity_find_friends);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        mToobar = findViewById(R.id.toolbarFind);
        setSupportActionBar(mToobar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");

        DefaultOrFriends = FirebaseDatabase.getInstance().getReference("Users");


        recycler_view_find=findViewById(R.id.recycler_find_friends);
        edtSearchUser=findViewById(R.id.edtSearch);
        imageSearch =findViewById(R.id.search_btn);

        recycler_view_find.setHasFixedSize(true);
        recycler_view_find.setLayoutManager(new LinearLayoutManager(this));

        imageSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String search_default= edtSearchUser.getText().toString();

                Query firebaseSearchQuery = DefaultOrFriends.orderByChild("name").startAt(search_default).endAt(search_default + "\uf8ff");


                FirebaseRecyclerAdapter<Friends,DefaultViewHolder> adapter = new FirebaseRecyclerAdapter<Friends, DefaultViewHolder>(
                        Friends.class,
                        R.layout.display_all_user,
                        DefaultViewHolder.class,
                        firebaseSearchQuery
                ) {
                    @Override
                    protected void populateViewHolder(DefaultViewHolder viewHolder, Friends model, final int position) {
                        viewHolder.setDetails(getApplicationContext(), model.getName(), model.getStatus(), model.getImage());
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String viewPersonalPage =getRef(position).getKey();

                                Intent intent = new Intent(FindFriendsActivity.this,ProfileActivity.class);
                                intent.putExtra("viewPersonalPage",viewPersonalPage);
                                startActivity(intent);
                            }
                        });
                    }
                };
                adapter.notifyDataSetChanged();
                recycler_view_find.setAdapter(adapter);
            }
        });



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                GoToBackActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void GoToBackActivity() {
        Intent intent = new Intent(FindFriendsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }


}
