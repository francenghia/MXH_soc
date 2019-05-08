package com.example.franc.mxh.FaceDetection;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.franc.mxh.ViewActivity.MainActivity;
import com.example.franc.mxh.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class FaceDetetive extends AppCompatActivity {
    private Toolbar mToobar;
    private String keyFace;
    private ImageView imageFace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detetive);
        mToobar = findViewById(R.id.toolbarFace);

        setSupportActionBar(mToobar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Post");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#330000ff")));
        getSupportActionBar().setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#550000ff")));

        keyFace = getIntent().getExtras().get("image").toString();

        imageFace = findViewById(R.id.imageFaceDetection);

        Picasso.with(getApplicationContext()).load(keyFace).into(imageFace);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                FaceBackToMain();
                break;
            case R.id.mnuDetect:
                if(isOnline()){
                    new AsyncTaskLoadImage(imageFace).execute(keyFace);
                }else{
                    Toast.makeText(this, "Please check internet to face identification", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class AsyncTaskLoadImage extends AsyncTask<String, String, Bitmap> {
        private final static String TAG = "AsyncTaskLoadImage";
        private ImageView imageView;

        public AsyncTaskLoadImage(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(params[0]);
                bitmap = BitmapFactory.decodeStream((InputStream) url.getContent());
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            Paint myRectPaint = new Paint();
            myRectPaint.setStrokeWidth(5);
            myRectPaint.setColor(Color.WHITE);
            myRectPaint.setStyle(Paint.Style.STROKE);

            Bitmap tempBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
            Canvas tempCanvas = new Canvas(tempBitmap);
            tempCanvas.drawBitmap(bitmap, 0, 0, null);

            FaceDetector faceDetector = new
                    FaceDetector.Builder(getApplicationContext()).setTrackingEnabled(false)
                    .build();
            if (!faceDetector.isOperational()) {
                new AlertDialog.Builder(getApplicationContext()).setMessage("Could not set up the face detector!").show();
                return;
            }

            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<Face> faces = faceDetector.detect(frame);

            for (int i = 0; i < faces.size(); i++) {
                Face thisFace = faces.valueAt(i);
                float x1 = thisFace.getPosition().x;
                float y1 = thisFace.getPosition().y;
                float x2 = x1 + thisFace.getWidth();
                float y2 = y1 + thisFace.getHeight();
                tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);
            }
            imageView.setImageBitmap(tempBitmap);
        }

    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detec, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void FaceBackToMain() {
        Intent intent = new Intent(FaceDetetive.this, MainActivity.class);
        startActivity(intent);
        finish();
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
