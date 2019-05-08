package com.example.franc.mxh.ViewActivity;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageSwitcher;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.example.franc.mxh.Model.Posts;
import com.example.franc.mxh.R;
import com.example.franc.mxh.ViewHolder.SliderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class CardSliderPhotoActivity extends AppCompatActivity {

    private String plitKey;
    private FirebaseDatabase database;
    private DatabaseReference yourPhoto;
    private ImageSwitcher imageSwitcher;
    private TextSwitcher txtSwitcher;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recycler_sroll;
    private static int size;
    private int overallXScroll = 0;
    private int currentPosition;
    private String count[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_slider_photo);

        plitKey = getIntent().getExtras().get("uidKey").toString();
        txtSwitcher = findViewById(R.id.txtSwitcher);

        database = FirebaseDatabase.getInstance();
        yourPhoto = database.getReference("Posts");
        yourPhoto.orderByChild("uid").equalTo(plitKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                size = (int) dataSnapshot.getChildrenCount();
                count = new String[size];
                for (int i = 0; i < size; i++) {
                    count[i] = (i + 1) + "";
                }

                txtSwitcher.setFactory(new TextViewFactory(R.style.textSwitcher, true));
                txtSwitcher.setCurrentText(count[0] + "/" + size);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        recycler_sroll = findViewById(R.id.recyclerview_sroll);
        recycler_sroll.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recycler_sroll.setLayoutManager(layoutManager);
        loadDataPhoto();

        recycler_sroll.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                overallXScroll = overallXScroll + dx;

                int pos = overallXScroll / (getWidthScreen(CardSliderPhotoActivity.this) - 40);
                if (pos == RecyclerView.NO_POSITION || pos == currentPosition) {
                    return;
                }
                ChangeCard(pos);
            }
        });

    }

    private void ChangeCard(int pos) {
        int animation[] = {R.anim.slide_in_right, R.anim.slide_out_left};

        final boolean leftToRight = pos < currentPosition;
        if (leftToRight) {
            animation[0] = R.anim.slide_in_left;
            animation[1] = R.anim.slide_out_right;
        }

        txtSwitcher.setInAnimation(this, animation[0]);
        txtSwitcher.setOutAnimation(this, animation[1]);
        txtSwitcher.setText(count[pos % size] + "/" + size);

        currentPosition = pos;
    }

    public static int getWidthScreen(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        return p.x;
    }

    private void loadDataPhoto() {
        Query query = yourPhoto.orderByChild("uid").equalTo(plitKey);

        FirebaseRecyclerAdapter<Posts, SliderViewHolder> adapter = new FirebaseRecyclerAdapter<Posts, SliderViewHolder>(
                Posts.class,
                R.layout.item_photo_slider,
                SliderViewHolder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(SliderViewHolder viewHolder, Posts model, int position) {
                viewHolder.txtName.setText(model.getName());
                viewHolder.txtStatus.setText(model.getDescription());
                viewHolder.txtDate.setText(model.getTime()+" "+model.getDate());
                Picasso.with(getApplicationContext()).load(model.getPostimage()).into(viewHolder.imageYourPhoto);
                Picasso.with(getApplicationContext()).load(model.getProfileimage()).into(viewHolder.imageYourPosts);

            }
        };
        recycler_sroll.setAdapter(adapter);
    }

    private class TextViewFactory implements ViewSwitcher.ViewFactory {
        @StyleRes
        final int styleId;
        final boolean center;

        TextViewFactory(@StyleRes int styleId, boolean center) {
            this.styleId = styleId;
            this.center = center;
        }

        @Override
        public View makeView() {
            final TextView textView = new TextView(CardSliderPhotoActivity.this);
            if (center) {
                textView.setGravity(Gravity.CENTER);
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                textView.setTextAppearance(CardSliderPhotoActivity.this, styleId);
            } else {
                textView.setTextAppearance(styleId);
            }

            return textView;

        }
    }
}
