package com.example.albumtest;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.example.utils.MyLog;
import java.util.ArrayList;
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

    private void initViewList () {
        MyLog.d(TAG, "initViewList");

        mImageIdList = new ArrayList<>();
        mImageIdList.add(R.drawable.picture_3_4);
        mImageIdList.add(R.drawable.picture_9_16);
        mImageIdList.add(R.drawable.picture_full);

        mImageViewArrayList = new ArrayList<>();
        for (int i = 0; i < mImageIdList.size(); ++i) {
            ImageView iv = new ImageView(this);
            iv.setImageResource(mImageIdList.get(i));
            mImageViewArrayList.add(iv);
        }

        mPagerAdapter = new MyPagerAdapter(this, mImageViewArrayList);
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

        @Override
        public int getCount() {
            return ivGoodsList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public ImageView instantiateItem(ViewGroup container, int position) {
            ImageView imageView=ivGoodsList.get(position);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ImageView imageView=(ImageView)object;
            container.removeView(imageView);
        }
    }
}
