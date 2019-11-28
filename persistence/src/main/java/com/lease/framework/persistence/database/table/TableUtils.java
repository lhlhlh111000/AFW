package com.lease.framework.persistence.database.table;

import android.text.TextUtils;

import com.lease.framework.persistence.database.annotation.Table;
import com.lease.framework.persistence.database.converter.ColumnConverterFactory;
import com.lease.framework.core.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class TableUtils {

    /**
     * 缓存所有表除了主键外的字段
     * key: entityType.name
     */
    private static ConcurrentHashMap<String, HashMap<String, Column>> entityColumnsMap = new ConcurrentHashMap<String, HashMap<String, Column>>();
    /**
     * 缓存所有表的主键
     * key: entityType.name
     */
    private static ConcurrentHashMap<String, Id> entityIdMap = new ConcurrentHashMap<String, com.lease.framework.persistence.database.table.Id>();

    private static ConcurrentHashMap<String, String> tableNameMap = new ConcurrentHashMap<>();

    private TableUtils() {

    }

    /**
     * 获取表名，如果没有自定义，则用类的全称
     *
     * @param entityType
     * @return
     */
    public static String getTableName(Class<?> entityType) {
        String name = tableNameMap.get(entityType.getName());
        if (StringUtils.isNotEmpty(name)) {
            return name;
        }
        Table table = entityType.getAnnotation(Table.class);
        if (table == null || TextUtils.isEmpty(table.name())) {
            name = getEntityClassName(entityType);
        } else {
            name = table.name();
        }
        tableNameMap.putIfAbsent(entityType.getName(), name); // 多次放入也是同一个name
        return name;
    }


    /**
     * 把类的全名做表名称
     *
     * @param entityType
     * @return
     */
    public static String getEntityClassName(Class<?> entityType) {
        return entityType.getName().replace('.', '_');
    }

    public static String getExecAfterTableCreated(Class<?> entityType) {
        Table table = entityType.getAnnotation(Table.class);
        if (table != null) {
            return table.execAfterTableCreated();
        }
        return null;
    }

    /**
     * 获取表中所有字段
     */
    static synchronized HashMap<String, Column> getColumnMap(Class<?> entityType) {

        if (entityColumnsMap.containsKey(entityType.getName())) {
            return entityColumnsMap.get(entityType.getName());
        }

        HashMap<String, Column> columnMap = new HashMap<String, Column>();
        String primaryKeyFieldName = getPrimaryKeyFieldName(entityType);
        addColumns2Map(entityType, primaryKeyFieldName, columnMap);
        entityColumnsMap.put(entityType.getName(), columnMap);

        return columnMap;
    }

    public synchronized static HashMap<String, Column> getTableColumnNameMap(Class<?> entityType) {
        getColumnMap(entityType);
        return entityColumnsMap.get(entityType.getName());
    }

    /**
     * 获取除主键外所有字段
     *
     * @param entityType
     * @param primaryKeyFieldName
     * @param columnMap
     */
    private static void addColumns2Map(Class<?> entityType, String primaryKeyFieldName, HashMap<String, Column> columnMap) {
        if (Object.class.equals(entityType)) return;
        try {
            Field[] fields = entityType.getDeclaredFields();
            for (Field field : fields) {
                if (ColumnUtils.isTransient(field) || Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                if (ColumnConverterFactory.isSupportColumnConverter(field.getType())) {
                    if (!field.getName().equals(primaryKeyFieldName)) {
                        Column column = new Column(entityType, field);
                        boolean isHaveSameColumnName = isHaveSameColumnName(columnMap, column.getColumnName());//sqlite字段不区分大小写，判断是否是相同的字段
                        if (!isHaveSameColumnName) {
                            columnMap.put(column.getColumnName(), column);
                        }
                    }
                }
            }

            if (!Object.class.equals(entityType.getSuperclass())) {
                addColumns2Map(entityType.getSuperclass(), primaryKeyFieldName, columnMap);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /* package */
    public static Column getColumnOrId(Class<?> entityType, String columnName) {
        if (getPrimaryKeyColumnName(entityType).equals(columnName)) {
            return getId(entityType, entityType.getName());
        }
        return getColumnMap(entityType).get(columnName);
    }

    public static boolean isaTable(Class<?> entityType) {
        return getId(entityType, entityType.getName()) != null;
    }

    /**
     * 获取表的主键
     *
     * @param entityType entity
     * @param entityName entity name  该方法递归调用,这里要传入原始entityName
     * @return
     */
    static Id getId(Class<?> entityType, String entityName) {
        if (Object.class.equals(entityType)) {
            throw new RuntimeException("column 'columnId'  not found ！ nor  id");
        }

        if (entityIdMap.containsKey(entityType.getName())) {
            return entityIdMap.get(entityType.getName());
        }

        //是否有自定义的主键
        Field primaryKeyField = null;
        Field[] fields = entityType.getDeclaredFields();
        if (fields != null) {
            for (Field field : fields) {
                if (field.getAnnotation(com.lease.framework.persistence.database.annotation.Id.class) != null) {
                    primaryKeyField = field;
                    break;
                }
            }

            //默认columnId为主键
            if (primaryKeyField == null) {
                for (Field field : fields) {
                    if ("columnId".equals(field.getName())) {
                        primaryKeyField = field;
                        break;
                    }
                }
            }
        }
        //查找父类的主键
        if (primaryKeyField == null) {
            return getId(entityType.getSuperclass(), entityType.getName());
        }

        Id id = new Id(entityType, primaryKeyField);

        synchronized (TableUtils.class) {
            entityIdMap.put(entityName, id);
        }
        return id;
    }

    public static String getPrimaryKeyFieldName(Class<?> entityType) {
        com.lease.framework.persistence.database.table.Id id = getId(entityType, entityType.getName());
        return id == null ? null : id.getColumnField().getName();
    }

    private static String getPrimaryKeyColumnName(Class<?> entityType) {
        com.lease.framework.persistence.database.table.Id id = getId(entityType, entityType.getName());
        return id == null ? null : id.getColumnName();
    }

    /**
     * 获取 唯一键  如果没有则返回空列表
     * @param entityType  clazz
     * @return  list
     */
    public static List<Column> getUnique(Class<?> entityType) {
        com.lease.framework.persistence.database.table.Table table =
                com.lease.framework.persistence.database.table.Table.get(entityType);
        List<Column> list = new ArrayList<>();
        if (table != null) {
            HashMap<String, Column> map = table.columnMap;
            if (map != null) {
                for (String s : map.keySet()) {
                    Column column = map.get(s);
                    if (column != null && column.isUnique) {
                        list.add(column);
                    }
                }
            }
        }
        return list;
    }

    /**
     * 获取 联合约束
     * @param entityType clazz
     * @return  list
     */
    public static List<Column> getMultiUnique(Class<?> entityType) {
        com.lease.framework.persistence.database.table.Table table =
                com.lease.framework.persistence.database.table.Table.get(entityType);
        List<Column> list = new ArrayList<>();
        if (table != null) {
            HashMap<String, Column> map = table.columnMap;
            if (map != null) {
                for (String s : map.keySet()) {
                    Column column = map.get(s);
                    if (column != null && column.multiUnique) {
                        list.add(column);
                    }
                }
            }
        }
        return list;
    }


    /**
     * sqlite字段不区分大小写，判断是否是相同的字段
     *
     * @param columnMap
     * @param columnName
     * @return
     */
    public static boolean isHaveSameColumnName(HashMap<String, Column> columnMap, String columnName) {
        boolean theSame = false;
        for (String key : columnMap.keySet()) {
            if (key.toLowerCase().equals(columnName.toLowerCase())) {
                theSame = true;
                break;
            }
        }
        return theSame;
    }
}
