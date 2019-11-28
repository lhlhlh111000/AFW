package com.lease.framework.persistence.database;

import com.lease.framework.persistence.database.sqlite.Selector;
import com.lease.framework.persistence.database.sqlite.WhereBuilder;

import java.util.List;

/**
 * Created by hxd
 */
public interface BaseDAO {

    /**
     * 插入数据
     *
     * @param entity 实体类
     * @return 0表示插入错误，1表示插入成功
     */
    public int insert(Object entity);

    /**
     * 批量插入数据
     *
     * @param entitys 实体类
     * @return 0表示插入错误，1表示插入成功
     */
    public<T> int insertAll(List<T> entitys);


    /**
     * 插入或者更新数据
     *
     * @param entity
     * @return
     */
    public int insertOrUpdate(Object entity);

    /**
     * 批量插入或者更新数据
     *
     * @param entitys
     * @return
     */
    public<T> int insertOrUpdateAll(List<T> entitys);


    /**
     * /**
     * 查询第一条数据
     *
     * @param entityClass 要查询表对应的类名
     * @param <T>
     * @return
     */
    public <T> T queryFirst(Class<T> entityClass);

    /**
     * 通过id查询数据
     *
     * @param entityClass
     * @param idValue
     * @param <T>
     * @return
     */
    public <T> T queryById(Class<T> entityClass, Object idValue);

    /**
     * 查询所有表记录
     *
     * @param entityClass
     * @param <T>
     * @return
     */
    public <T> List<T> queryAll(Class<?> entityClass);

    /**
     * 按条件查询表记录
     *
     * @param entityClass
     * @param selector
     * @param <T>
     * @return
     */
    public <T> List<T> query(Class<?> entityClass, Selector selector);

    /**
     * 按条件查询表记录
     *
     * @param entityClass
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @param <T>
     * @return
     */
    public <T> List<T> query(Class<?> entityClass, String selection, String[] selectionArgs, String sortOrder);

    /**
     * 按条件查询表记录
     *
     * @param sql
     * @param entityClass
     * @param selectionArgs
     * @param <T>
     * @return
     */
    public <T> List<T> query(String sql, Class<?> entityClass, String[] selectionArgs);


    /**
     * 按条件查询表记录
     *
     * @param entityClass
     * @param selector
     * @param <T>
     * @return
     */
    public <T> T queryEntity(Class<?> entityClass, Selector selector);

    /**
     * 按条件查询表记录
     *
     * @param entityClass
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @param <T>
     * @return
     */
    public <T> T queryEntity(Class<?> entityClass, String selection, String[] selectionArgs, String sortOrder);

    /**
     * 按条件查询表记录
     *
     * @param sql
     * @param entityClass
     * @param selectionArgs
     * @param <T>
     * @return
     */
    public <T> T queryEntity(String sql, Class<?> entityClass, String[] selectionArgs);

    /**
     * 删除表某一条记录
     *
     * @param entity
     * @return 0表示删除失败，>0表删除成功
     */
    public int delete(Object entity);

    /**
     * 按条件删除表记录
     *
     * @param entityType
     * @param whereBuilder
     * @return
     */
    public int delete(Class<?> entityType, WhereBuilder whereBuilder);

    /**
     * 删除指定几条记录
     *
     * @param entities
     * @return
     */
    public int deleteAll(List<?> entities);

    /**
     * 删除表所有记录
     *
     * @param entityType
     */
    public void deleteAll(Class<?> entityType);


    /**
     * 按条件删除记录
     *
     * @param sql
     * @param selection
     * @param selectionArgs
     * @return
     */
    //public int deleteFromSql(String sql, String selection, String[] selectionArgs);

    /**
     * 更新内容
     *
     * @param entity
     * @param updateColumnNames 指定字段
     * @return
     */
    public int update(Object entity, String... updateColumnNames);

    /**
     * 更新内容
     *
     * @param entity
     * @param whereBuilder
     * @param updateColumnNames
     * @return
     */
    public int update(Object entity, WhereBuilder whereBuilder, String... updateColumnNames);

    /**
     * 批量更新内容
     *
     * @param entities
     * @param updateColumnNames
     * @return
     */
    public int updateAll(List<?> entities, String... updateColumnNames);

    /**
     * 批量更新内容
     *
     * @param entities
     * @param whereBuilder
     * @param updateColumnNames
     * @return
     */
    public int updateAll(List<?> entities, WhereBuilder whereBuilder, String... updateColumnNames);

}
