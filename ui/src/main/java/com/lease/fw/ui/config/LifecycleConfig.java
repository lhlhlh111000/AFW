package com.lease.fw.ui.config;

import java.util.ArrayList;
import java.util.List;

/**
 * 生命周期配置信息
 */
public class LifecycleConfig {

    private List<LifecycleObserver> observers = new ArrayList<>();

    public void addObserver(LifecycleObserver observer) {
        observers.add(observer);
    }

    public List<LifecycleObserver> getObservers() {
        return observers;
    }
}