package com.lease.fw.router.callback;

/**
 * 回调者
 * Created by cqt on 2018/6/28.
 */
public interface RouterCallBacker<T> {

    /**
     * 回调方法，返回指定信息
     * @param result
     */
    void callBackResult(T result);
}