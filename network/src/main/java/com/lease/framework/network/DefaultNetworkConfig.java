package com.lease.framework.network;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class DefaultNetworkConfig extends NetworkConfig {

    public static final int DEFAULT_TIMEOUT = 20; // 默认20s超时时间

    public DefaultNetworkConfig() {
        setAdapterFactory(RxJava2CallAdapterFactory.create());
        setConverterFactory(EmptyConverterFactory.create());
        setTimeout(DEFAULT_TIMEOUT);
    }

    static class EmptyConverterFactory extends Converter.Factory {

        private GsonConverterFactory factory;

        public static EmptyConverterFactory create() {
            return new EmptyConverterFactory(GsonConverterFactory.create());
        }

        private EmptyConverterFactory(GsonConverterFactory factory) {
            this.factory = factory;
        }

        @Override
        public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
            return factory.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
        }

        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(final Type type, Annotation[] annotations, Retrofit retrofit) {
            final Converter<ResponseBody, ?> delegate = factory.responseBodyConverter(type, annotations, retrofit);
            return new Converter<ResponseBody,Object>() {
                @Override
                public Object convert(ResponseBody body) throws IOException {
                    BufferedSource source = body.source();
                    source.request(Long.MAX_VALUE); // request the entire body.
                    Buffer buffer = source.buffer();
                    String responseStr = buffer.clone().readString(Charset.forName("UTF-8"));
                    if (TextUtils.isEmpty(responseStr)) {
                        try {
                            Constructor[] constructors = Class.forName(TypeToken.get(type).getRawType().getName()).getDeclaredConstructors();
                            for(Constructor constructor : constructors) {
                                try {
                                    constructor.setAccessible(true);
                                    return constructor.newInstance();
                                }catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (InstantiationException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            }
                        }catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                    return delegate.convert(ResponseBody.create(body.contentType(), responseStr));
                }
            };
        }
    }
}