package com.lease.fw.ui.utils;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class LightStatusBarUtils {

    public static void setLightStatusBar(Activity activity) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
//            Window window = activity.getWindow();
//            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            //设置状态栏颜色
//            window.setStatusBarColor(activity.getResources().getColor(R.color.color_3a3e4a));

        }
    }
}