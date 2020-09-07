package com.example.albumtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.utils.MyLog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class GalleryViewActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getName();

    private ViewPager mViewPager = null;
    ArrayList<Integer> mImageIdList = null;
    ArrayList<ImageView> mImageViewArrayList = null;
    MyPagerAdapter mPagerAdapter = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_view);
        initUI();
    }

    private void initUI () {
        MyLog.d(TAG, "initUI");
        // hide action bar
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        //setSystemUIVisible (false);
        setNavigationColor ();

        findViewById(R.id.gallery_btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.BLACK);
            //getWindow().setNavigationBarColor(getResources().getColor(R.color.black));
            //getWindow().setNavigationBarColor(Color.BLUE);
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
        mPagerAdapter = new MyPagerAdapter(this, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath());
        mViewPager = findViewById(R.id.gallery_view_pager);
        mViewPager.setAdapter(mPagerAdapter);
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
                    if (file.lastModified() < newFile.lastModified()) {
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
