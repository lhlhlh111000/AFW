package com.lease.fw.router.config;

import android.content.Context;

/**
 * Created by zlh on 2017/5/5.
 */

public interface SchemeHandler {

    /**
     * 指定能处理的协议头
     * @return
     */
    String handleScheme();

    /**
     * 处理协议
     * @param context
     * @param uri
     * @param callBackerKey
     */
    boolean dispatchRouter(Context context, String uri, String callBackerKey);
}
