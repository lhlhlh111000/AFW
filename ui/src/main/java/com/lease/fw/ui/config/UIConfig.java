package com.lease.fw.ui.config;

/**
 * ui配置信息
 */
public class UIConfig {

    private TitleBarConfig titleBarConfig;

    private StatusBarConfig statusBarConfig;

    private LoadingDialogConfig loadingDialogConfig;

    private ToastConfig toastConfig;

    private UIConfig(Builder builder) {
        setTitleBarConfig(builder.titleBarConfig);
        setStatusBarConfig(builder.statusBarConfig);
        setLoadingDialogConfig(builder.loadingDialogConfig);
        setToastConfig(builder.toastConfig);
    }

    public TitleBarConfig getTitleBarConfig() {
        if(null == titleBarConfig) {
            return null;
        }
        return new TitleBarConfig(titleBarConfig);
    }

    public void setTitleBarConfig(TitleBarConfig titleBarConfig) {
        this.titleBarConfig = titleBarConfig;
    }

    public StatusBarConfig getStatusBarConfig() {
        return statusBarConfig;
    }

    public void setStatusBarConfig(StatusBarConfig statusBarConfig) {
        this.statusBarConfig = statusBarConfig;
    }

    public LoadingDialogConfig getLoadingDialogConfig() {
        return loadingDialogConfig;
    }

    public void setLoadingDialogConfig(LoadingDialogConfig loadingDialogConfig) {
        this.loadingDialogConfig = loadingDialogConfig;
    }

    public ToastConfig getToastConfig() {
        return toastConfig;
    }

    public void setToastConfig(ToastConfig toastConfig) {
        this.toastConfig = toastConfig;
    }

    public static final class Builder {
        private TitleBarConfig titleBarConfig;
        private StatusBarConfig statusBarConfig;
        private LoadingDialogConfig loadingDialogConfig;
        private ToastConfig toastConfig;

        public Builder() {
        }

        public Builder titleBarConfig(TitleBarConfig val) {
            titleBarConfig = val;
            return this;
        }

        public Builder statusBarConfig(StatusBarConfig val) {
            statusBarConfig = val;
            return this;
        }

        public Builder loadingDialogConfig(LoadingDialogConfig val) {
            loadingDialogConfig = val;
            return this;
        }

        public Builder toastConfig(ToastConfig val) {
            toastConfig = val;
            return this;
        }

        public UIConfig build() {
            return new UIConfig(this);
        }
    }
}