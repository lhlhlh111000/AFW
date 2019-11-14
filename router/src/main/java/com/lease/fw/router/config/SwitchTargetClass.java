package com.lease.fw.router.config;

import android.content.Context;
import android.os.Bundle;

/**
 * created time: 2019-11-13
 * author: cqt
 * description: 跳转
 */
public interface SwitchTargetClass {

    void start(Context context, Class clazz, Bundle bundle);
}