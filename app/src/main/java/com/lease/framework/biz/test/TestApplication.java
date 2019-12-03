package com.lease.framework.biz.test;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;

import com.lease.fw.ui.UICentre;
import com.lease.fw.ui.config.ToastConfig;
import com.lease.fw.ui.config.UIConfig;
import com.lease.fw.ui.toast.ToastUtil;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshFooterCreator;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator;

public class TestApplication extends Application {

    static {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayout layout) {
                return new ClassicsHeader(context);
            }
        });
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @NonNull
            @Override
            public RefreshFooter createRefreshFooter(@NonNull Context context, @NonNull RefreshLayout layout) {
                return new ClassicsFooter(context);
            }
        });
    }

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        initUIConfig();
    }

    public static Context getContext() {
        return context;
    }

    private void initUIConfig() {
        ToastConfig.Builder toastBuilder = new ToastConfig.Builder();
        toastBuilder.mode(ToastUtil.Mode.REPLACEABLE)
                .gravity(Gravity.TOP)
                .isCustomRes(true)
                .customResId(R.layout.view_toast)
                .build();
        UIConfig.Builder builder = new UIConfig.Builder()
                .toastConfig(toastBuilder.build());
        UICentre.getInstance().withContext(this).setUiConfig(builder.build());
    }
}