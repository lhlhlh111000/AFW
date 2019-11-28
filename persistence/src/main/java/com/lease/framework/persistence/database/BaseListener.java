package com.lease.framework.persistence.database;

/**
 * Created by Linhh on 16/8/2.
 */
public interface BaseListener {

    /**
     * 数据插入
     */
    void onInsert();

    /**
     * 数据删除
     */
    void onDelete();

    /**
     * 数据更新
     */
    void onUpdate();

    /**
     * 数据变动
     */
    void onChange();
}
