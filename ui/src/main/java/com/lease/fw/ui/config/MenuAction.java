package com.lease.fw.ui.config;

public abstract class MenuAction {

    public int menuIcon;

    public int menuText;

    public MenuAction(int menuText) {
        this.menuText = menuText;
    }

    public MenuAction(int menuIcon, int menuText) {
        this.menuIcon = menuIcon;
        this.menuText = menuText;
    }

    public abstract void onAction();
}