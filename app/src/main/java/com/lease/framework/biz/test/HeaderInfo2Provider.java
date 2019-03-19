package com.lease.framework.biz.test;

import com.lease.framework.network.HeaderProvider;

import java.util.Map;

public class HeaderInfo2Provider implements HeaderProvider {

    @Override
    public Map<String, String> headerProvide() {
        Map<String, String> header = HeaderUtils.generateMallHeader();
        header.put("ch_id", 1 + "");
        return header;
    }

    @Override
    public Class targetApiClass() {
        return MallApiService.class;
    }
}