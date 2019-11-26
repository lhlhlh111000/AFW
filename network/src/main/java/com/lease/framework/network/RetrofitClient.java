package com.lease.framework.network;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * 在Retrofit 之上稍微包装提供调用，配置信息走 @see{@link NetworkCentre}
 */
public class RetrofitClient {

    private static RetrofitClient sInstance;

    private RetrofitClient() {}

    private Map<String, Retrofit> retrofitMap = new HashMap<>();

    public static RetrofitClient getInstance() {
        if(null == sInstance) {
            synchronized (RetrofitClient.class) {
                if(null == sInstance) {
                    sInstance = new RetrofitClient();
                }
            }
        }

        return sInstance;
    }

    /**
     * 创建api service
     * @param apiService 目标service
     * @param <T>
     * @return
     */
    public <T> T create(final Class<T> apiService) {
        if(null == apiService) {
            throw new IllegalStateException("Api service is null");
        }

        Retrofit retrofit = retrofitMap.get(apiService.getName());
        if(null == retrofit) {
            retrofit = createRetrofit(apiService);
            retrofitMap.put(apiService.getName(), retrofit);
        }

        return retrofit.create(apiService);
    }

    /**
     * 创建 Retrofit
     * @param apiService 目标service
     * @return retrofit
     */
    private Retrofit createRetrofit(Class apiService) {
        return new Retrofit.Builder()
                .client(OkHttpManager.getInstance().getOkHttpClient(apiService))
                .addConverterFactory(NetworkCentre.getInstance().getNetworkConfig().getConverterFactory())
                .addCallAdapterFactory(NetworkCentre.getInstance().getNetworkConfig().getAdapterFactory())
                .baseUrl(obtainBaseUrl(apiService))
                .build();
    }

    public String obtainBaseUrl(Class apiService) {
        if(null == NetworkCentre.getInstance().getNetworkConfig().getUrlProviders()
                || NetworkCentre.getInstance().getNetworkConfig().getUrlProviders().size() <= 0) {
            throw new IllegalStateException("Base url providers is null");
        }

        for(BaseUrlProvider provider : NetworkCentre.getInstance().getNetworkConfig().getUrlProviders()) {
            if(null != provider.targetApiClass() &&
                    apiService.getName().equals(provider.targetApiClass().getName())) {
                return provider.baseUrlProvide();
            }
        }

        // 默认第一个
        return NetworkCentre.getInstance().getNetworkConfig().getUrlProviders().get(0).baseUrlProvide();
    }
}
