package com.lease.fw.router.config;

import android.content.Context;

/**
 * Created by zlh on 2017/5/5.
 */
public interface RouterInterceptor {

    void invokeInterceptor(Context context, String uri);
}
