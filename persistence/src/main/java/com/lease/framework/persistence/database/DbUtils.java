package com.lease.framework.persistence.database;

import android.content.ContentValues;
import android.net.Uri;

import com.lease.framework.persistence.database.sqlite.Selector;
import com.lease.framework.persistence.database.sqlite.SqlInfoBuilder;
import com.lease.framework.persistence.database.table.Column;
import com.lease.framework.persistence.database.table.Id;
import com.lease.framework.persistence.database.table.KeyValue;
import com.lease.framework.persistence.database.table.Table;
import com.lease.framework.persistence.database.table.TableUtils;
import com.lease.framework.core.LogUtils;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;

/**
 * Created by caishuxing on 2015/9/1.
 */
public class DbUtils {
    /**
     * 从实体类中获取ContentValues
     *
     * @param isRemovePrimary 是否排除主键
     * @return
     */
    public static ContentValues getContentValues(Object entity, boolean isRemovePrimary) {
        Class<?> entityType = entity.getClass();
        List<KeyValue> keyValueList = SqlInfoBuilder.entity2KeyValueListAll(entity);
        ContentValues contentValues = new ContentValues();
        try {
            for (KeyValue kv : keyValueList) {
                if (isRemovePrimary) {
                    Table table = Table.get(entityType);
                    Id id = table.id;
                    if (kv.equals(id.getColumnName())) {
                        continue;
                    }
                }
                Column column = TableUtils.getColumnOrId(entityType, kv.key);
                Field columnField = column.getColumnField();
                Object value = kv.value;
                Class<?> type = columnField.getType();
                if (type == String.class) {
                    contentValues.put(kv.key, (String) value);
                } else if (type == long.class || type == Long.class) {
                    contentValues.put(kv.key, (Long) value);
                } else if (type == double.class || type == Double.class) {
                    contentValues.put(kv.key, (Double) value);
                } else if (type == int.class || type == Integer.class) {
                    contentValues.put(kv.key, (Integer) value);
                } else if (type == boolean.class || type == Boolean.class) {
                    contentValues.put(kv.key, (Integer) value);
                } else if (type == byte.class || type == Byte.class) {
                    contentValues.put(kv.key, (Byte) value);
                } else if (type == float.class || type == Float.class) {
                    contentValues.put(kv.key, (Float) value);
                } else if (type == short.class || type == Short.class) {
                    contentValues.put(kv.key, (Short) value);
                } else {
                    LogUtils.e("没有对应的类型");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentValues;
    }

    public static Uri getUri(Object entity, int uriType, String appAuthority) {
        String tableName = TableUtils.getTableName(entity.getClass());
        String clazzName = TableUtils.getEntityClassName(entity.getClass());
        if (uriType == BaseContentResolver.ACCESS_URI) {
            return UriHelper.makeTableAccessUri(tableName, clazzName, appAuthority);
        } else {
            return UriHelper.makeTableReplaceAccessUri(tableName, clazzName, appAuthority);
        }
    }

    /**
     * 筛选条件字符串
     *
     * @param selector
     * @return
     */
    public static String getSortOrder(Class<?> entityClass, Selector selector) {
        List<Selector.OrderBy> orderByList = selector.getOrderByList();
        int limit = selector.getLimit();
        int offset = selector.getOffset();
        StringBuilder result = new StringBuilder();
        if (orderByList != null) {
            for (int i = 0; i < orderByList.size(); i++) {
                result.append(orderByList.get(i).toString());
                if (i < orderByList.size() - 2) {
                    result.append(",");
                }
            }
        } else {
            Table table = Table.get(entityClass);
            result.append(table.id.getColumnName());
            result.append(" ASC");
        }
        if (limit > 0) {
            result.append(" LIMIT ").append(limit);
            if (offset > 0) {
                result.append(" OFFSET ").append(offset);
            }
        }
        return result.toString();
    }

    /**
     * 获取ContentValues
     *
     * @param updateColumnNameSet
     * @param keyValueList
     * @return
     */
    public static ContentValues getContentValues(Object entity, HashSet<String> updateColumnNameSet, List<KeyValue> keyValueList) {
        ContentValues contentValues = new ContentValues();
        Class<?> entityType = entity.getClass();
        for (KeyValue kv : keyValueList) {
            if (updateColumnNameSet == null || updateColumnNameSet.contains(kv.key)) {
                Column column = TableUtils.getColumnOrId(entityType, kv.key);
                Field columnField = column.getColumnField();
                Object value = kv.value;
                Class<?> type = columnField.getType();
                if (type == String.class) {
                    contentValues.put(kv.key, (String) value);
                } else if (type == long.class || type == Long.class) {
                    contentValues.put(kv.key, (Long) value);
                } else if (type == double.class || type == Double.class) {
                    contentValues.put(kv.key, (Double) value);
                } else if (type == int.class || type == Integer.class) {
                    contentValues.put(kv.key, (Integer) value);
                } else if (type == boolean.class || type == Boolean.class) {
                    contentValues.put(kv.key, (Integer) value);
                } else if (type == byte.class || type == Byte.class) {
                    contentValues.put(kv.key, (Byte) value);
                } else if (type == float.class || type == Float.class) {
                    contentValues.put(kv.key, (Float) value);
                } else if (type == short.class || type == Short.class) {
                    contentValues.put(kv.key, (Short) value);
                } else {
                    LogUtils.e("没有对应的类型");
                }
            }
        }
        return contentValues;
    }
}
