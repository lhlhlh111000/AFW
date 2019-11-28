package com.lease.framework.persistence.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.lease.framework.persistence.database.sqlite.SqlInfo;
import com.lease.framework.persistence.database.sqlite.SqlInfoBuilder;
import com.lease.framework.persistence.database.table.Column;
import com.lease.framework.persistence.database.table.Table;
import com.lease.framework.persistence.database.table.TableUtils;
import com.lease.framework.core.LogUtils;
import com.lease.framework.core.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lease.framework.core.LogUtils.e;

/**
 * db 升级辅助类
 * Created by hxd on 16/1/18.
 */
public class DbUpgradeHelper {
    private static final String sTAG = "DbUpgradeHelper";

    public static boolean alterColumn(DbManager dbManager, Class clazz, String newColumn, String newColumnType, String defaultValue) {
        String tableName = TableUtils.getTableName(clazz);
        try {
            String sql = "ALTER TABLE " + tableName + " ADD " + newColumn + " " + newColumnType + " DEFAULT " + defaultValue;
            LogUtils.d(sTAG, sql);
            dbManager.execNonQuery(sql);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 通过重新创建来完成升级表
     * 使用场景是要 delete 字段， 约束
     *
     * @param dbManager
     * @param clazz
     * @return
     */
    public static boolean recreateTable(DbManager dbManager, Class clazz) {
        try {
            SqlInfo info = SqlInfoBuilder.buildCreateTableSqlInfo(clazz);
            String tableName = TableUtils.getTableName(clazz);
            String tmpTableName = tableName + "_tmp";
            if (StringUtils.contains(info.getSql(), tableName)) {
                String newSql = StringUtils.replace(info.getSql(), tableName, tmpTableName);
                LogUtils.d(sTAG, newSql);
                dbManager.beginTransaction();//开启事务
                dbManager.execNonQuery(newSql);
                dbManager.execNonQuery("INSERT INTO " + tmpTableName + " SELECT * FROM " + tableName);
                dbManager.execNonQuery("DROP TABLE " + tableName);
                dbManager.execNonQuery("ALTER TABLE " + tmpTableName + " RENAME TO " + tableName);
                dbManager.endTransaction();
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 方法1：检查某表列是否存在
     *
     * @param db
     * @param tableName  表名
     * @param columnName 列名
     * @return
     */
    private static boolean checkColumnExist(SQLiteDatabase db, String tableName, String columnName) {
        boolean result = false;
        Cursor cursor = null;
        try {
            //查询一行
            cursor = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 0", null);
            result = cursor != null && cursor.getColumnIndex(columnName) != -1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return result;
    }

    /**
     * 添加新的字段
     */
    public static void addNewCoulumns(DbManager dbManager, Class<?> entityType, HashMap<String, Column> map) {
        try {
            if (map == null || map.size() == 0) {
                return;
            }
            if (dbManager.tableIsExist(entityType)) {
                String tableName = TableUtils.getTableName(entityType);
                for (Map.Entry<String, Column> entry : map.entrySet()) {
                    if (TextUtils.isEmpty(entry.getKey()) || entry.getValue() == null) {
                        continue;
                    }
                    Column column = entry.getValue();
                    if (!checkColumnExist(dbManager.getDatabase(), tableName, entry.getKey())) {
                        String sql = "ALTER TABLE " + tableName + " ADD " + entry.getKey() + " " + column.getColumnDbType() + " DEFAULT " + column.getDefaultValue();
                        e(sTAG, sql);
                        dbManager.execNonQuery(sql);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 把表名称转成类的全名
     *
     * @param tableName
     * @return
     */
    public static String changedTableNameToClassName(String tableName) {
        return tableName.replace('_', '.');
    }

    /**
     * 获取数据库中所有的表
     *
     * @param dbManager
     * @return
     */
    private static Class<?>[] getAllTableClass(DbManager dbManager) {
        if (dbManager == null) {
            return null;
        }
        Class<?>[] classArray = null;
        Cursor cursor = null;
        try {
            cursor = dbManager.execQuery("select name from sqlite_master where type='table' order by name");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor == null)
            return classArray;
        int columnCount = cursor.getColumnCount();
        classArray = new Class<?>[columnCount];
        List<Class<?>> test = new ArrayList<>();
        while (cursor.moveToNext()) {
            //遍历出表名
            String name = cursor.getString(0);
            if (StringUtils.isNull(name))
                continue;
            if (name.equals("android_metadata") || name.equals("sqlite_sequence"))
                continue;
            name = changedTableNameToClassName(name);
            LogUtils.d(sTAG, name);
            try {
                Class clazz = Class.forName(name);
                test.add(clazz);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (null != test && test.size() > 0) {
            int size = test.size();
            classArray = new Class<?>[size];
            for (int i = 0; i < size; i++) {
                Class<?> clazz = test.get(i);
                classArray[i] = clazz;
            }
        }
        return classArray;
    }

    /**
     * 自动更新所有的表结构
     */
    public static void autoUpdateAllTable(DbManager dbManager) {
        if (dbManager == null) {
            e("dbManager is null!!!");
            return;
        }
        Class<?>[] classArray = getAllTableClass(dbManager);//dbManager.getDaoConfig().getAllTableClassList();
        if (classArray != null && classArray.length > 0) {
            for (int i = 0; i < classArray.length; ++i) {
                try {
                    Class<?> entityType = classArray[i];
                    if (TableUtils.isaTable(entityType)) {
                        //查询匹配该表的字段是否完全存在
                        if (dbManager.tableIsExist(entityType)) {
                            checkTableAllColumn(dbManager, entityType);
                        }
                    }
                } catch (Exception e) {
                    e(e.getLocalizedMessage());
                }
            }
        } else {
            e("there is no table ??!!!!!!");
        }
    }

    /**
     * 检测表的所有字段是否都有字段
     *
     * @param entityType
     */
    public static void checkTableAllColumn(DbManager dbManager, Class<?> entityType) {
        try {
            String tableName = TableUtils.getTableName(entityType);
            Cursor cursor = dbManager.execQuery("SELECT * FROM " + tableName + " limit 0,1");
            if (cursor == null)
                return;
            Table table = Table.get(entityType);
            if (table == null || table.columnMap == null) {
                return;
            }
            HashMap<String, Column> classFieldMap = table.columnMap; //TableUtils.getTableColumnNameMap(entityType);
            if (classFieldMap != null) {
                HashMap<String, Column> classFieldMapTemp = new HashMap<>();
                classFieldMapTemp.putAll(classFieldMap);
                //获取model需要创建的所有字段
                LogUtils.e("cursor is not null");
                int columnCount = cursor.getColumnCount();
                for (int i = 0; i < columnCount; i++) {
                    String columnName = cursor.getColumnName(i);
                    if (classFieldMapTemp.containsKey(columnName)) {
                        classFieldMapTemp.remove(columnName);
                    }
                }
                DbUpgradeHelper.addNewCoulumns(dbManager, entityType, classFieldMapTemp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
