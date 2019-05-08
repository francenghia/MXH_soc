package com.example.franc.mxh.ViewActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.franc.mxh.R;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SlashScreen extends AppCompatActivity {

    private TextView txtSocial,txtAnim;
    Animation animation;

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
        setContentView(R.layout.activity_slash_screen);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        txtSocial=findViewById(R.id.txtSocial);
        txtAnim=findViewById(R.id.txtAnim);

        animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.push_right);
        txtSocial.setAnimation(animation);

        animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.push_left);
        txtAnim.setAnimation(animation);

        Handler handler =new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SlashScreen.this,Login_Register_Activity.class));
                finish();
            }
        },3000);
    }
}
