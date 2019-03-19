package com.lease.fw.ui.config;

/**
 * ui配置信息
 */
public class UIConfig {

    private TitleBarConfig titleBarConfig;

    private LifecycleConfig lifecycleConfig;

    private StatusBarConfig statusBarConfig;

    private UIConfig(Builder builder) {
        setTitleBarConfig(builder.titleBarConfig);
        setLifecycleConfig(builder.lifecycleConfig);
        setStatusBarConfig(builder.statusBarConfig);
    }

    public TitleBarConfig getTitleBarConfig() {
        return titleBarConfig;
    }

    public void setTitleBarConfig(TitleBarConfig titleBarConfig) {
        this.titleBarConfig = titleBarConfig;
    }

    public LifecycleConfig getLifecycleConfig() {
        return lifecycleConfig;
    }

    public void setLifecycleConfig(LifecycleConfig lifecycleConfig) {
        this.lifecycleConfig = lifecycleConfig;
    }

    public StatusBarConfig getStatusBarConfig() {
        return statusBarConfig;
    }

    public void setStatusBarConfig(StatusBarConfig statusBarConfig) {
        this.statusBarConfig = statusBarConfig;
    }

    public static final class Builder {
        private TitleBarConfig titleBarConfig;
        private LifecycleConfig lifecycleConfig;
        private StatusBarConfig statusBarConfig;

        public Builder() {
        }

        public Builder titleBarConfig(TitleBarConfig val) {
            titleBarConfig = val;
            return this;
        }

        public Builder lifecycleConfig(LifecycleConfig val) {
            lifecycleConfig = val;
            return this;
        }

        public Builder statusBarConfig(StatusBarConfig val) {
            statusBarConfig = val;
            return this;
        }

        public UIConfig build() {
            return new UIConfig(this);
        }
    }
}