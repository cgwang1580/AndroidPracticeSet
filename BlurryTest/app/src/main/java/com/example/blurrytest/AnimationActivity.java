package com.example.blurrytest;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

public class AnimationActivity extends AppCompatActivity {

    private final static String TAG = "AnimationActivity";
    private ImageView mImageViewPreview = null;
    private ImageView mImageViewCam = null;
    private ImageView mImageViewPreviewUp = null;
    private ImageView mImageViewPreviewDown = null;

    private int mStatusBarHeight = 0;
    private int mNavigateBarHeight = 0;

    private int mSrcRenderWidth = 0;
    private int mSrcRenderHeight = 0;

    private long mCurrentTime = 0;
    private Handler mHandler = null;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);
        initUI ();
        initListener();
        mHandler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case CommonDefine.HandleMessageDefine.MESSAGE_HIDE_RENDER:
                        findViewById(R.id.image_view_preview).setVisibility(View.INVISIBLE);
                        updateRenderSize(0, 0, 0, mSrcRenderWidth, mSrcRenderHeight);
                        break;
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
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

        mStatusBarHeight = Utils.getStatusBarHeight(this);
        mNavigateBarHeight = Utils.getNavigationBarHeight(this);

        mImageViewPreview = this.findViewById(R.id.image_view_preview);
        mImageViewCam = this.findViewById(R.id.image_view_cam);
        mImageViewPreviewUp = this.findViewById(R.id.image_view_preview_up);
        mImageViewPreviewDown = this.findViewById(R.id.image_view_preview_down);

        mImageViewPreview.setVisibility(View.INVISIBLE);
        mImageViewPreviewUp.setVisibility(View.INVISIBLE);
        mImageViewPreviewDown.setVisibility(View.INVISIBLE);
        mImageViewCam.setBackground(getDrawable(R.drawable.picture_full));
        mImageViewCam.setVisibility(View.VISIBLE);

        setOriginalView ();
    }

    private void setOriginalView () {
        mImageViewPreview.setVisibility(View.INVISIBLE);
        mImageViewPreviewUp.setVisibility(View.INVISIBLE);
        mImageViewPreviewDown.setVisibility(View.INVISIBLE);
        mImageViewCam.setBackground(getDrawable(R.drawable.picture_full));
        mImageViewCam.setVisibility(View.VISIBLE);

        doScreenChange(CommonDefine.ScreenSizeDefine.RATIO_9_16, 0,0, 1920, 1080);
        mSrcRenderWidth = 1920;
        mSrcRenderHeight = 1080;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1200);
                    Message msg = new Message();
                    Log.d(TAG, "setOriginalView MESSAGE_HIDE_RENDER");
                    msg.what = CommonDefine.HandleMessageDefine.MESSAGE_HIDE_RENDER;
                    mHandler.sendMessage (msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initListener () {
        findViewById(R.id.btn_3_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchScreenSize(mImageViewPreview, CommonDefine.ScreenSizeDefine.RATIO_3_4);
            }
        });

        findViewById(R.id.btn_9_16).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchScreenSize(mImageViewPreview, CommonDefine.ScreenSizeDefine.RATIO_9_16);
            }
        });

        findViewById(R.id.btn_1_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchScreenSize(mImageViewPreview, CommonDefine.ScreenSizeDefine.RATIO_1_1);
            }
        });

        findViewById(R.id.btn_full).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchScreenSize(mImageViewPreview, CommonDefine.ScreenSizeDefine.RATIO_FULL);
            }
        });

        findViewById(R.id.btn_original).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOriginalView();
            }
        });
    }

    private void switchScreenSize (View view, int ratioCase) {
        Bitmap bitmap = null;
        switch (ratioCase) {
            case CommonDefine.ScreenSizeDefine.RATIO_3_4:
                bitmap = getBlurRenderImageView(R.drawable.picture_3_4);
                view.setBackground(new BitmapDrawable(getResources(), bitmap));
                view.setVisibility(View.VISIBLE);
                doScreenChange (ratioCase, mSrcRenderWidth, mSrcRenderHeight, 1280, 960);
                mSrcRenderWidth = 1280;
                mSrcRenderHeight = 960;
                break;
            case CommonDefine.ScreenSizeDefine.RATIO_9_16:
                bitmap = getBlurRenderImageView(R.drawable.picture_9_16);
                view.setBackground(new BitmapDrawable(getResources(), bitmap));
                view.setVisibility(View.VISIBLE);
                doScreenChange (ratioCase, mSrcRenderWidth, mSrcRenderHeight, 1920, 1080);
                mSrcRenderWidth = 1920;
                mSrcRenderHeight = 1080;
                break;
            case CommonDefine.ScreenSizeDefine.RATIO_1_1:
                bitmap = getBlurRenderImageView(R.drawable.picture_full);
                view.setBackground(new BitmapDrawable(getResources(), bitmap));
                view.setVisibility(View.VISIBLE);
                doScreenChange (ratioCase, mSrcRenderWidth, mSrcRenderHeight, 2448, 2448);
                mSrcRenderWidth = 2448;
                mSrcRenderHeight = 2448;
                break;
            case CommonDefine.ScreenSizeDefine.RATIO_FULL:
                bitmap = getBlurRenderImageView(R.drawable.picture_full);
                view.setBackground(new BitmapDrawable(getResources(), bitmap));
                view.setVisibility(View.VISIBLE);
                doScreenChange (ratioCase, mSrcRenderWidth, mSrcRenderHeight, 1920, 864);
                mSrcRenderWidth = 1920;
                mSrcRenderHeight = 864;
                break;
            default:
                break;
        }
        //mImageViewPreviewUp.setVisibility(View.VISIBLE);
        //mImageViewPreviewDown.setVisibility(View.VISIBLE);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(findViewById(R.id.image_view_preview), "alpha", 1.f, 0.8f, 0.7f, 0.f),
                ObjectAnimator.ofFloat(findViewById(R.id.image_view_cam), "alpha", 0.f, 0.1f, 0.2f, 1.f));
        animatorSet.setDuration(700).start();

        mCurrentTime = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1200);
                    Message msg = new Message();
                    msg.what = CommonDefine.HandleMessageDefine.MESSAGE_HIDE_RENDER;
                    mHandler.sendMessage (msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Bitmap getBlurRenderImageView (int resourceId) {
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), resourceId);
        bitmap = BlurBitmap.blur(this, bitmap);
        return bitmap;
    }

    private void doScreenChange (int screenRatio, int srcWidth, int srcHeight, int dstWidth, int dstHeight) {
        Log.d(TAG, "doScreenChange dstWidth = " + dstWidth + ", dstHeight = " + dstHeight);
        Point point = Utils.getScreenRealSize(this);
        int ScreenHeight = point.y;
        int ScreenWidth = point.x;

        //updateRenderSize (screenRatio, srcWidth, srcHeight, dstWidth, dstHeight);

        if (dstHeight > ScreenWidth) {
            float oriRatio2 = 1.f * dstWidth / dstHeight;
            dstHeight = ScreenWidth;
            dstWidth = (int)(oriRatio2 * dstHeight);
        }
        int dstMargin = 0;
        int img2ScreenHeight = (int)(1.f * ScreenWidth/dstHeight * dstWidth);
        switch (screenRatio) {
            case CommonDefine.ScreenSizeDefine.RATIO_3_4:
            case CommonDefine.ScreenSizeDefine.RATIO_9_16:
                dstMargin = Utils.dip2px(this, + 64) + mStatusBarHeight;
                break;
            case CommonDefine.ScreenSizeDefine.RATIO_1_1:
                dstMargin = Utils.dip2px(this, + 120) + mStatusBarHeight;
                break;
            case CommonDefine.ScreenSizeDefine.RATIO_FULL:
                dstMargin = 0;
                break;
            default:
                break;
        }
        int dstBottomMargin = ScreenHeight - dstMargin - img2ScreenHeight - mNavigateBarHeight;
        if (dstBottomMargin < 0) dstBottomMargin = 0;

        Log.d(TAG, "doScreenChange mStatusBarHeight = " + mStatusBarHeight + ", mNavigateBarHeight = " + mNavigateBarHeight);
        Log.d(TAG, "doScreenChange margin = " + dstMargin + ", bottomMargin = " + dstBottomMargin);
        ConstraintSet newSet = new ConstraintSet();
        ConstraintLayout rootLayout1 = findViewById(R.id.animation_root);
        newSet.clone(rootLayout1);
        TransitionManager.beginDelayedTransition(rootLayout1);

        newSet.connect(R.id.image_view_cam, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
        newSet.connect(R.id.image_view_cam, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, dstMargin);
        newSet.connect(R.id.image_view_cam, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
        newSet.connect(R.id.image_view_cam, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, dstBottomMargin);
        newSet.constrainWidth(R.id.image_view_cam, ConstraintSet.MATCH_CONSTRAINT);
        newSet.constrainHeight(R.id.image_view_cam, ConstraintSet.MATCH_CONSTRAINT);

        newSet.applyTo(rootLayout1);
    }

    private void updateRenderSize (int screenRatio, int srcWidth, int srcHeight, int dstWidth, int dstHeight) {

        Log.d(TAG, "updateRenderSize dstWidth = " + dstWidth + ", dstHeight = " + dstHeight);
        Point point = Utils.getScreenRealSize(this);
        int ScreenHeight = point.y;
        int ScreenWidth = point.x;
        float srcOriRatio = 1.f * dstWidth / dstHeight;
        if (dstHeight > ScreenWidth) {
            dstHeight = ScreenWidth;
            dstWidth = (int)(srcOriRatio * dstHeight);
        }
        int srcMargin = 0;
        int srcImg2ScreenHeight = (int)(1.f * ScreenWidth/dstHeight * dstWidth);
        if (srcOriRatio == 4.f/3 || srcOriRatio == 16.f/9) {
            srcMargin = Utils.dip2px(this, + 64) + mStatusBarHeight;
        } else if (srcOriRatio == 1.f) {
            srcMargin = Utils.dip2px(this, + 120) + mStatusBarHeight;
        }
        int srcBottomMargin = ScreenHeight - srcMargin - srcImg2ScreenHeight - mNavigateBarHeight;
        Log.d(TAG, "updateRenderSize srcMargin = " + srcMargin + ", srcBottomMargin = " + srcBottomMargin);
        ConstraintSet srcNewSet = new ConstraintSet();
        ConstraintLayout rootLayout = findViewById(R.id.animation_root);
        srcNewSet.clone(rootLayout);
        srcNewSet.connect(R.id.image_view_preview, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
        srcNewSet.connect(R.id.image_view_preview, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, srcMargin);
        srcNewSet.connect(R.id.image_view_preview, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
        srcNewSet.connect(R.id.image_view_preview, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, srcBottomMargin);
        srcNewSet.constrainWidth(R.id.image_view_preview, ConstraintSet.MATCH_CONSTRAINT);
        srcNewSet.constrainHeight(R.id.image_view_preview, ConstraintSet.MATCH_CONSTRAINT);
        srcNewSet.applyTo(rootLayout);
    }
}
