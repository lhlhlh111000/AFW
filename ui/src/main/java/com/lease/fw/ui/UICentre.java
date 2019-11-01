package com.lease.fw.ui;

import android.content.Context;

import com.lease.fw.ui.config.DefaultTitleBarConfig;
import com.lease.fw.ui.config.LoadingDialogConfig;
import com.lease.fw.ui.config.StatusBarConfig;
import com.lease.fw.ui.config.TitleBarConfig;
import com.lease.fw.ui.config.UIConfig;

/**
 * ui中心，可对全局ui做部分定制化操作
 */
public class UICentre {

    private static UICentre sInstance;

    private UICentre() {
    }

    public static UICentre getInstance() {
        if(null == sInstance) {
            synchronized (UICentre.class) {
                if(null == sInstance) {
                    sInstance = new UICentre();
                }
            }
        }

        return sInstance;
    }

    private UIConfig uiConfig;

    public void setUiConfig(UIConfig uiConfig) {
        this.uiConfig = uiConfig;
    }

    public UIConfig getUiConfig() {
        if(null == this.uiConfig) {
            UIConfig.Builder builder = new UIConfig.Builder();
            builder.titleBarConfig(buildDefaultTitleBarConfig());
            builder.statusBarConfig(buildDefaultStatusBarConfig());
            builder.loadingDialogConfig(buildDefaultLoadingDialogConfig());
            this.uiConfig = builder.build();
        }
        if(null == this.uiConfig.getTitleBarConfig()) {
            this.uiConfig.setTitleBarConfig(buildDefaultTitleBarConfig());
        }
        if(null == this.uiConfig.getStatusBarConfig()) {
            this.uiConfig.setStatusBarConfig(buildDefaultStatusBarConfig());
        }
        if(null == this.uiConfig.getLoadingDialogConfig()) {
            this.uiConfig.setLoadingDialogConfig(buildDefaultLoadingDialogConfig());
        }
        return this.uiConfig;
    }

    private TitleBarConfig buildDefaultTitleBarConfig() {
        return new DefaultTitleBarConfig();
    }

    private StatusBarConfig buildDefaultStatusBarConfig() {
        return new StatusBarConfig.Builder()
                .isLightStatus(true).build();
    }

    private LoadingDialogConfig buildDefaultLoadingDialogConfig() {
        return new LoadingDialogConfig(){
            @Override
            public void showLoadingDialog(Context context, String title) {
                // Do nothing
            }

            @Override
            public void dismissDialog() {
                // Do nothing
            }
        };
    }
}