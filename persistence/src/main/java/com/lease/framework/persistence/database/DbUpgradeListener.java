package com.lease.framework.persistence.database;

/**
 *数据库升级回调接口
 */
public interface DbUpgradeListener {
    /**
     * // 1. 取出旧数据备份到零时表或其他地方
     // 2. drop旧表
     // 3. 插入新数据
     // or
     // 1. alter修改旧表，sqlite的alter语句有很多缺陷。
     // 2. 更新数据
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    public void onUpgrade(DbManager db, int oldVersion, int newVersion);
}
