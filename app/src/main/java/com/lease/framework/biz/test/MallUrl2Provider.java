package com.lease.framework.biz.test;

import com.lease.framework.network.BaseUrlProvider;

public class MallUrl2Provider implements BaseUrlProvider {

    @Override
    public String baseUrlProvide() {
        return "http://dev.mall.taoqicar.com:8086";
    }

    @Override
    public Class targetApiClass() {
        return MallApiService.class;
    }
}
