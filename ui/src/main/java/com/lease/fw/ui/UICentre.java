package com.lease.fw.ui;

import com.lease.fw.ui.config.DefaultTitleBarConfig;
import com.lease.fw.ui.config.LifecycleConfig;
import com.lease.fw.ui.config.LifecycleObserver;
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
            builder.lifecycleConfig(buildDefaultLifecycleConfig());
            builder.statusBarConfig(buildDefaultStatusBarConfig());
            this.uiConfig = builder.build();
        }
        if(null == this.uiConfig.getTitleBarConfig()) {
            this.uiConfig.setTitleBarConfig(buildDefaultTitleBarConfig());
        }
        if(null == this.uiConfig.getLifecycleConfig()) {
            this.uiConfig.setLifecycleConfig(buildDefaultLifecycleConfig());
        }
        if(null == this.uiConfig.getStatusBarConfig()) {
            this.uiConfig.setStatusBarConfig(buildDefaultStatusBarConfig());
        }
        return this.uiConfig;
    }

    private TitleBarConfig buildDefaultTitleBarConfig() {
        return new DefaultTitleBarConfig();
    }

    private LifecycleConfig buildDefaultLifecycleConfig() {
        LifecycleConfig lifecycleConfig = new LifecycleConfig();
        lifecycleConfig.addObserver(new LifecycleObserver() {
            @Override
            public void onCreate(Class pageClass) {

            }

            @Override
            public void onStart(Class pageClass) {

            }

            @Override
            public void onResume(Class pageClass) {

            }

            @Override
            public void onStop(Class pageClass) {

            }

            @Override
            public void onPause(Class pageClass) {

            }

            @Override
            public void onDestroy(Class pageClass) {

            }
        });
        return lifecycleConfig;
    }

    private StatusBarConfig buildDefaultStatusBarConfig() {
        return new StatusBarConfig.Builder()
                .isLightStatus(true).build();
    }
}