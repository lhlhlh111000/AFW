package com.lease.framework.network;

import java.util.Map;

/**
 * 头部信息提供者
 */
public interface HeaderProvider {

    /**
     * 头部信息提供
     * @return 头部信息
     */
    Map<String, String> headerProvide();

    /**
     * 匹配对目标api类
     * @return api类
     */
    Class targetApiClass();
}