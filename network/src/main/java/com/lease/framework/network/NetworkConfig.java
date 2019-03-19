package com.lease.framework.network;

import java.util.List;

import okhttp3.Interceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;

public class NetworkConfig {

    private int timeout;  // 超时时间

    private List<HeaderProvider> headerProviders; // 头部信息提供者

    private List<BaseUrlProvider> urlProviders; // 基地址提供者

    private List<Interceptor> interceptors; // 拦截器

    private Converter.Factory converterFactory; // 转换器

    private CallAdapter.Factory adapterFactory; // 转换器

    public NetworkConfig() {}

    private NetworkConfig(Builder builder) {
        setTimeout(builder.timeout);
        setHeaderProviders(builder.headerProviders);
        setUrlProviders(builder.urlProviders);
        setInterceptors(builder.interceptors);
        setConverterFactory(builder.converterFactory);
        setAdapterFactory(builder.adapterFactory);
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public List<HeaderProvider> getHeaderProviders() {
        return headerProviders;
    }

    public void setHeaderProviders(List<HeaderProvider> headerProviders) {
        this.headerProviders = headerProviders;
    }

    public List<BaseUrlProvider> getUrlProviders() {
        return urlProviders;
    }

    public void setUrlProviders(List<BaseUrlProvider> urlProviders) {
        this.urlProviders = urlProviders;
    }

    public List<Interceptor> getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(List<Interceptor> interceptors) {
        this.interceptors = interceptors;
    }

    public Converter.Factory getConverterFactory() {
        return converterFactory;
    }

    public void setConverterFactory(Converter.Factory converterFactory) {
        this.converterFactory = converterFactory;
    }

    public CallAdapter.Factory getAdapterFactory() {
        return adapterFactory;
    }

    public void setAdapterFactory(CallAdapter.Factory adapterFactory) {
        this.adapterFactory = adapterFactory;
    }

    public static final class Builder {
        private int timeout;
        private List<HeaderProvider> headerProviders;
        private List<BaseUrlProvider> urlProviders;
        private List<Interceptor> interceptors;
        private Converter.Factory converterFactory;
        private CallAdapter.Factory adapterFactory;

        public Builder() {
        }

        public Builder timeout(int val) {
            timeout = val;
            return this;
        }

        public Builder headerProviders(List<HeaderProvider> val) {
            headerProviders = val;
            return this;
        }

        public Builder urlProviders(List<BaseUrlProvider> val) {
            urlProviders = val;
            return this;
        }

        public Builder interceptors(List<Interceptor> val) {
            interceptors = val;
            return this;
        }

        public Builder converterFactory(Converter.Factory val) {
            converterFactory = val;
            return this;
        }

        public Builder adapterFactory(CallAdapter.Factory val) {
            adapterFactory = val;
            return this;
        }

        public NetworkConfig build() {
            return new NetworkConfig(this);
        }
    }
}