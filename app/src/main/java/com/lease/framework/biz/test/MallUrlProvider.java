package com.lease.framework.biz.test;

import com.lease.framework.network.BaseUrlProvider;

public class MallUrlProvider implements BaseUrlProvider {

    @Override
    public String baseUrlProvide() {
        return "http://dev.mall.taoqicar.com:8080";
    }

    @Override
    public Class targetApiClass() {
        return null;
    }
}
