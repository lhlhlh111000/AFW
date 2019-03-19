package com.lease.framework.network;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * OkHttp client 管理
 */
public class OkHttpManager {

    private static OkHttpManager sInstance;

    private Map<String, OkHttpClient> clientMap = new HashMap<>();

    private OkHttpManager() {}

    public static OkHttpManager getInstance() {
        if(null == sInstance) {
            synchronized (OkHttpManager.class) {
                if(null == sInstance) {
                    sInstance = new OkHttpManager();
                }
            }
        }

        return sInstance;
    }

    /**
     * 根据目标api获取http client
     * @param apiClass 目标api
     * @return client
     */
    public OkHttpClient getOkHttpClient(Class apiClass) {
        OkHttpClient client = null;
        if(null == apiClass) {
            return client;
        }

        client = clientMap.get(apiClass.getSimpleName());
        if(null == client) {
            client = createClient(apiClass);
            clientMap.put(apiClass.getSimpleName(), client);
        }

        return client;
    }

    /**
     * 创建http client
     * @param apiClass 目标api
     * @return client
     */
    private OkHttpClient createClient(Class apiClass) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        builder.cookieJar(new CookieJar)
        builder.addInterceptor(new DefaultHeaderInterceptor(buildHeaderInfo(apiClass)));
        invokeInterceptor(builder);
        builder.connectTimeout(NetworkCentre.getInstance().getNetworkConfig().getTimeout(), TimeUnit.SECONDS);
        builder.writeTimeout(NetworkCentre.getInstance().getNetworkConfig().getTimeout(), TimeUnit.SECONDS);
        // 设置同时连接的个数和时间，这里8个，和每个保持时间为15s
        builder.connectionPool(new ConnectionPool(8, 15, TimeUnit.SECONDS));
        return builder.build();
    }

    /**
     * 构建头部信息
     * @param apiClass 目标api
     * @return 头部信息
     */
    private HeaderProvider buildHeaderInfo(Class apiClass) {
        if(null == NetworkCentre.getInstance().getNetworkConfig()
                || null == NetworkCentre.getInstance().getNetworkConfig().getHeaderProviders()) {
            return null;
        }

        HeaderProvider headers = null;
        if(NetworkCentre.getInstance().getNetworkConfig().getHeaderProviders().size() > 0) {
            for(HeaderProvider provider : NetworkCentre.getInstance().getNetworkConfig().getHeaderProviders()) {
                if(null != provider.targetApiClass() &&
                        apiClass.getName().equals(provider.targetApiClass().getName())) {
                    headers = provider;
                }
            }

            if(null == headers) { // 匹配不到，默认使用第一个
                headers = NetworkCentre.getInstance().getNetworkConfig().getHeaderProviders().get(0);
            }
        }

        return headers;
    }

    /**
     * 运用拦截器
     * @param builder http client
     */
    private void invokeInterceptor(OkHttpClient.Builder builder) {
        if(null == NetworkCentre.getInstance().getNetworkConfig().getInterceptors()) {
            return;
        }

        for(Interceptor interceptor : NetworkCentre.getInstance().getNetworkConfig().getInterceptors()) {
            builder.addInterceptor(interceptor);
        }
    }
}
