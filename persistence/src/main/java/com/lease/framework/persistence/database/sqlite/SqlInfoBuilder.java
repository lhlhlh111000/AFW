
package com.lease.framework.persistence.database.sqlite;

import com.lease.framework.persistence.database.table.*;
import com.lease.framework.core.LogUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * contentProvider 的接口查询 不太试用该类的方法
 * Build "insert", "replace",，"update", "delete" and "create" sql.
 */
public class SqlInfoBuilder {

    private SqlInfoBuilder() {
    }

    //*********************************************** insert sql ***********************************************

    public static SqlInfo buildInsertSqlInfo(Object entity) throws Exception {

        List<KeyValue> keyValueList = entity2KeyValueList(entity);
        if (keyValueList.size() == 0) return null;

        SqlInfo result = new SqlInfo();
        StringBuffer sqlBuffer = new StringBuffer();

        sqlBuffer.append("INSERT INTO ");
        sqlBuffer.append(TableUtils.getTableName(entity.getClass()));
        sqlBuffer.append(" (");
        for (KeyValue kv : keyValueList) {
            sqlBuffer.append(kv.key).append(",");
            result.addBindArgWithoutConverter(kv.value);
        }
        sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
        sqlBuffer.append(") VALUES (");

        int length = keyValueList.size();
        for (int i = 0; i < length; i++) {
            sqlBuffer.append("?,");
        }
        sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
        sqlBuffer.append(")");

        result.setSql(sqlBuffer.toString());

        return result;
    }

    //*********************************************** replace sql ***********************************************

    public static SqlInfo buildReplaceSqlInfo(Object entity) throws Exception {

        List<KeyValue> keyValueList = entity2KeyValueList(entity);
        if (keyValueList.size() == 0) return null;

        SqlInfo result = new SqlInfo();
        StringBuffer sqlBuffer = new StringBuffer();

        sqlBuffer.append("REPLACE INTO ");
        sqlBuffer.append(TableUtils.getTableName(entity.getClass()));
        sqlBuffer.append(" (");
        for (KeyValue kv : keyValueList) {
            sqlBuffer.append(kv.key).append(",");
            result.addBindArgWithoutConverter(kv.value);
        }
        sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
        sqlBuffer.append(") VALUES (");

        int length = keyValueList.size();
        for (int i = 0; i < length; i++) {
            sqlBuffer.append("?,");
        }
        sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
        sqlBuffer.append(")");

        result.setSql(sqlBuffer.toString());

        return result;
    }

    //*********************************************** delete sql ***********************************************

    private static String buildDeleteSqlByTableName(String tableName) {
        return "DELETE FROM " + tableName;
    }

    public static SqlInfo buildDeleteSqlInfo(Object entity) throws Exception {
        SqlInfo result = new SqlInfo();

        Class<?> entityType = entity.getClass();
        Table table = Table.get(entityType);
        Id id = table.id;
        Object idValue = id.getColumnValue(entity);

        if (idValue == null) {
            throw new Exception("this entity[" + entity.getClass() + "]'s id value is null");
        }
        StringBuilder sb = new StringBuilder(buildDeleteSqlByTableName(table.tableName));
        sb.append(" WHERE ").append(WithArgsWhereBuilder.b(id.getColumnName(), "=", idValue));

        result.setSql(sb.toString());

        return result;
    }

    public static SqlInfo buildDeleteSqlInfo(Class<?> entityType, Object idValue) throws Exception {
        SqlInfo result = new SqlInfo();

        Table table = Table.get(entityType);
        Id id = table.id;

        if (null == idValue) {
            throw new Exception("this entity[" + entityType + "]'s id value is null");
        }
        StringBuilder sb = new StringBuilder(buildDeleteSqlByTableName(table.tableName));
        sb.append(" WHERE ").append(WithArgsWhereBuilder.b(id.getColumnName(), "=", idValue));

        result.setSql(sb.toString());

        return result;
    }

    public static SqlInfo buildDeleteSqlInfo(Class<?> entityType, WhereBuilder whereBuilder) throws Exception {
        Table table = Table.get(entityType);
        StringBuilder sb = new StringBuilder(buildDeleteSqlByTableName(table.tableName));
        SqlInfo sqlInfo = new SqlInfo();
        if (whereBuilder != null && whereBuilder.getWhereItemSize() > 0) {
            sb.append(" WHERE ").append(whereBuilder.toString());
        }
        if (whereBuilder!=null && whereBuilder.getSelectionArgs() != null) {
            sqlInfo.addBindArgs((Object[])whereBuilder.getSelectionArgs());
        }
        sqlInfo.setSql(sb.toString());
        return sqlInfo;
    }

    //*********************************************** update sql ***********************************************

    public static SqlInfo buildUpdateSqlInfo(Object entity, String... updateColumnNames) throws Exception {

        List<KeyValue> keyValueList = entity2KeyValueList(entity);
        if (keyValueList.size() == 0) return null;

        HashSet<String> updateColumnNameSet = null;
        if (updateColumnNames != null && updateColumnNames.length > 0) {
            updateColumnNameSet = new HashSet<String>(updateColumnNames.length);
            Collections.addAll(updateColumnNameSet, updateColumnNames);
        }


        Class<?> entityType = entity.getClass();
        Table table = Table.get(entityType);
        Id id = table.id;
        Object idValue = id.getColumnValue(entity);

        if (null == idValue) {
            throw new Exception("this entity[" + entity.getClass() + "]'s id value is null");
        }

        SqlInfo result = new SqlInfo();
        StringBuffer sqlBuffer = new StringBuffer("UPDATE ");
        sqlBuffer.append(table.tableName);
        sqlBuffer.append(" SET ");
        for (KeyValue kv : keyValueList) {
            if (updateColumnNameSet == null || updateColumnNameSet.contains(kv.key)) {
                sqlBuffer.append(kv.key).append("=?,");
                result.addBindArgWithoutConverter(kv.value);
            }
        }
        sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
        sqlBuffer.append(" WHERE ").append(WithArgsWhereBuilder.b(id.getColumnName(), "=", idValue));

        result.setSql(sqlBuffer.toString());
        return result;
    }

    @Deprecated
    public static SqlInfo buildUpdateSqlInfo(Object entity, WhereBuilder whereBuilder, String... updateColumnNames) throws Exception {

        List<KeyValue> keyValueList = entity2KeyValueList(entity);
        if (keyValueList.size() == 0) return null;

        HashSet<String> updateColumnNameSet = null;
        if (updateColumnNames != null && updateColumnNames.length > 0) {
            updateColumnNameSet = new HashSet<String>(updateColumnNames.length);
            Collections.addAll(updateColumnNameSet, updateColumnNames);
        }

        Class<?> entityType = entity.getClass();
        String tableName = TableUtils.getTableName(entityType);

        SqlInfo result = new SqlInfo();
        StringBuffer sqlBuffer = new StringBuffer("UPDATE ");
        sqlBuffer.append(tableName);
        sqlBuffer.append(" SET ");
        for (KeyValue kv : keyValueList) {
            if (updateColumnNameSet == null || updateColumnNameSet.contains(kv.key)) {
                sqlBuffer.append(kv.key).append("=?,");
                result.addBindArgWithoutConverter(kv.value);
            }
        }
        sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
        if (whereBuilder != null && whereBuilder.getWhereItemSize() > 0) {
            sqlBuffer.append(" WHERE ").append(whereBuilder.toString());
        }
        if (whereBuilder!=null&& whereBuilder.getSelectionArgs() != null) {
            result.addBindArgs((Object[])whereBuilder.getSelectionArgs());
        }

        result.setSql(sqlBuffer.toString());


        return result;
    }

    //*********************************************** others ***********************************************

    /**
     * 创建表的SQL语句
     *
     * @param entityType
     * @return
     * @throws Exception
     */
    public static SqlInfo buildCreateTableSqlInfo(Class<?> entityType) throws Exception {
        LogUtils.d("create table --");
        Table table = Table.get(entityType);
        Id id = table.id;

        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("CREATE TABLE IF NOT EXISTS ");
        sqlBuffer.append(table.tableName);
        sqlBuffer.append(" ( ");

        if (id.isAutoIncrement()) {
            sqlBuffer.append("\"").append(id.getColumnName()).append("\"  ").append("INTEGER PRIMARY KEY AUTOINCREMENT,");
        } else {
            sqlBuffer.append("\"").append(id.getColumnName()).append("\"  ").append(id.getColumnDbType()).append(" PRIMARY KEY,");
        }

        List<Column> multiColumns = new ArrayList<>();

        Collection<Column> columns = table.columnMap.values();
        for (Column column : columns) {
            sqlBuffer.append("\"").append(column.getColumnName()).append("\"  ");
            sqlBuffer.append(column.getColumnDbType());
            if (ColumnUtils.isUnique(column.getColumnField())) {
                sqlBuffer.append(" UNIQUE");
            }
            if (ColumnUtils.isNotNull(column.getColumnField())) {
                sqlBuffer.append(" NOT NULL");
            }
            if (ColumnUtils.isMultiUnique(column.getColumnField())) {
                multiColumns.add(column);
            }
            String check = ColumnUtils.getCheck(column.getColumnField());
            if (check != null) {
                sqlBuffer.append(" CHECK(").append(check).append(")");
            }
            sqlBuffer.append(",");
        }
        // multi unique  for CONTSTRAINT
        if (!multiColumns.isEmpty()) {
            sqlBuffer.append(" UNIQUE (");
            for (Column column : multiColumns) {
                sqlBuffer.append(" \"").append(column.getColumnName()).append("\"  ").append(" ,");
            }
            sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
            sqlBuffer.append(" ) ").append(" ON CONFLICT REPLACE ");
        } else {
            sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
        }
        sqlBuffer.append(" )");
        LogUtils.d("create sql is :" + sqlBuffer.toString());
        return new SqlInfo(sqlBuffer.toString());
    }

    private static KeyValue column2KeyValue(Object entity, Column column) {
        KeyValue kv = null;
        String key = column.getColumnName();
        if (key != null) {
            Object value = column.getColumnValue(entity);
            value = value == null ? column.getDefaultValue() : value;
            kv = new KeyValue(key, value);
        }
        return kv;
    }

    /**
     * 获取实体类中的keyValue
     * 排除自动增长的id
     *
     * @param entity
     * @return
     */
    public static List<KeyValue> entity2KeyValueList(Object entity) {

        List<KeyValue> keyValueList = new ArrayList<KeyValue>();

        Class<?> entityType = entity.getClass();
        Table table = Table.get(entityType);
        Id id = table.id;

        if (!id.isAutoIncrement()) {
            Object idValue = id.getColumnValue(entity);
            KeyValue kv = new KeyValue(id.getColumnName(), idValue);
            keyValueList.add(kv);
        }

        Collection<Column> columns = table.columnMap.values();
        for (Column column : columns) {
            KeyValue kv = column2KeyValue(entity, column);
            if (kv != null) {
                keyValueList.add(kv);
            }
        }

        return keyValueList;
    }

    /**
     * 获取实体类中的keyValue
     *
     * @param entity
     * @return
     */
    public static List<KeyValue> entity2KeyValueListAll(Object entity) {

        List<KeyValue> keyValueList = new ArrayList<KeyValue>();

        Class<?> entityType = entity.getClass();
        Table table = Table.get(entityType);
        Id id = table.id;

        Object idValue = id.getColumnValue(entity);
        KeyValue kvId = new KeyValue(id.getColumnName(), idValue);
        keyValueList.add(kvId);

        Collection<Column> columns = table.columnMap.values();
        for (Column column : columns) {
            KeyValue kv = column2KeyValue(entity, column);
            if (kv != null) {
                keyValueList.add(kv);
            }
        }

        return keyValueList;
    }
}
