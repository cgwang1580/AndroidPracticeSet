package com.example.blurrytest;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.File;

public class BlurTestActivity extends AppCompatActivity {

    private static final String TAG = "BlurTestActivity";

    @SuppressLint("SdCardPath")
    private final static String ImagePath = "/sdcard/OpenGLESTest/coji_1080x1920.jpg";
    private boolean bDoBlurry = false;
    Bitmap mOriginBitmap = null;
    ImageView mImageViewOri = null;
    ImageView mImageViewBlu = null;
    GLSurfaceView mGLSurfaceView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageViewOri = findViewById(R.id.image_view_ori);
        mImageViewBlu = findViewById(R.id.image_view_blu);
        mGLSurfaceView = findViewById(R.id.gl_surface_view);
        mOriginBitmap = getBitmapFromFile();
        findViewById(R.id.btn_blur).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bDoBlurry = !bDoBlurry;
                showImageView(mImageViewBlu, bDoBlurry);
                showImageView(mGLSurfaceView, bDoBlurry);
            }
        });

        findViewById(R.id.btn_cartoon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeCartoon(mImageViewOri, true);
            }
        });

        findViewById(R.id.btn_larger_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeLarger(mImageViewOri, true);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mImageViewOri.setBackground(new BitmapDrawable(getResources(), mOriginBitmap));
        mImageViewBlu.setBackground(new BitmapDrawable(getResources(), mOriginBitmap));
        mGLSurfaceView.setBackground(new BitmapDrawable(getResources(), mOriginBitmap));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private Bitmap getBitmapFromFile () {

        Bitmap bitmap;
        File file = new File(ImagePath);
        if (file.exists()) {
            bitmap = BitmapFactory.decodeFile(ImagePath);
            return bitmap;
        } else {
            return null;
        }
    }

    private void showImageView(View view, boolean bBlurry) {

        if (null != mOriginBitmap) {
            view.setBackgroundResource(0);
            view.setBackground(new BitmapDrawable(getResources(), mOriginBitmap));
        }

        if (bBlurry) {
            long time = System.currentTimeMillis();
            Bitmap bitmap = BlurBitmap.blur(this, mOriginBitmap);
            long timeCost = System.currentTimeMillis() - time;
            Log.d ("BlurTestActivity", "timeCost = " + timeCost);
            view.setBackground(new BitmapDrawable(getResources(), bitmap));

            /*Blurry.with(this)
                    .radius(10)
                    .sampling(8)
                    .color(Color.argb(66, 255, 255, 255))
                    .async()
                    .capture(view)
                    .into(view);*/
        } else {

        }
    }

    private void makeCartoon (View view, boolean bDo) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f, 1f);
        animator.setDuration(3000);
        animator.start();

        ObjectAnimator animator1 = ObjectAnimator.ofFloat(view, "scaleY", 1f, 2f, 1f);
        animator1.setDuration(2000);
        animator1.start();
    }

    private void makeLarger (View view, boolean bDo) {
        WindowManager windowManager = this.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getRealSize(point);
        Log.d(TAG,"makeLarger (" + point.x + " ," + point.y + ")");

        ViewGroup.LayoutParams params = view.getLayoutParams();
        float ratio = 1.f * 16 / 9;
        /*params.width = point.x;
        params.height = (int)(params.width * ratio);
        imageView.setLayoutParams(params);*/

        ObjectAnimator animator1 = ObjectAnimator.ofFloat(view, "scaleY", 1.f, ratio);
        animator1.setDuration(1000);
        animator1.start();
    }
}