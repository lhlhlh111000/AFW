package com.lease.fw.ui.config;


import android.graphics.Color;
import android.view.Gravity;
import android.view.View;

import com.lease.fw.ui.R;


public class DefaultTitleBarConfig extends TitleBarConfig {

    public DefaultTitleBarConfig() {
        setBackIcon(R.mipmap.ui_ic_back);
        setBackShow(View.VISIBLE);
        setTitleBarBackgroundColor(Color.WHITE);
        setTitleTextSize(16);
        setTitleTextColor(Color.BLACK);
        setTitleTextGravity(Gravity.CENTER);
        setActionTextSize(14);
        setActionTextColor(Color.BLACK);
    }
}