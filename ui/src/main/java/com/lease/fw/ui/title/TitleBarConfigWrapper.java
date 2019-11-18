package com.lease.fw.ui.title;

import android.arch.lifecycle.MutableLiveData;

import com.lease.fw.ui.config.MenuAction;
import com.lease.fw.ui.config.TitleBarConfig;

import java.util.List;

public class TitleBarConfigWrapper {

    private TitleBarConfig titleBarConfig;

    private MutableLiveData<TitleBarConfig> mutableLiveData;

    public TitleBarConfigWrapper(TitleBarConfig config, MutableLiveData<TitleBarConfig> mutableLiveData) {
        this.titleBarConfig = config;
        this.mutableLiveData = mutableLiveData;
    }

    public void show(int show) {
        titleBarConfig.setShow(show);
        update();
    }

    public void setBackIcon(int backIcon) {
        titleBarConfig.setBackIcon(backIcon);
        update();
    }

    public void setBackShow(int backShow) {
        titleBarConfig.setBackShow(backShow);
        update();
    }

    public void setTitleBarBackgroundColor(int titleBarBackgroundColor) {
        titleBarConfig.setTitleBarBackgroundColor(titleBarBackgroundColor);
        update();
    }

    public void setTitleTextSize(int titleTextSize) {
        titleBarConfig.setTitleTextSize(titleTextSize);
        update();
    }

    public void setTitleTextColor(int titleTextColor) {
        titleBarConfig.setTitleTextColor(titleTextColor);
        update();
    }

    public void setTitleTextGravity(int titleTextGravity) {
        titleBarConfig.setTitleTextGravity(titleTextGravity);
        update();
    }

    public void setActionTextSize(int actionTextSize) {
        titleBarConfig.setActionTextSize(actionTextSize);
        update();
    }

    public void setActionTextColor(int actionTextColor) {
        titleBarConfig.setActionTextColor(actionTextColor);
        update();
    }

    public void setTitleText(String titleText) {
        titleBarConfig.setTitleText(titleText);
        update();
    }

    public void setActions(List<MenuAction> actions) {
        titleBarConfig.setActions(actions);
        update();
    }

    private void update() {
        mutableLiveData.setValue(titleBarConfig);
    }
}
