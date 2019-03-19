package com.lease.framework.biz.test;

import com.lease.framework.network.HeaderProvider;

import java.util.Map;

public class HeaderInfoProvider implements HeaderProvider {

    @Override
    public Map<String, String> headerProvide() {
        return HeaderUtils.generateMallHeader();
    }

    @Override
    public Class targetApiClass() {
        return null;
    }
}