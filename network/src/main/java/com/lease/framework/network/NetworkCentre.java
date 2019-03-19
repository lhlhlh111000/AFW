package com.lease.framework.network;

import android.util.Log;

import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

public class NetworkCentre {

    private static NetworkCentre sInstance;

    private NetworkCentre() {
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                // 捕获全局异常，默认不做处理

                Log.e("NetworkCentre", "RxJavaPlugins error handler");
            }
        });
    }

    private NetworkConfig config;

    public static NetworkCentre getInstance() {
        if(null == sInstance) {
            synchronized (NetworkCentre.class) {
                if(null == sInstance) {
                    sInstance = new NetworkCentre();
                }
            }
        }

        return sInstance;
    }

    public void setNetworkConfig(NetworkConfig config) {
        this.config = config;
    }

    public NetworkConfig getNetworkConfig() {
        if(null == config) {
            config = new DefaultNetworkConfig();
        }
        if(null == config.getAdapterFactory()) {
            config.setAdapterFactory(new DefaultNetworkConfig().getAdapterFactory());
        }
        if(null == config.getConverterFactory()) {
            config.setConverterFactory(new DefaultNetworkConfig().getConverterFactory());
        }
        return this.config;
    }
}