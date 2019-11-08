package com.lease.fw.ui.config;

import com.lease.fw.ui.toast.ToastUtil;

/**
 * created time: 2019-11-08
 * author: cqt
 * description:
 */
public class ToastConfig {

    private ToastUtil.Mode mode;

    private int gravity;

    private int customResId;

    private boolean isCustomRes;

    private ToastConfig(Builder builder) {
        setMode(builder.mode);
        setGravity(builder.gravity);
        setCustomResId(builder.customResId);
        setCustomRes(builder.isCustomRes);
    }

    public void setMode(ToastUtil.Mode mode) {
        this.mode = mode;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    public void setCustomResId(int customResId) {
        this.customResId = customResId;
    }

    public void setCustomRes(boolean customRes) {
        isCustomRes = customRes;
    }

    public ToastUtil.Mode getMode() {
        return mode;
    }

    public int getGravity() {
        return gravity;
    }

    public int getCustomResId() {
        return customResId;
    }

    public boolean isCustomRes() {
        return isCustomRes;
    }

    public static final class Builder {
        private ToastUtil.Mode mode;
        private int gravity;
        private int customResId;
        private boolean isCustomRes;

        public Builder() {
        }

        public Builder mode(ToastUtil.Mode val) {
            mode = val;
            return this;
        }

        public Builder gravity(int val) {
            gravity = val;
            return this;
        }

        public Builder customResId(int val) {
            customResId = val;
            return this;
        }

        public Builder isCustomRes(boolean val) {
            isCustomRes = val;
            return this;
        }

        public ToastConfig build() {
            return new ToastConfig(this);
        }
    }
}
