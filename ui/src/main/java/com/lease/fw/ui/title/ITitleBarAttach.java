package com.lease.fw.ui.title;

public interface ITitleBarAttach {

    /**
     * 是否覆盖式标题
     * @return
     */
    boolean isOverlayTitle();

    /**
     * 获取自定义标题栏布局
     * @return 自定义布局资源id
     */
    int obtainTitleBarLayout();

    /**
     * 设置标题
     * @param titleRes
     */
    void setTitle(int titleRes);

    /**
     * 设置标题
     * @param titleStr
     */
    void setTitle(String titleStr);
}

