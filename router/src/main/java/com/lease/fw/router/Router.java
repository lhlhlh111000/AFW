package com.lease.fw.router;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.lease.fw.router.callback.RouterCallBackCentre;
import com.lease.fw.router.callback.RouterCallBacker;
import com.lease.fw.router.config.RouterConfig;
import com.lease.fw.router.config.RouterInterceptor;
import com.lease.fw.router.config.RouterResultProcess;
import com.lease.fw.router.config.SchemeHandler;
import com.lease.fw.router.param.ParamParser;

import org.json.JSONObject;

import java.util.List;

/**
 * 路由
 * Created by zlh on 2017/5/5.
 */
public class Router {

    private static final String TAG = Router.class.getSimpleName();

    public static final String KEY_CALLBACK_ID = "KEY_CALLBACK_ID"; // 回调者id

    private static RouterConfig sConfig; // 路由配置

    private static RouterConfig getConfig() {
        if(null == sConfig) {
            // Give a default config
            RouterConfig.Builder builder = new RouterConfig.Builder();
            sConfig = builder.build();
        }

        return sConfig;
    }

    public static void setConfig(RouterConfig config) {
        sConfig = config;
    }

    /**
     * 获取协议处理者
     * @param scheme
     * @return
     */
    private static SchemeHandler wrapperHandler(String scheme) {
        RouterConfig config = getConfig();
        List<SchemeHandler> list = config.getHandlers();
        if(null == list) {
            return null;
        }
        for(SchemeHandler handler : list) {
            if(!TextUtils.isEmpty(scheme) && scheme.equals(handler.handleScheme())) {
                return handler;
            }
        }
        return null;
    }

    /**
     * 参数初始化
     * @param activity
     */
    public static void initParam(Activity activity) {
        ParamParser.init(activity);
    }

    /**
     * 参数初始化
     * @param fragment
     */
    public static void initParam(Fragment fragment) {
        ParamParser.init(fragment);
    }

    /**
     * 路由跳转
     * @param context 上下文
     * @param clazz 目标页面
     */
    public static void router(Context context, Class<?> clazz) {
        router(context, clazz, null, null);
    }

    /**
     * 路由跳转
     * @param context 上下文
     * @param clazz 目标页面
     * @param bundle 跳转参数
     */
    public static void router(Context context, Class<?> clazz, Bundle bundle) {
        router(context, clazz, null, bundle);
    }

    /**
     * 路由跳转
     * @param context 上下文
     * @param clazz 目标页面
     * @param callBacker 回调者
     */
    public static void router(Context context, Class<?> clazz, RouterCallBacker callBacker) {
        router(context, clazz, callBacker, null);
    }

    /**
     * 路由跳转
     * @param context 上下文
     * @param clazz 目标页面
     * @param callBacker 回调者
     * @param bundle 跳转参数
     */
    public static void router(Context context, Class<?> clazz,
                              RouterCallBacker callBacker, Bundle bundle) {
        if(null == context || null == clazz) {
            return;
        }
        if(!RouterUtil.isActivity(clazz)
                && !RouterUtil.isFragment(clazz)) {
            throw new IllegalStateException(clazz.getName() + " is not a activity or fragment.");
        }

        if(null != callBacker) { // 绑定回调者id
            String key = callBacker.hashCode() + "";
            RouterCallBackCentre.getInstance().push(callBacker);
            if(null == bundle) {
                bundle = new Bundle();
            }
            bundle.putString(KEY_CALLBACK_ID, key);
        }
        Router.getConfig().getSwitchTargetClass().start(context, clazz, bundle);
    }

    /**
     * 路由转发 无回调者
     * @param context 上下文
     * @param uri 跳转协议
     * @return
     */
    public static boolean router(Context context, String uri) {
        return router(context, uri, null);
    }

    /**
     * 路由转发
     * @param context 上下文
     * @param uri 跳转协议
     * @param callBacker 回调者
     * @return
     */
    public static boolean router(Context context, String uri, RouterCallBacker callBacker) {
        boolean isProcess = false;
        if(null == context) {
            return false;

        }
        if(TextUtils.isEmpty(uri)) {
            Log.e(TAG, "Can not process empty uri~");
            return false;
        }

        // 拦截处理
        if(null != getConfig().getInterceptors()) {
            for(RouterInterceptor interceptor : getConfig().getInterceptors()) {
                interceptor.invokeInterceptor(context, uri);
            }
        }

        // 路由处理
        SchemeHandler handler = wrapperHandler(RouterUtil.getSchemeStr(uri));
        if(null == handler) {
            Log.e(TAG, "Not found scheme handler to process uri: " + uri);
        }else {
            String key = null;
            if(null != callBacker) {
                key = callBacker.hashCode() + "";
                RouterCallBackCentre.getInstance().push(callBacker);
            }else {
                // 是否透传参数
                try {
                    JSONObject object = new JSONObject(Uri.parse(uri).getQueryParameter("param"));
                    if(object.has("callbackName")) {
                        key = object.optString("callbackName");
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            isProcess = handler.dispatchRouter(context, uri, key);
        }


        // 结果处理
        if(null != getConfig().getResultProcesses()) {
            for(RouterResultProcess resultProcess : getConfig().getResultProcesses()) {
                isProcess = resultProcess.onProcess(context, uri, isProcess);
            }
        }

        return isProcess;
    }

    /**
     * 结果回调
     * @param fragment
     * @param result
     * @param <T>
     */
    public static <T> void callBackResult(Fragment fragment, T result) {
        callBackResult(fragment.getArguments().getString(KEY_CALLBACK_ID), result);
    }

    /**
     * 结果回调
     * @param activity
     * @param result
     * @param <T>
     */
    public static <T> void callBackResult(Activity activity, T result) {
        if(null == activity.getIntent()) {
            return;
        }
        Bundle bundle = activity.getIntent().getExtras();
        if(null == bundle) {
            return;
        }
        callBackResult(bundle.getString(KEY_CALLBACK_ID), result);
    }

    /**
     * 结果回调
     * @param key
     * @param result
     * @param <T>
     */
    public static <T> void callBackResult(String key, T result) {
        if(TextUtils.isEmpty(key)) {
            return;
        }

        RouterCallBacker<T> callBacker = RouterCallBackCentre.getInstance()
                .obtainCallBacker(key);
        if(null == callBacker) {
            return;
        }
        callBacker.callBackResult(result);
    }
}