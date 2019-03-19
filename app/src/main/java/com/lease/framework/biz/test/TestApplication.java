package com.lease.framework.biz.test;

import android.app.Application;
import android.content.Context;

import com.lease.framework.network.BaseUrlProvider;
import com.lease.framework.network.DefaultNetworkConfig;
import com.lease.framework.network.HeaderProvider;
import com.lease.framework.network.NetworkCentre;
import com.lease.framework.network.NetworkConfig;

import java.util.ArrayList;
import java.util.List;

public class TestApplication extends Application {

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