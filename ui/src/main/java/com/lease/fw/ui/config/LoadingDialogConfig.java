package com.lease.fw.ui.config;

import android.content.Context;

/**
 * created time: 2019-10-31
 * author: cqt
 * description: 加载对话框配置
 */
public interface LoadingDialogConfig {


    /**
     * 显示加载对话框
     * @param context
     * @param msg
     */
    void showLoadingDialog(Context context, String msg);

    /**
     * 隐藏加载对话框
     */
    void dismissDialog();
}
