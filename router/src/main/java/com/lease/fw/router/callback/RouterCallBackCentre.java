package com.lease.fw.router.callback;

import java.util.HashMap;
import java.util.Map;

/**
 * 回调处理中心
 * Created by cqt on 2018/6/28.
 */

public class RouterCallBackCentre {

    private static RouterCallBackCentre sInstance;

    private RouterCallBackCentre() {}

    private Map<String, RouterCallBacker> callBackerMap = new HashMap<>();

    public static RouterCallBackCentre getInstance() {
        if(null == sInstance) {
            synchronized (RouterCallBackCentre.class) {
                if(null == sInstance) {
                    sInstance = new RouterCallBackCentre();
                }
            }
        }

        return sInstance;
    }

    public void push(RouterCallBacker callBacker) {
        if(null == callBacker) {
            return;
        }

        callBackerMap.put(callBacker.hashCode() + "", callBacker);
    }

    public RouterCallBacker obtainCallBacker(String key) {
        if(!callBackerMap.containsKey(key)) {
            return null;
        }

        RouterCallBacker callBacker = callBackerMap.get(key);
        removeCallBackerKey(key);
        return callBacker;
    }

    public void removeCallBackerKey(String key) {
        if(!callBackerMap.containsKey(key)) {
            return;
        }

        callBackerMap.remove(key);
    }
}
