package com.example.blurrytest;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

class Utils {

    static public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    static public int px2dip(Context context,float pxValue) {
        final float scale =context.getResources().getDisplayMetrics().density;
        return(int) (pxValue / scale + 0.5f);
    }

    static public Point getScreenRealSize(Activity activity) {
        WindowManager m = activity.getWindowManager();
        Display d = m.getDefaultDisplay();
        Point sz = new Point();
        d.getRealSize(sz);
        return sz;
    }

    // 计算顶部状态栏高度
    static public int getStatusBarHeight(Activity activity) {
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen","android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }
    // 计算底部导航栏高度
    static public int getNavigationBarHeight(Activity activity) {
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height","dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        Log.v("dbw", "Navi height:" + height);
        return height;
    }
}
