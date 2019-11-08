package com.lease.framework.network;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 默认头部信息插入拦截者
 */
public class DefaultHeaderInterceptor implements Interceptor {

    private HeaderProvider headerProvider;

    public DefaultHeaderInterceptor(HeaderProvider headerProvider) {
        this.headerProvider = headerProvider;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        if(null != headerProvider) {
            Map<String, String> headers = headerProvider.headerProvide();
            if(null != headers) {
                Set<String> keys = headers.keySet();
                Iterator var4 = keys.iterator();
                while(var4.hasNext()) {
                    String headerKey = (String)var4.next();
                    String head = headers.get(headerKey);
                    if(null == head) {
                        head = "";
                    }
                    builder.addHeader(headerKey, head);
                }
            }
        }
        return chain.proceed(builder.build());
    }
}