package com.lease.framework.biz.test;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.lease.framework.network.BaseUrlProvider;
import com.lease.framework.network.DefaultNetworkConfig;
import com.lease.framework.network.HeaderProvider;
import com.lease.framework.network.NetworkCentre;
import com.lease.framework.network.NetworkConfig;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshFooterCreator;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator;

import java.util.ArrayList;
import java.util.List;

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

        initNetworkConfig();
    }

    public static Context getContext() {
        return context;
    }

    private void initNetworkConfig() {
        NetworkConfig networkConfig = new DefaultNetworkConfig();

        List<HeaderProvider> headerProviders = new ArrayList<>();
        headerProviders.add(new HeaderInfoProvider());
        headerProviders.add(new HeaderInfo2Provider());
        networkConfig.setHeaderProviders(headerProviders);

        List<BaseUrlProvider> urlProviders = new ArrayList<>();
        urlProviders.add(new MallUrlProvider());
        urlProviders.add(new MallUrl2Provider());
        networkConfig.setUrlProviders(urlProviders);

        NetworkCentre.getInstance().setNetworkConfig(networkConfig);
    }
}