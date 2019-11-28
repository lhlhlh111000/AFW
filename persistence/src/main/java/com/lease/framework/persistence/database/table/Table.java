
package com.lease.framework.persistence.database.table;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

public class Table {

    //    public final DbManager db;
    public final String tableName;
    public final Id id;

    /**
     * key: columnName
     */
    public final HashMap<String, Column> columnMap;
    public Class<?> entityType;
    /**
     * key: className
     * 注意 这里的 key 不是 tableName 而是entityType.getName()
     */
    private static final HashMap<String, Table> tableMap = new HashMap<String, Table>();

    private Table(Class<?> entityType) {
        this.tableName = TableUtils.getTableName(entityType);
        this.id = TableUtils.getId(entityType,entityType.getName());
        this.columnMap = TableUtils.getColumnMap(entityType);
        this.entityType = entityType;
    }

    public static synchronized Table get(Class<?> entityType) {
        String tableKey =entityType.getName();
        Table table = tableMap.get(tableKey);
        if (table == null) {
            table = new Table(entityType);
            tableMap.put(tableKey, table);
        }
        return table;
    }

    public static synchronized void remove(Class<?> entityType) {
        String tableKey =entityType.getName();
        tableMap.remove(tableKey);
    }

    public static synchronized void remove(String tableName) {
        if (tableMap.size() > 0) {
            String key = null;
            for (Map.Entry<String, Table> entry : tableMap.entrySet()) {
                Table table = entry.getValue();
                if (table != null && table.tableName.equals(tableName)) {
                    key = entry.getKey();
                }
            }
            if (!TextUtils.isEmpty(key)) {
                tableMap.remove(key);
            }
        }
    }

    private boolean checkedDatabase;

    public boolean isCheckedDatabase() {
        return checkedDatabase;
    }

    public void setCheckedDatabase(boolean checkedDatabase) {
        this.checkedDatabase = checkedDatabase;
    }

    public Class<?> getEntityType() {
        return entityType;
    }

}
