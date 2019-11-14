package com.lease.fw.router.config;

import android.content.Context;

/**
 * 路由结果处理
 * Created by zlh on 2017/5/8.
 */

public interface RouterResultProcess {

    boolean onProcess(Context context, String uri, boolean isProcess);
}
