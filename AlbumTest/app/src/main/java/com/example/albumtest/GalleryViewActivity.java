package com.example.albumtest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.utils.MyFileUtils;
import com.example.utils.MyLog;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class GalleryViewActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getName();
    private ViewPager mViewPager = null;
    private MyPagerAdapter mPagerAdapter = null;
    private ActionBar mActionBar = null;
    private String mPictureFolder = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_view);
        mPictureFolder = getIntent().getStringExtra("folder");
        MyLog.d(TAG, "onCreate mPictureFolder = " + mPictureFolder);
        initUI();
    }

    private void initUI () {
        MyLog.d(TAG, "initUI");
        setNavigationColor ();
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
        mViewPager = findViewById(R.id.gallery_view_pager);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPictureFolder != null) {
            String folderPath = Environment.getExternalStorageDirectory().getPath() + mPictureFolder;
            boolean bOk = initViewList(this, folderPath);
            if (!bOk) {
                Toast.makeText(this, "initViewList failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setSystemUIVisible(boolean show) {
        if (show) {
            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            uiFlags |= 0x00001000;
            getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        } else {
            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            uiFlags |= 0x00001000;
            getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        }
    }

    private void setNavigationColor () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//设置透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);//设置透明导航栏
        }
    }

    private boolean initViewList (Context context, String folderPath) {
        MyLog.d(TAG, "initViewList folderPath = " + folderPath);
        mPagerAdapter = new MyPagerAdapter(context, folderPath);
        if (mPagerAdapter.mPictureFileList.size() <= 0) {
            return false;
        }
        mViewPager.setAdapter(mPagerAdapter);
        String firstFileTime = getFileModifyTime(mPagerAdapter.mPictureFileList.get(0));
        mActionBar.setTitle(firstFileTime);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                MyLog.d(TAG, "onPageSelected position = " + position);
                File file = mPagerAdapter.mPictureFileList.get(position);
                String modifyTime = getFileModifyTime (file);
                mActionBar.setTitle(modifyTime);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return true;
    }

    private String getFileModifyTime (File file) {
        String modifyTime = null;
        if (file != null) {
            long time= file.lastModified();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy年M月d日");
            modifyTime = formatter.format(time);
        }
        return modifyTime;
    }

    protected class MyPagerAdapter extends PagerAdapter {

        Context context;
        List<File> mPictureFileList = null;
        public MyPagerAdapter(Context context, String picturePath) {
            this.context = context;
            mPictureFileList = MyFileUtils.listFileSortByModifyTime(picturePath);
        }

        @Override
        public int getCount() {
            return mPictureFileList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public ImageView instantiateItem(ViewGroup container, int position) {
            MyLog.d(TAG, "instantiateItem position = " + position);
            File file = mPictureFileList.get(position);
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            ImageView imageView = new ImageView(this.context);
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                container.addView(imageView);
            }
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ImageView imageView=(ImageView)object;
            container.removeView(imageView);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        MyLog.d(TAG, "onWindowFocusChanged hasFocus = " + hasFocus);
        super.onWindowFocusChanged(hasFocus);
        //NavigationBarStatusBar (this, hasFocus);
    }

    public static void NavigationBarStatusBar(Activity activity, boolean hasFocus){
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = activity.getWindow().getDecorView();
            if (hasFocus) {
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE);
            } else {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
        }
    }


    private void hideNavigationBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void showNavigationBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void setStatusBarVisible(boolean show) {
        if (show) {
            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            uiFlags |= 0x00001000;
            getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        } else {
            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            uiFlags |= 0x00001000;
            getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        }
    }


}
