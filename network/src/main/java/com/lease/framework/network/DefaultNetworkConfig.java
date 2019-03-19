package com.lease.framework.network;

import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class DefaultNetworkConfig extends NetworkConfig {

    public static final int DEFAULT_TIMEOUT = 20; // 默认20s超时时间

    public DefaultNetworkConfig() {
        setAdapterFactory(RxJava2CallAdapterFactory.create());
        setConverterFactory(GsonConverterFactory.create());
        setTimeout(DEFAULT_TIMEOUT);
    }
}