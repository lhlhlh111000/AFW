package com.lease.framework.task;

import com.lease.framework.task.core.DefaultErrorTransformer;


/**
 * 任务中心
 * 基于rxjava做的简单调用，包了一层对错误异常的处理
 * 任务调度优先级这块暂无处理，如果有遇到对应开发需求，可自行实现
 */
public class TaskCentre {

    private static TaskCentre sInstance;

    private TaskCentre() {}

    private TaskConfig config;

    public static TaskCentre getInstance() {
        if(null == sInstance) {
            synchronized (TaskCentre.class) {
                if(null == sInstance) {
                    sInstance = new TaskCentre();
                }
            }
        }

        return sInstance;
    }

    public TaskConfig getConfig() {
        if(null == config) {
            config = new TaskConfig();
            config.setErrorTransformer(new DefaultErrorTransformer());
        }
        return config;
    }

    public void setConfig(TaskConfig config) {
        this.config = config;
    }
}