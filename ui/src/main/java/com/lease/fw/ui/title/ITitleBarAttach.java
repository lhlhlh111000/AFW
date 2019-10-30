package com.lease.fw.ui.title;

import com.lease.fw.ui.config.MenuAction;

import java.util.List;

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
     * 构建右侧菜单列表
     * @return 菜单列表
     */
    List<MenuAction> buildMenuActions();

    /**
     * 获取菜单配置
     * @return 菜单配置
     */
    TitleBarConfigWrapper getTitleBarConfig();

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

