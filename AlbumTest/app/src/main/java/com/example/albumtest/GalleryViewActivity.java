package com.example.albumtest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ActionMenuView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.utils.MyLog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class GalleryViewActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getName();

    private ViewPager mViewPager = null;
    private ArrayList<Integer> mImageIdList = null;
    private ArrayList<ImageView> mImageViewArrayList = null;
    private MyPagerAdapter mPagerAdapter = null;
    private boolean bShowStatusAndNavigationBar = true;
    private ActionBar mActionBar = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_view);
        initUI();
    }

    private void initUI () {
        MyLog.d(TAG, "initUI");

        // hide action bar
        /*if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }*/
        //setSystemUIVisible (false);
        //Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setNavigationColor ();
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }

        /*findViewById(R.id.gallery_btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });*/
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        initViewList();

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
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.BLACK);
            //getWindow().setNavigationBarColor(getResources().getColor(R.color.black));
            //getWindow().setNavigationBarColor(Color.BLUE);
        }*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//设置透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);//设置透明导航栏
        }
    }

    private void initViewList () {
        MyLog.d(TAG, "initViewList");

        /*mImageIdList = new ArrayList<>();
        mImageIdList.add(R.drawable.picture_3_4);
        mImageIdList.add(R.drawable.picture_9_16);
        mImageIdList.add(R.drawable.picture_full);
        mImageIdList.add(R.drawable.capture_1080x1080);
        mImageIdList.add(R.drawable.capture_1080x1440);
        mImageIdList.add(R.drawable.capture_1080x2400);
        mImageIdList.add(R.drawable.capture__1080x1920);

        mImageViewArrayList = new ArrayList<>();
        for (int i = 0; i < mImageIdList.size(); ++i) {
            ImageView iv = new ImageView(this);
            iv.setImageResource(mImageIdList.get(i));
            mImageViewArrayList.add(iv);
        }
        mPagerAdapter = new MyPagerAdapter(this, mImageViewArrayList);*/
        mPagerAdapter = new MyPagerAdapter(this, Environment.getExternalStorageDirectory().getPath()
                + "OpenGLESTest");
        mViewPager = findViewById(R.id.gallery_view_pager);
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
        ArrayList<ImageView> ivGoodsList;
        public MyPagerAdapter(Context context, ArrayList<ImageView>ivList){
            this.context=context;
            this.ivGoodsList=ivList;
        }
        List<File> mPictureFileList = null;
        public MyPagerAdapter(Context context, String picturePath) {
            this.context = context;
            mPictureFileList = listFileSortByModifyTime(picturePath);
        }

        @Override
        public int getCount() {
            //return ivGoodsList.size();
            return mPictureFileList.size();
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public ImageView instantiateItem(ViewGroup container, int position) {
            MyLog.d(TAG, "instantiateItem position = " + position);
            //ImageView imageView=ivGoodsList.get(position);
            File file = mPictureFileList.get(position);
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            ImageView imageView = new ImageView(this.context);
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            if (bitmap != null) {
                //imageView.setBackground(new BitmapDrawable(context.getResources(), bitmap));
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

    private View.OnClickListener mViewPageOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //setStatusBarVisible (bShowStatusAndNavigationBar);
            //NavigationBarStatusBar (GalleryViewActivity.this, bShowStatusAndNavigationBar);
            //bShowStatusAndNavigationBar = !bShowStatusAndNavigationBar;
        }
    };

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

    //按时间对文件名进行排序
    private List<File> listFileSortByModifyTime(String path) {
        List<File> list = getFiles(path);
        if (list != null && list.size() > 0) {
            Collections.sort(list, new Comparator<File>() {
                public int compare(File file, File newFile) {
                    if (file.lastModified() > newFile.lastModified()) {
                        return -1;
                    } else if (file.lastModified() == newFile.lastModified()) {
                        return 0;
                    } else {
                        return 1;
                    }
                }
            });
        }
        return list;
    }
    //获取文件夹下的文件
    private static List<File> getFiles (String realPath) {
        List<File> files = new ArrayList<File>();
        File realFile = new File(realPath);
        if (realFile.isDirectory()) {
            File[] subFiles = realFile.listFiles();
            for (File file : subFiles) {
                if (file.isDirectory()) {

                } else {
                    files.add(file);
                }
            }
        }
        return files;
    }
}
