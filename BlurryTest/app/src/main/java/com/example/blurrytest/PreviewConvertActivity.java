package com.example.blurrytest;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

public class PreviewConvertActivity extends AppCompatActivity {

    private final static String TAG = "PreviewConvertActivity";

    private int mStatusBarHeight = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        initUI ();
        setViewListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mStatusBarHeight = Utils.getStatusBarHeight(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initUI () {
        View decorView = this.getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        decorView.setSystemUiVisibility(uiOptions);
        findViewById(R.id.image_view_preview2).setVisibility(View.GONE);
        //((ImageView)findViewById(R.id.image_view_preview2)).setImageDrawable(getDrawable(R.drawable.capture_1_1));
        this.findViewById(R.id.image_view_render).setBackground(getDrawable(R.drawable.picture_9_16));
        setImageViewSize (1920, 1080, false);
        findViewById(R.id.image_view_preview).setBackground(getDrawable(R.drawable.picture_full));
        showBackPreview (false);
    }

    private void setViewListener () {
        this.findViewById(R.id.btn_3_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDefaultImageView (CommonDefine.ScreenSizeDefine.RATIO_3_4);
            }
        });

        this.findViewById(R.id.btn_9_16).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDefaultImageView (CommonDefine.ScreenSizeDefine.RATIO_9_16);
            }
        });

        this.findViewById(R.id.btn_1_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDefaultImageView (CommonDefine.ScreenSizeDefine.RATIO_1_1);
            }
        });

        this.findViewById(R.id.btn_full).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDefaultImageView (CommonDefine.ScreenSizeDefine.RATIO_FULL);
            }
        });

        this.findViewById(R.id.btn_original).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testConstraintLayout (v);
            }
        });
    }

    private void setDefaultImageView (int showRatio) {
        switch (showRatio) {
            case CommonDefine.ScreenSizeDefine.RATIO_3_4:
                setBlurRenderImageView (R.drawable.picture_3_4);
                setImageViewSize (1440, 1080, true);
                break;
            case CommonDefine.ScreenSizeDefine.RATIO_9_16:
                setBlurRenderImageView (R.drawable.picture_9_16);
                setImageViewSize (1920, 1080, true);
                break;
            case CommonDefine.ScreenSizeDefine.RATIO_1_1:
                setBlurRenderImageView (R.drawable.picture_full);
                setImageViewSize (1080, 1080, true);
                break;
            case CommonDefine.ScreenSizeDefine.RATIO_FULL:
                setBlurRenderImageView (R.drawable.picture_full);
                setImageViewSize (2400, 1080, true);
                break;
            default:
                this.findViewById(R.id.image_view_render).setBackground(getDrawable(R.drawable.picture_9_16));
                break;
        }
    }

    private void setImageViewSize(int previewWidth, int previewHeight, boolean bSetUI)
    {
        int margin = 0;
        if((previewWidth * 9 / previewHeight) == 16) {
            margin =  Utils.dip2px(this, + 64) + mStatusBarHeight;
        }else if(previewWidth * 3 / previewHeight == 4) {
            margin = Utils.dip2px(this, + 64) + mStatusBarHeight;
        }else if(previewWidth / previewHeight == 1) {
            margin = Utils.dip2px(this, + 120) + mStatusBarHeight;
        }
        Log.e(TAG, "margin = " + margin + ",mStatusBarHeight = " + mStatusBarHeight);
        Log.e(TAG, "previewHeight = " + previewHeight + ",previewWidth = " + previewWidth);

        int bootomNavHeight = Utils.getNavigationBarHeight(this);
        // set bottom margin
        Point point = Utils.getScreenRealSize(this);
        int bottomMargin = point.y - previewWidth - margin - bootomNavHeight;
        if (bottomMargin < 0) {
            bottomMargin = 0;
        }
        Log.e(TAG, "bottomMargin = " + bottomMargin + ",bootomNavHeight = " + bootomNavHeight);

        if (bSetUI) {
            //showForeground (true);
            // 快速改变preview image view的大小作为dst
            ConstraintSet setNewPreview = new ConstraintSet();
            ConstraintLayout rootLayout = findViewById(R.id.preview_root);
            setNewPreview.clone(rootLayout);
            TransitionManager.beginDelayedTransition(rootLayout);
            setNewPreview.connect(R.id.image_view_preview, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
            setNewPreview.connect(R.id.image_view_preview, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin);
            setNewPreview.connect(R.id.image_view_preview, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
            setNewPreview.connect(R.id.image_view_preview, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, bottomMargin);
            setNewPreview.constrainWidth(R.id.image_view_preview, ConstraintSet.MATCH_CONSTRAINT);
            setNewPreview.applyTo(rootLayout);

            showBackPreview(true);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(
                    ObjectAnimator.ofFloat(findViewById(R.id.image_view_render), "alpha", 1.f, 1.f),
                    ObjectAnimator.ofFloat(findViewById(R.id.image_view_render), "alpha", 1.f, 0.f),
                    ObjectAnimator.ofFloat(findViewById(R.id.image_view_preview), "alpha", 0.f, 1.f));
            animatorSet.setDuration(1500).start();

            // render即上层的image view渐变
            /*ConstraintSet setNew = new ConstraintSet();
            //ConstraintLayout rootLayout = findViewById(R.id.preview_root);
            setNew.clone(rootLayout);
            // 渐变
            TransitionManager.beginDelayedTransition(rootLayout);
            setNew.connect(R.id.image_view_render, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
            setNew.connect(R.id.image_view_render, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, margin);
            setNew.connect(R.id.image_view_render, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
            setNew.connect(R.id.image_view_render, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, bottomMargin);
            setNew.constrainWidth(R.id.image_view_render, ConstraintSet.MATCH_CONSTRAINT);
            setNew.applyTo(rootLayout);*/
            //showForeground (false);
        }
    }

    private void testConstraintLayout (View v) {
        Log.d(TAG, "testConstraintLayout");
        ConstraintLayout constraintLayout = findViewById(R.id.preview_root);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        TransitionManager.beginDelayedTransition(constraintLayout);
        constraintSet.connect(R.id.btn_original, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 500);
        constraintSet.applyTo(constraintLayout);
    }

    private void showForeground (boolean bShow) {
            this.findViewById(R.id.image_view_render).setVisibility(bShow?View.VISIBLE:View.INVISIBLE);
    }

    private void showBackPreview (boolean bShow) {
        this.findViewById(R.id.image_view_preview).setVisibility(bShow?View.VISIBLE:View.INVISIBLE);
    }

    private void setBlurRenderImageView (int resourceId) {
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), resourceId);
        bitmap = BlurBitmap.blur(this, bitmap);
        this.findViewById(R.id.image_view_render).setBackground(new BitmapDrawable(getResources(), bitmap));
    }
}
