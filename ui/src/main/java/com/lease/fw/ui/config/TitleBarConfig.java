package com.lease.fw.ui.config;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 标题栏配置信息
 */
public class TitleBarConfig {

    private int show = View.VISIBLE; // 标题栏是否可见

    private int backIcon; // 返回按钮

    private int backShow = View.VISIBLE; // 返回按钮是否可见

    private int titleBarBackgroundColor; // 标题栏背景色

    private String titleText; // 标题

    private int titleTextSize; // 标题文字大小

    private int titleTextColor; // 标题文字颜色

    private int titleTextGravity; // 标题文字位置 - gravity.center - gravity.left

    private int actionTextSize; // 右侧菜单文字大小

    private int actionTextColor; // 右侧菜单文字颜色

    private List<MenuAction> actions = new ArrayList<>(); // 右侧菜单栏，不建议大于2个

    public TitleBarConfig() {}

    public TitleBarConfig(TitleBarConfig builder) {
        setShow(builder.show);
        setBackIcon(builder.backIcon);
        setBackShow(builder.backShow);
        setTitleBarBackgroundColor(builder.titleBarBackgroundColor);
        setTitleText(builder.titleText);
        setTitleTextSize(builder.titleTextSize);
        setTitleTextColor(builder.titleTextColor);
        setTitleTextGravity(builder.titleTextGravity);
        setActionTextSize(builder.actionTextSize);
        setActionTextColor(builder.actionTextColor);
        setActions(builder.actions);
    }

    private TitleBarConfig(Builder builder) {
        setShow(builder.show);
        setBackIcon(builder.backIcon);
        setBackShow(builder.backShow);
        setTitleBarBackgroundColor(builder.titleBarBackgroundColor);
        setTitleText(builder.titleText);
        setTitleTextSize(builder.titleTextSize);
        setTitleTextColor(builder.titleTextColor);
        setTitleTextGravity(builder.titleTextGravity);
        setActionTextSize(builder.actionTextSize);
        setActionTextColor(builder.actionTextColor);
        setActions(builder.actions);
    }

    public int getBackIcon() {
        return backIcon;
    }

    public void setBackIcon(int backIcon) {
        this.backIcon = backIcon;
    }

    public int getBackShow() {
        return backShow;
    }

    public void setBackShow(int backShow) {
        this.backShow = backShow;
    }

    public int getTitleBarBackgroundColor() {
        return titleBarBackgroundColor;
    }

    public void setTitleBarBackgroundColor(int titleBarBackgroundColor) {
        this.titleBarBackgroundColor = titleBarBackgroundColor;
    }

    public int getTitleTextSize() {
        return titleTextSize;
    }

    public void setTitleTextSize(int titleTextSize) {
        this.titleTextSize = titleTextSize;
    }

    public int getTitleTextColor() {
        return titleTextColor;
    }

    public void setTitleTextColor(int titleTextColor) {
        this.titleTextColor = titleTextColor;
    }

    public int getTitleTextGravity() {
        return titleTextGravity;
    }

    public void setTitleTextGravity(int titleTextGravity) {
        this.titleTextGravity = titleTextGravity;
    }

    public int getActionTextSize() {
        return actionTextSize;
    }

    public void setActionTextSize(int actionTextSize) {
        this.actionTextSize = actionTextSize;
    }

    public int getActionTextColor() {
        return actionTextColor;
    }

    public void setActionTextColor(int actionTextColor) {
        this.actionTextColor = actionTextColor;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public List<MenuAction> getActions() {
        return actions;
    }

    public void setActions(List<MenuAction> actions) {
        this.actions = actions;
    }

    public int getShow() {
        return show;
    }

    public void setShow(int show) {
        this.show = show;
    }

    public static final class Builder {
        private int backIcon;
        private int backShow;
        private int titleBarBackgroundColor;
        private String titleText;
        private int titleTextSize;
        private int titleTextColor;
        private int titleTextGravity;
        private int actionTextSize;
        private int actionTextColor;
        private List<MenuAction> actions;
        private int show;

        public Builder() {
        }

        public Builder backIcon(int val) {
            backIcon = val;
            return this;
        }

        public Builder backShow(int val) {
            backShow = val;
            return this;
        }

        public Builder titleBarBackgroundColor(int val) {
            titleBarBackgroundColor = val;
            return this;
        }

        public Builder titleText(String val) {
            titleText = val;
            return this;
        }

        public Builder titleTextSize(int val) {
            titleTextSize = val;
            return this;
        }

        public Builder titleTextColor(int val) {
            titleTextColor = val;
            return this;
        }

        public Builder titleTextGravity(int val) {
            titleTextGravity = val;
            return this;
        }

        public Builder actionTextSize(int val) {
            actionTextSize = val;
            return this;
        }

        public Builder actionTextColor(int val) {
            actionTextColor = val;
            return this;
        }

        public Builder actions(List<MenuAction> val) {
            actions = val;
            return this;
        }

        public TitleBarConfig build() {
            return new TitleBarConfig(this);
        }

        public Builder show(int val) {
            show = val;
            return this;
        }
    }
}