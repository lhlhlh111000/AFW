package com.lease.framework.persistence.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.text.TextUtils;

import com.lease.framework.core.IOUtils;
import com.lease.framework.core.LogUtils;
import com.lease.framework.persistence.database.sqlite.CursorUtils;
import com.lease.framework.persistence.database.sqlite.Selector;
import com.lease.framework.persistence.database.sqlite.SqlInfo;
import com.lease.framework.persistence.database.sqlite.SqlInfoBuilder;
import com.lease.framework.persistence.database.sqlite.WhereBuilder;
import com.lease.framework.persistence.database.table.Column;
import com.lease.framework.persistence.database.table.Id;
import com.lease.framework.persistence.database.table.Table;
import com.lease.framework.persistence.database.table.TableUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 简单的DB操作封装 未使用ContentProvider
 * Created by hxd on 15/11/30.
 */
public class DefaultBaseDAO implements BaseDAO {
    private boolean allowTransaction = false;
    private SQLiteDatabase database;
    private boolean debug = false;
    private Lock writeLock = new ReentrantLock();
    private volatile boolean writeLocked = false;

    public DefaultBaseDAO(SQLiteDatabase database) {
        this.database = database;
    }

    public Cursor execQuery(SqlInfo sqlInfo) throws Exception {
        debugSql(sqlInfo.getSql());
        try {
            return database.rawQuery(sqlInfo.getSql(), sqlInfo.getBindArgsAsStrArray());
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    public Cursor execQuery(String sql) throws Exception {
        debugSql(sql);
        try {
            return database.rawQuery(sql, null);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    /**
     * 表是否存在
     *
     * @param entityType
     * @return
     * @throws Exception
     */
    public boolean tableIsExist(Class<?> entityType) throws Exception {
        Table table = Table.get(entityType);
        if (table.isCheckedDatabase()) {
            return true;
        }
        Cursor cursor = execQuery("SELECT COUNT(*) AS c FROM sqlite_master WHERE type='table' AND name='" + table.tableName + "'");
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    int count = cursor.getInt(0);
                    if (count > 0) {
                        table.setCheckedDatabase(true);
                        return true;
                    }
                }
            } catch (Throwable e) {
                throw new Exception(e);
            } finally {
                IOUtils.closeQuietly(cursor);
            }
        }
        return false;
    }

    public void execNonQuery(SqlInfo sqlInfo) throws Exception {
        debugSql(sqlInfo.getSql());
        try {
            if (sqlInfo.getBindArgs() != null) {
                database.execSQL(sqlInfo.getSql(), sqlInfo.getBindArgsAsArray());
            } else {
                database.execSQL(sqlInfo.getSql());
            }
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    private void debugSql(String sql) {
        if (debug) {
            LogUtils.e("sql-->" + sql);
        }
    }

    public void execNonQuery(String sql) throws Exception {
        debugSql(sql);
        try {
            database.execSQL(sql);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    public void createTableIfNotExist(Class<?> entityType) throws Exception {
        if (!tableIsExist(entityType)) {
            SqlInfo sqlInfo = SqlInfoBuilder.buildCreateTableSqlInfo(entityType);
            execNonQuery(sqlInfo);
            String execAfterTableCreated = TableUtils.getExecAfterTableCreated(entityType);
            if (!TextUtils.isEmpty(execAfterTableCreated)) {
                execNonQuery(execAfterTableCreated);
            }
        }
    }

    @Override
    public int insert(Object entity) {
        try {
            beginTransaction();
            createTableIfNotExist(entity.getClass());
            execNonQuery(SqlInfoBuilder.buildInsertSqlInfo(entity));

            setTransactionSuccessful();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            endTransaction();
        }
        return 1;
    }

    @Override
    public <T> int insertAll(List<T> entities) {
        if (entities == null || entities.size() == 0) return 0;
        try {
            beginTransaction();

            createTableIfNotExist(entities.get(0).getClass());
            for (Object entity : entities) {
                execNonQuery(SqlInfoBuilder.buildInsertSqlInfo(entity));
            }

            setTransactionSuccessful();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            endTransaction();
        }
        return 1;
    }

    @Override
    public int insertOrUpdate(Object entity) {
        try {
            beginTransaction();

            createTableIfNotExist(entity.getClass());
            saveOrUpdateWithoutTransaction(entity);

            setTransactionSuccessful();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            endTransaction();
        }
        return 1;
    }

    private void saveOrUpdateWithoutTransaction(Object entity) throws Exception {
        Table table = Table.get(entity.getClass());
        Id id = table.id;
        if (id.isAutoIncrement()) {
            if (id.getColumnValue(entity) != null) {
                execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo(entity));
            } else {
                List<Column> uniqueList = TableUtils.getUnique(entity.getClass());
                List<Column> multiUniqueList = TableUtils.getMultiUnique(entity.getClass());
                uniqueList.addAll(multiUniqueList);
                if (uniqueList.isEmpty()) {
                    saveBindingIdWithoutTransaction(entity);
                } else {
                    execNonQuery(SqlInfoBuilder.buildReplaceSqlInfo(entity));
                }
            }
        } else {
            execNonQuery(SqlInfoBuilder.buildReplaceSqlInfo(entity));
        }
    }

    /**
     private boolean updateByConstraintWithoutTransaction(List<Column> uniqueList, Object entity) {
     if (uniqueList == null || uniqueList.isEmpty()) {
     return true;
     }
     try {
     WhereBuilder whereBuilder = WhereBuilder.b();
     for (Column column : uniqueList) {
     whereBuilder.and(column.getColumnName(), "=", column.getColumnValue(entity));
     }
     execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo(entity, whereBuilder));
     } catch (Exception e) {
     e.printStackTrace();
     }
     return true;
     }  **/

    private boolean saveBindingIdWithoutTransaction(Object entity) throws Exception {
        Class<?> entityType = entity.getClass();
        Table table = Table.get(entityType);
        Id idColumn = table.id;
        if (idColumn.isAutoIncrement()) {
            execNonQuery(SqlInfoBuilder.buildInsertSqlInfo(entity));
            long id = getLastAutoIncrementId(table.tableName);
            if (id == -1) {
                return false;
            }
            idColumn.setAutoIncrementId(entity, id);
            return true;
        } else {
            execNonQuery(SqlInfoBuilder.buildInsertSqlInfo(entity));
            return true;
        }
    }

    private long getLastAutoIncrementId(String tableName) throws Exception {
        long id = -1;
        Cursor cursor = execQuery("SELECT seq FROM sqlite_sequence WHERE name='" + tableName + "'");
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    id = cursor.getLong(0);
                }
            } catch (Throwable e) {
                throw new Exception(e);
            } finally {
                IOUtils.closeQuietly(cursor);
            }
        }
        return id;
    }

    @Override
    public <T> int insertOrUpdateAll(List<T> entities) {
        if (entities == null || entities.size() == 0) return 0;
        try {
            beginTransaction();

            createTableIfNotExist(entities.get(0).getClass());
            for (Object entity : entities) {
                saveOrUpdateWithoutTransaction(entity);
            }

            setTransactionSuccessful();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            endTransaction();
        }
        return 1;
    }


    @SuppressWarnings("unchecked")
    public <T> T findFirst(Selector selector) throws Exception {
        if (!tableIsExist(selector.getEntityType())) return null;

        String sql = selector.limit(1).toString();

        Cursor cursor = execQuery(sql);
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    T entity = (T) CursorUtils.getEntity(cursor, selector.getEntityType());
                    return entity;
                }
            } catch (Throwable e) {
                throw new Exception(e);
            } finally {
                IOUtils.closeQuietly(cursor);
            }
        }
        return null;
    }

    @Override
    public <T> T queryFirst(Class<T> entityClass) {
        try {
            return findFirst(Selector.from(entityClass));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T queryById(Class<T> entityType, Object idValue) {
        try {
            if (!tableIsExist(entityType)) return null;
            Table table = Table.get(entityType);
            Selector selector = Selector.from(entityType).where(table.id.getColumnName(), "=", idValue);
            String sql = selector.toString();
            String[] args = selector.getWhereBuilder() == null ? null : selector.getWhereBuilder().getSelectionArgs();
            Cursor cursor = null;
            if (args != null) {
                cursor = execQuery(new SqlInfo(sql,(Object[]) args));
            } else {
                cursor = execQuery(sql);
            }
            if (cursor != null) {
                try {
                    if (cursor.moveToNext()) {
                        T entity = (T) CursorUtils.getEntity(cursor, entityType);
                        return entity;
                    }
                } catch (Throwable e) {
                    throw new Exception(e);
                } finally {
                    IOUtils.closeQuietly(cursor);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public <T> List<T> queryAll(Class<?> entityType) {
        try {
            Selector selector = Selector.from(entityType);
            return queryAllFromSelector(selector);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> List<T> queryAllFromSelector(Selector selector) throws Exception {
        if (!tableIsExist(selector.getEntityType())) return null;
        String sql = selector.toString();
        List<T> result = new ArrayList<T>();
        String[] args = selector.getWhereBuilder() == null ? null : selector.getWhereBuilder().getSelectionArgs();
        Cursor cursor = null;
        if (args != null) {
            cursor = execQuery(new SqlInfo(sql, (Object[])args));
        } else {
            cursor = execQuery(sql);
        }

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    T entity = (T) CursorUtils.getEntity(cursor, selector.getEntityType());
                    result.add(entity);
                }
            } catch (Throwable e) {
                throw new Exception(e);
            } finally {
                IOUtils.closeQuietly(cursor);
            }
        }
        return result;
    }

    @Override
    public <T> List<T> query(Class<?> entityClass, Selector selector) {
        try {
            return queryAllFromSelector(selector);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> List<T> query(Class<?> entityClass, String selection, String[] selectionArgs, String sortOrder) {
        String sql = SQLiteQueryBuilder.buildQueryString(
                false, TableUtils.getTableName(entityClass), null, selection, null, null, sortOrder, null);
        Cursor cursor = database.rawQuery(sql, selectionArgs);
        try {
            return parseCursor(entityClass, cursor);
        } catch (Exception e) {
            LogUtils.e(e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public <T> List<T> query(String sql, Class<?> entityClass, String[] selectionArgs) {
        try {
            Cursor cursor = execQuery(new SqlInfo(sql, (Object[]) selectionArgs));
            return parseCursor(entityClass, cursor);
        } catch (Exception e) {
            LogUtils.e(e.getLocalizedMessage());
        }
        return null;
    }

    private <T> List<T> parseCursor(Class<?> entityClass, Cursor cursor) throws Exception {
        List<T> result = new ArrayList<T>();
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    T entity = (T) CursorUtils.getEntity(cursor, entityClass);
                    result.add(entity);
                }
            } catch (Throwable e) {
                throw new Exception(e);
            } finally {
                IOUtils.closeQuietly(cursor);
            }
        }
        return result;
    }

    @Override
    public <T> T queryEntity(Class<?> entityClass, Selector selector) {
        try {
            List<T> list = queryAllFromSelector(selector);
            if (list != null && list.size() >= 1) {
                return list.get(0);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public <T> T queryEntity(Class<?> entityClass, String selection, String[] selectionArgs, String sortOrder) {
        String sql = SQLiteQueryBuilder.buildQueryString(
                false, TableUtils.getTableName(entityClass), null, selection, null, null, sortOrder, "1");
        Cursor cursor = database.rawQuery(sql, selectionArgs);
        try {
            List<T> list = parseCursor(entityClass, cursor);
            if (list != null && list.size() > 0) {
                return list.get(0);
            }
        } catch (Exception e) {
            LogUtils.e(e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public <T> T queryEntity(String sql, Class<?> entityClass, String[] selectionArgs) {
        List<T> list = query(sql, entityClass, selectionArgs);
        return (list != null && list.size() > 0) ? list.get(0) : null;
    }

    @Override
    public int delete(Object entity) {
        try {
            if (!tableIsExist(entity.getClass())) return 0;
            beginTransaction();

            execNonQuery(SqlInfoBuilder.buildDeleteSqlInfo(entity));

            setTransactionSuccessful();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            endTransaction();
        }
        return 1;
    }

    @Override
    public int delete(Class<?> entityType, WhereBuilder whereBuilder) {
        try {
            if (!tableIsExist(entityType)) return 0;
            beginTransaction();

            execNonQuery(SqlInfoBuilder.buildDeleteSqlInfo(entityType, whereBuilder));

            setTransactionSuccessful();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            endTransaction();
        }
        return 1;
    }

    @Override
    public int deleteAll(List<?> entities) {
        try {
            if (entities == null || entities.size() == 0 ||
                    !tableIsExist(entities.get(0).getClass()))
                return 0;
            beginTransaction();

            for (Object entity : entities) {
                execNonQuery(SqlInfoBuilder.buildDeleteSqlInfo(entity));
            }

            setTransactionSuccessful();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            endTransaction();
        }
        return 1;
    }

    @Override
    public void deleteAll(Class<?> entityType) {
        delete(entityType, null);
    }

    @Override
    public int update(Object entity, String... updateColumnNames) {
        try {
            if (!tableIsExist(entity.getClass())) return 0;
            try {
                beginTransaction();

                execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo(entity, updateColumnNames));

                setTransactionSuccessful();
            } finally {
                endTransaction();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return 1;
    }

    @Override
    public int update(Object entity, WhereBuilder whereBuilder, String... updateColumnNames) {
        try {
            if (!tableIsExist(entity.getClass())) return 0;
            try {
                beginTransaction();

                execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo(entity, whereBuilder, updateColumnNames));

                setTransactionSuccessful();
            } finally {
                endTransaction();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return 1;
    }

    @Override
    public int updateAll(List<?> entities, String... updateColumnNames) {
        try {
            if (entities == null || entities.size() == 0 || !tableIsExist(entities.get(0).getClass()))
                return 0;
            try {
                beginTransaction();

                for (Object entity : entities) {
                    execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo(entity, updateColumnNames));
                }

                setTransactionSuccessful();
            } finally {
                endTransaction();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return 1;
    }

    @Override
    public int updateAll(List<?> entities, WhereBuilder whereBuilder, String... updateColumnNames) {
        try {
            if (entities == null || entities.size() == 0 || !tableIsExist(entities.get(0).getClass()))
                return 0;
            try {
                beginTransaction();

                for (Object entity : entities) {
                    execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo(entity, whereBuilder, updateColumnNames));
                }

                setTransactionSuccessful();
            } finally {
                endTransaction();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return 1;
    }

    public void beginTransaction() {
        if (allowTransaction) {
            database.beginTransaction();
        } else {
            if(!writeLocked) {
                writeLock.lock();
                writeLocked = true;
            }
        }
    }

    public void setTransactionSuccessful() {
        if (allowTransaction) {
            database.setTransactionSuccessful();
        }
    }

    public void endTransaction() {
        if (allowTransaction) {
            database.endTransaction();
        }
        if (writeLocked) {
            writeLock.unlock();
            writeLocked = false;
        }
    }
}
