package com.lease.fw.ui.status;

/**
 * created time: 2019-11-11
 * author: cqt
 * description: 多状态
 */
enum Status {

    DEFAULT, // 默认展示内容
    LOADING, // 加载中
    EMPTY, // 空数据
    ERROR, // 异常
    NET_OFFILEN // 无网络
}