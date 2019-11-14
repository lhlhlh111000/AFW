package com.lease.fw.router.config;

import java.util.ArrayList;
import java.util.List;

/**
 * 路由配置
 * Created by zlh on 2017/5/5.
 */
public class RouterConfig {

    private List<RouterInterceptor> interceptors = new ArrayList<>();

    private List<SchemeHandler> handlers = new ArrayList<>();

    private List<RouterResultProcess> resultProcesses = new ArrayList<>();

    private SwitchTargetClass switchTargetClass;

    private RouterConfig(Builder builder) {
        setInterceptors(builder.interceptors);
        setHandlers(builder.handlers);
        setResultProcesses(builder.resultProcesses);
        setSwitchTargetClass(builder.switchTargetClass);
    }

    public List<RouterInterceptor> getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(List<RouterInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    public List<SchemeHandler> getHandlers() {
        return handlers;
    }

    public void setHandlers(List<SchemeHandler> handlers) {
        this.handlers = handlers;
    }

    public List<RouterResultProcess> getResultProcesses() {
        return resultProcesses;
    }

    public void setResultProcesses(List<RouterResultProcess> resultProcesses) {
        this.resultProcesses = resultProcesses;
    }

    public SwitchTargetClass getSwitchTargetClass() {
        if(null == switchTargetClass) {
            switchTargetClass = new DefaultSwitchTargetClass();
        }
        return switchTargetClass;
    }

    public void setSwitchTargetClass(SwitchTargetClass switchTargetClass) {
        this.switchTargetClass = switchTargetClass;
    }

    public static final class Builder {
        private List<RouterInterceptor> interceptors;
        private List<SchemeHandler> handlers;
        private List<RouterResultProcess> resultProcesses;
        private SwitchTargetClass switchTargetClass;

        public Builder() {
        }

        public Builder interceptors(List<RouterInterceptor> val) {
            interceptors = val;
            return this;
        }

        public Builder handlers(List<SchemeHandler> val) {
            handlers = val;
            return this;
        }

        public Builder resultProcesses(List<RouterResultProcess> val) {
            resultProcesses = val;
            return this;
        }

        public Builder switchTargetClass(SwitchTargetClass val) {
            switchTargetClass = val;
            return this;
        }

        public RouterConfig build() {
            return new RouterConfig(this);
        }
    }
}