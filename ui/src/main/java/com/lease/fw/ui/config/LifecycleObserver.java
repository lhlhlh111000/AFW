package com.lease.fw.ui.config;

/**
 * 生命周期观察者，可用于打点统计之类用
 */
public abstract class LifecycleObserver {
    public abstract void onCreate(Class pageClass);
    public abstract void onStart(Class pageClass);
    public abstract void onResume(Class pageClass);
    public abstract void onStop(Class pageClass);
    public abstract void onPause(Class pageClass);
    public abstract void onDestroy(Class pageClass);
}