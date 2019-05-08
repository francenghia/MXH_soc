package com.example.franc.mxh.ViewActivity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.franc.mxh.R;
import com.example.franc.mxh.Adapter.ViewPagerAdapter;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Login_Register_Activity extends AppCompatActivity {
    private ViewPager viewPager ;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Merienda-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_login__register_);

        viewPager = findViewById(R.id.viewPager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        viewPager.setPageTransformer(true, new FidgetSpinTransformation ());
    }
    public class FidgetSpinTransformation implements ViewPager.PageTransformer {
        @Override
        public void transformPage(View page, float position) {

            page.setTranslationX(-position * page.getWidth());

            if (Math.abs(position) < 0.5) {
                page.setVisibility(View.VISIBLE);
                page.setScaleX(1 - Math.abs(position));
                page.setScaleY(1 - Math.abs(position));
            } else if (Math.abs(position) > 0.5) {
                page.setVisibility(View.GONE);
            }



            if (position < -1){     // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.setAlpha(0);

            }
            else if (position <= 0) {    // [-1,0]
                page.setAlpha(1);
                page.setRotation(36000*(Math.abs(position)*Math.abs(position)*Math.abs(position)*Math.abs(position)*Math.abs(position)*Math.abs(position)*Math.abs(position)));

            }else if (position <= 1){    // (0,1]
                page.setAlpha(1);
                page.setRotation(-36000 *(Math.abs(position)*Math.abs(position)*Math.abs(position)*Math.abs(position)*Math.abs(position)*Math.abs(position)*Math.abs(position)));

            }
            else {    // (1,+Infinity]
                // This page is way off-screen to the right.
                page.setAlpha(0);

            }


        }
    }


}
