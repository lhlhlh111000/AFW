package com.lease.framework.biz.test;

import com.lease.framework.network.BaseUrlProvider;

public class MallUrl2Provider implements BaseUrlProvider {

    @Override
    public String baseUrlProvide() {
        return "http://api.taoqicar.com";
    }

    @Override
    public Class targetApiClass() {
        return MallApiService.class;
    }
}
