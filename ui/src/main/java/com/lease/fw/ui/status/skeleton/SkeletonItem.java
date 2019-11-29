package com.lease.fw.ui.status.skeleton;

/**
 * created time: 2019-11-28
 * author: cqt
 * description: 骨架item项
 */
public class SkeletonItem {

    private int num;

    private int resId;

    public SkeletonItem(int resId) {
        this.num = 1;
        this.resId = resId;
    }

    public SkeletonItem(int num, int resId) {
        this.num = num;
        this.resId = resId;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}