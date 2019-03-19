package com.lease.framework.network;

/**
 * 基地址提供者
 */
public interface BaseUrlProvider {

    /**
     * 基地址提供
     * @return 基地址
     */
    String baseUrlProvide();

    /**
     * 匹配对目标api类
     * @return api类
     */
    Class targetApiClass();
}