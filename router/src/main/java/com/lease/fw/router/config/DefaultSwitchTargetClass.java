package com.lease.fw.router.config;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.lease.fw.router.RouterUtil;

/**
 * created time: 2019-11-13
 * author: cqt
 * description: 默认跳转处理
 */
public class DefaultSwitchTargetClass implements SwitchTargetClass {

    @Override
    public void start(Context context, Class clazz, Bundle bundle) {
        if(RouterUtil.isFragment(clazz)) {
            throw new IllegalStateException("No invoke for switch fragment container.");
        }else {
            Intent intent = new Intent(context, clazz);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            context.startActivity(intent);
        }
    }
}