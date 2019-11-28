package com.lease.framework.persistence.database;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.text.TextUtils;

import com.lease.framework.persistence.database.sqlite.CursorUtils;
import com.lease.framework.persistence.database.sqlite.Selector;
import com.lease.framework.persistence.database.sqlite.SqlInfoBuilder;
import com.lease.framework.persistence.database.sqlite.WhereBuilder;
import com.lease.framework.persistence.database.table.ColumnUtils;
import com.lease.framework.persistence.database.table.Id;
import com.lease.framework.persistence.database.table.KeyValue;
import com.lease.framework.persistence.database.table.Table;
import com.lease.framework.persistence.database.table.TableUtils;
import com.lease.framework.core.IOUtils;
import com.lease.framework.core.LogUtils;
import com.lease.framework.core.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * 内容解决者
 * Created by Administrator on 2015/6/24.
 */
public class BaseContentResolver implements BaseDAO {
    private ContentResolver resolver;
    /*应用数据库唯一标识*/
    private String appAuthority;
    //uriType
    public static int ACCESS_URI = 0;
    public static int REPLACE_URI = 1;

    /**
     * 构造函数
     *
     * @param context
     * @param appAuthority 应用数据库唯一标识，不能为空
     */
    public BaseContentResolver(Context context, String appAuthority) {
        resolver = context.getContentResolver();
        this.appAuthority = appAuthority;
    }

    /**
     * 插入数据
     *
     * @param entity 实体类
     * @return 0表示插入错误，1表示插入成功
     */
    @Override
    public int insert(Object entity) {
        try {
            if (entity == null) {
                return 0;
            }
            Uri uri = DbUtils.getUri(entity, ACCESS_URI, appAuthority);
            Uri result = resolver.insert(uri, DbUtils.getContentValues(entity, true));
            if (result != null) {
                String query = result.getEncodedPath();
                if (!TextUtils.isEmpty(query)) {
                    int index = query.lastIndexOf("/");
                    if (index != -1) {
                        String columnId = query.substring(index + 1);
                        try {
                            int id = Integer.valueOf(columnId);
                            return id;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 插入或者更新数据
     *
     * @param entity
     * @retur
     */
    @Override
    public int insertOrUpdate(Object entity) {
        try {
            if (entity == null) {
                return 0;
            }
            Uri uri = DbUtils.getUri(entity, REPLACE_URI, appAuthority);
            Uri result = resolver.insert(uri, DbUtils.getContentValues(entity, true));
            if (result != null) {
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private ArrayList<Field> getUniqueKeys(Object entity) {
        Class clazz = entity.getClass();
        ArrayList<Field> fieldArrayList = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        if (fields != null && fields.length > 0) {
            String id = TableUtils.getPrimaryKeyFieldName(clazz);
            for (int i = 0; i < fields.length; ++i) {
                if (ColumnUtils.isUnique(fields[i])) {
                    if (!StringUtils.equals(fields[i].getName(), id)) {
                        fieldArrayList.add(fields[i]);
                    }
                }
                if (ColumnUtils.isMultiUnique(fields[i])) {
                    fieldArrayList.add(fields[i]);
                }
            }
        }
        return fieldArrayList;
    }

    private boolean hasUniqueKey(Object entity) {
        Class clazz = entity.getClass();
        Field[] fields = clazz.getDeclaredFields();
        if (fields != null && fields.length > 0) {
            String id = TableUtils.getPrimaryKeyFieldName(clazz);
            for (int i = 0; i < fields.length; ++i) {
                if (ColumnUtils.isUnique(fields[i])) {
                    if (!StringUtils.equals(fields[i].getName(), id)) {
                        return true;
                    }
                }
                if (ColumnUtils.isMultiUnique(fields[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isIdHasValues(Object entity) {
        Class clazz = entity.getClass();
        String id = TableUtils.getPrimaryKeyFieldName(clazz);
        try {
            Field field = clazz.getDeclaredField(id);
            Object value = field.get(entity);
            if (value != null) {
                return true;
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 查询第一条数据
     *
     * @param entityClass 要查询表对应的类名
     * @param <T>
     * @return
     */
    @Override
    public <T> T queryFirst(Class<T> entityClass) {
        try {
            Selector selector = Selector.from(entityClass);
            selector.limit(1);
            List<T> listResult = query(entityClass, selector);
            if (listResult != null && listResult.size() > 0) {
                return listResult.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过id查询数据
     *
     * @param entityClass
     * @param idValue
     * @param <T>
     * @return
     */
    @Override
    public <T> T queryById(Class<T> entityClass, Object idValue) {
        try {
            Table table = Table.get(entityClass);
            Selector selector = Selector.from(entityClass).where(table.id.getColumnName(), "=", idValue);
            selector.limit(1);
            List<T> listResult = query(entityClass, selector);
            if (listResult != null && listResult.size() > 0) {
                return listResult.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询所有表记录
     *
     * @param entityClass
     * @param <T>
     * @return
     */
    @Override
    public <T> List<T> queryAll(Class<?> entityClass) {
        return query(entityClass, Selector.from(entityClass));
    }

    /**
     * 按条件查询表记录
     *
     * @param entityClass
     * @param selector
     * @param <T>
     * @return
     */
    @Override
    public <T> List<T> query(Class<?> entityClass, Selector selector) {
        try {
            String selection = null;
            String[] selectionArgs = null;
            if (selector.getWhereBuilder() != null) {
                selection = selector.getWhereBuilder().getSelection();
                selectionArgs = selector.getWhereBuilder().getSelectionArgs();
            }
            String sortOrder = DbUtils.getSortOrder(entityClass, selector);
            String tableName = TableUtils.getTableName(entityClass);
            String clazzName = TableUtils.getEntityClassName(entityClass);
            Uri queryUri = UriHelper.makeTableAccessUri(tableName, clazzName, appAuthority);
            return query(queryUri, entityClass, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

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
    @Override
    public <T> List<T> query(Class<?> entityClass, String selection, String[] selectionArgs, String sortOrder) {
        String tableName = TableUtils.getTableName(entityClass);
        String clazzName = TableUtils.getEntityClassName(entityClass);
        Uri queryUri = UriHelper.makeTableAccessUri(tableName, clazzName, appAuthority);
        return query(queryUri, entityClass, selection, selectionArgs, sortOrder);
    }

    /**
     * 内容提供者对接接口
     *
     * @param uri
     * @param entityClass
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @param <T>
     * @return
     */
    private <T> List<T> query(Uri uri, Class<?> entityClass, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = resolver.query(uri, null, selection, selectionArgs, sortOrder);
        if (cursor != null) {
            try {
                List<T> result = null;
                while (cursor.moveToNext()) {
                    if (result == null) {
                        result = new ArrayList<T>();
                    }
                    T entity = (T) CursorUtils.getEntity(cursor, entityClass);
                    result.add(entity);
                }
                return result;
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(cursor);
            }
        }
        return null;
    }

    /**
     * 按条件查询表记录
     *
     * @param sql
     * @param entityClass
     * @param selectionArgs
     * @param <T>
     * @return
     */
    @Override
    public <T> List<T> query(String sql, Class<?> entityClass, String[] selectionArgs) {
        String clazzName = TableUtils.getEntityClassName(entityClass);
        Uri queryUri = UriHelper.makeRowSQLUri(clazzName, sql, appAuthority);
        return query(queryUri, entityClass, null, selectionArgs, null);
    }

    @Override
    public <T> T queryEntity(Class<?> entityClass, Selector selector) {
        List<T> list = query(entityClass, selector);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public <T> T queryEntity(Class<?> entityClass, String selection, String[] selectionArgs, String sortOrder) {
        List<T> list = query(entityClass, selection, selectionArgs, sortOrder);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public <T> T queryEntity(String sql, Class<?> entityClass, String[] selectionArgs) {
        List<T> list = query(sql, entityClass, selectionArgs);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }


    /**
     * 删除表所有记录
     *
     * @param entityType
     */
    @Override
    public void deleteAll(Class<?> entityType) {
        delete(entityType, null);
    }

    /**
     * 删除指定几条记录
     *
     * @param entities
     * @return
     */
    @Override
    public int deleteAll(List<?> entities) {
        try {
            if (entities == null || entities.size() == 0) {
                return 0;
            }
            for (Object entity : entities) {
                delete(entity);
            }
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 删除表某一条记录
     *
     * @param entity
     * @return 0表示删除失败，>0表删除成功
     */
    @Override
    public int delete(Object entity) {
        try {
            if (entity == null) {
                return 0;
            }
            Class<?> entityType = entity.getClass();
            Table table = Table.get(entityType);
            Object idValue = table.id.getColumnValue(entity);
            return deleteById(entityType, idValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 按条件删除表记录
     *
     * @param entityType
     * @param idValue    记录的id
     * @return
     */
    private int deleteById(Class<?> entityType, Object idValue) {
        try {
            Table table = Table.get(entityType);
            WhereBuilder whereBuilder = WhereBuilder.b(table.id.getColumnName(), "=", idValue);
            return delete(entityType, whereBuilder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 按条件删除表记录
     *
     * @param entityType
     * @param whereBuilder
     * @return
     */
    @Override
    public int delete(Class<?> entityType, WhereBuilder whereBuilder) {
        try {
            String selection = null;
            String[] selectionArgs = null;
            if (whereBuilder != null) {
                selection = whereBuilder.getSelection();
                selectionArgs = whereBuilder.getSelectionArgs();
            }
            String tableName = TableUtils.getTableName(entityType);
            String clazzName = TableUtils.getEntityClassName(entityType);

            Uri queryUri = UriHelper.makeTableAccessUri(tableName, clazzName, appAuthority);
            return delete(queryUri, selection, selectionArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 按条件删除记录
     *
     * @param sql
     * @param selection
     * @param selectionArgs
     * @return

     @Override public int deleteFromSql(String sql, String selection, String[] selectionArgs) {
     Uri queryUri = UriHelper.makeRowSQLUri(sql, appAuthority);
     return delete(queryUri, selection, selectionArgs);
     }
     */

    /**
     * 内容提供者对接接口
     *
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    private int delete(Uri uri, String selection, String[] selectionArgs) {
        int result = resolver.delete(uri, selection, selectionArgs);
        if (result >= 0) {
            return 1;
        }
        return 0;
    }

    /**
     * 更新内容
     *
     * @param entity
     * @param updateColumnNames 指定字段
     * @return
     */
    @Override
    public int update(Object entity, String... updateColumnNames) {
        try {
            if (entity == null) {
                return 0;
            }
            return update(entity, null, updateColumnNames);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 更新内容
     *
     * @param entity
     * @param whereBuilder
     * @param updateColumnNames
     * @return
     */
    @Override
    public int update(Object entity, WhereBuilder whereBuilder, String... updateColumnNames) {
        try {
            if (entity == null) {
                return 0;
            }
            List<KeyValue> keyValueList = SqlInfoBuilder.entity2KeyValueListAll(entity);
            if (keyValueList.size() == 0)
                return 0;
            HashSet<String> updateColumnNameSet = null;
            if (updateColumnNames != null && updateColumnNames.length > 0) {
                updateColumnNameSet = new HashSet<String>(updateColumnNames.length);
                Collections.addAll(updateColumnNameSet, updateColumnNames);
            }
            ContentValues values = DbUtils.getContentValues(entity, updateColumnNameSet, keyValueList);
            String selection = null;
            String[] selectionArgs = null;
            if (whereBuilder == null) {
                Class<?> entityType = entity.getClass();
                Table table = Table.get(entityType);
                Id id = table.id;
                Object idValue = id.getColumnValue(entity);
                if (idValue == null) {
                    LogUtils.e("对象columnId不能为空");
                    return 0;
                }
                whereBuilder = WhereBuilder.b(id.getColumnName(), "=", idValue);
            }
            selection = whereBuilder.getSelection();
            selectionArgs = whereBuilder.getSelectionArgs();
            Uri uri = DbUtils.getUri(entity, ACCESS_URI, appAuthority);
            return resolver.update(uri, values, selection, selectionArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    //////////////////////////////////////////////////批量处理/////////////////////////////////////////////

    /**
     * 批量插入数据
     *
     * @param entities 实体类
     * @return 0表示插入错误，1表示插入成功
     */
    @Override
    public <T> int insertAll(List<T> entities) {
        try {
            if (entities != null && entities.size() > 0) {
                return getContentProviderOperation(entities, ACCESS_URI);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 批量插入或者更新数据
     *
     * @param entities 实体类
     * @return 0表示插入错误，1表示插入成功
     */
    @Override
    public <T> int insertOrUpdateAll(List<T> entities) {
        try {
            if (entities != null && entities.size() > 0) {
                return getContentProviderOperation(entities, REPLACE_URI);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 批量更新内容
     *
     * @param entities
     * @param updateColumnNames
     * @return
     */
    @Override
    public int updateAll(List<?> entities, String... updateColumnNames) {
        try {
            if (entities == null || entities.size() == 0) {
                return 0;
            }
            return getContentProviderOperation(entities, null, updateColumnNames);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 批量更新内容
     *
     * @param entities
     * @param whereBuilder
     * @param updateColumnNames
     * @return
     */
    @Override
    public int updateAll(List<?> entities, WhereBuilder whereBuilder, String... updateColumnNames) {
        try {
            if (entities == null || entities.size() == 0) {
                return 0;
            }
            return getContentProviderOperation(entities, whereBuilder, updateColumnNames);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private <T> int getContentProviderOperation(List<T> entities, int uriType) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        if (entities != null && entities.size() > 0) {
            for (T entity : entities) {
                Uri uri = DbUtils.getUri(entity, uriType, appAuthority);
                ContentValues contentValues = DbUtils.getContentValues(entity, true);
                ops.add(ContentProviderOperation.newInsert(uri).withValues(contentValues).build());
            }
            return applyBatch(ops);
        }
        return 0;
    }

    private <T> int getContentProviderOperation(List<T> entities, WhereBuilder whereBuilder, String... updateColumnNames) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        if (entities != null && entities.size() > 0) {
            for (T entity : entities) {
                List<KeyValue> keyValueList = SqlInfoBuilder.entity2KeyValueListAll(entity);
                if (keyValueList.size() == 0)
                    return 0;
                HashSet<String> updateColumnNameSet = null;
                if (updateColumnNames != null && updateColumnNames.length > 0) {
                    updateColumnNameSet = new HashSet<String>(updateColumnNames.length);
                    Collections.addAll(updateColumnNameSet, updateColumnNames);
                }
                ContentValues contentValues = DbUtils.getContentValues(entity, updateColumnNameSet, keyValueList);
                String selection = null;
                String[] selectionArgs = null;
                WhereBuilder whereBuilderSigle = null;
                if (whereBuilder == null) {
                    Class<?> entityType = entity.getClass();
                    Table table = Table.get(entityType);
                    Id id = table.id;
                    Object idValue = id.getColumnValue(entity);
                    if (idValue == null) {
                        LogUtils.e("对象Id不能为空");
                        return 0;
                    }
                    whereBuilderSigle = WhereBuilder.b(id.getColumnName(), "=", idValue);
                } else {
                    whereBuilderSigle = whereBuilder;
                }
                selection = whereBuilderSigle.getSelection();
                selectionArgs = whereBuilderSigle.getSelectionArgs();
                Uri uri = DbUtils.getUri(entity, ACCESS_URI, appAuthority);
                ops.add(ContentProviderOperation.newUpdate(uri).withValues(contentValues).withSelection(selection, selectionArgs).build());
            }
            return applyBatch(ops);
        }
        return 0;
    }

    private int applyBatch(ArrayList<ContentProviderOperation> ops) {
        try {
            if (ops != null && ops.size() > 0) {
                ContentProviderResult[] result = resolver.applyBatch(appAuthority, ops);
                if (result.length > 0) {
                    return 1;
                }
            }
        } catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
