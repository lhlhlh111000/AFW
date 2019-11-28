package com.lease.framework.persistence.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.lease.framework.persistence.database.sqlite.SqlInfo;
import com.lease.framework.persistence.database.sqlite.SqlInfoBuilder;
import com.lease.framework.persistence.database.table.Table;
import com.lease.framework.persistence.database.table.TableUtils;
import com.lease.framework.core.IOUtils;
import com.lease.framework.core.LogUtils;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 数据库管理
 */
public class DbManager {
    private static HashMap<String, DbManager> dbMap = new HashMap<String, DbManager>();
    private SQLiteDatabase database;
    private DaoConfig daoConfig;
    private boolean debug = false;
    private boolean allowTransaction = false;

    private DbManager() {

    }

    public static DbManager get(String dbName){
        return dbMap.get(dbName);
    }

    /**
     * 生成 dbmanager 如果已经创建就不再生成
     * @param daoConfig
     * @return
     */
    public static DbManager create(DaoConfig daoConfig) {
        if (daoConfig == null) {
            throw new IllegalArgumentException("daoConfig may not be null");
        }
        DbManager dbManager = dbMap.get(daoConfig.getDbName());
        synchronized (DbManager.class) {
            if (dbManager == null) {
                dbManager = new DbManager();
                dbManager.database = openOrCreateDatabase(daoConfig);
                dbManager.daoConfig = daoConfig;
                dbMap.put(daoConfig.getDbName(), dbManager);
            } else {
                dbManager.daoConfig = daoConfig;
            }
        }
        return dbManager;
    }
    /**
     * 初始化数据
     *
     */
    public DbManager init() {

        Class<?>[] classArray = daoConfig.getAllTableClassList();
        if (classArray != null && classArray.length > 0) {
            for (int i = 0; i < classArray.length; ++i) {
                try {
                    if (TableUtils.isaTable(classArray[i])) createTableIfNotExist(classArray[i]);
                } catch (Exception e) {
                    LogUtils.e("create table ex " + e.getLocalizedMessage());
                }
            }
        } else {
            LogUtils.e("there is no table need create ??!!!!!!");
        }
        int oldVersion = database.getVersion();
        int newVersion = daoConfig.getDbVersion();
        if (oldVersion < newVersion) {
            daoConfig.onUpgrade(this, oldVersion, newVersion);
            database.setVersion(newVersion);
        }
        return this;
    }

    private static SQLiteDatabase openOrCreateDatabase(DaoConfig config) {
        SQLiteDatabase result = null;
        String dbDir = config.getDbDir();
        if (!TextUtils.isEmpty(dbDir)) {
            File dir = new File(dbDir);
            if (dir.exists() || dir.mkdirs()) {
                File dbFile = new File(dbDir, config.getDbName());
                result = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
            } else {
                result = config.getContext().openOrCreateDatabase(config.getDbName(), 0, null);
            }
        } else {
            result = config.getContext().openOrCreateDatabase(config.getDbName(), 0, null);
        }
        return result;
    }

    public DbManager configDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public DbManager configAllowTransaction(boolean allowTransaction) {
        this.allowTransaction = allowTransaction;
        return this;
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public DaoConfig getDaoConfig() {
        return daoConfig;
    }

    //*********************************************** operations ********************************************************
    private Lock writeLock = new ReentrantLock();
    private volatile boolean writeLocked = false;

    public void beginTransaction() {
        if (allowTransaction) {
            database.beginTransaction();
        } else {
            writeLock.lock();
            writeLocked = true;
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

    public void createTable(Class<?> entityType) throws Exception {
        if (!tableIsExist(entityType)) {
            SqlInfo sqlInfo = SqlInfoBuilder.buildCreateTableSqlInfo(entityType);
            execNonQuery(sqlInfo);
            String execAfterTableCreated = TableUtils.getExecAfterTableCreated(entityType);
            if (!TextUtils.isEmpty(execAfterTableCreated)) {
                execNonQuery(execAfterTableCreated);
            }
        }
    }

    public void dropDb() throws Exception {
        Cursor cursor = execQuery("SELECT name FROM sqlite_master WHERE type='table' AND name<>'sqlite_sequence'");
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    try {
                        String tableName = cursor.getString(0);
                        execNonQuery("DROP TABLE " + tableName);
                        Table.remove(tableName);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }

            } catch (Throwable e) {
                throw new Exception(e);
            } finally {
                IOUtils.closeQuietly(cursor);
            }
        }
    }

    public void dropTable(Class<?> entityType) throws Exception {
        if (!tableIsExist(entityType)) return;
        String tableName = TableUtils.getTableName(entityType);
        execNonQuery("DROP TABLE " + tableName);
        Table.remove(entityType);
    }

    private void close() {
        String dbName = this.daoConfig.getDbName();
        if (dbMap.containsKey(dbName)) {
            dbMap.remove(dbName);
            this.database.close();
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

    public void execNonQuery(String sql) throws Exception {
        debugSql(sql);
        try {
            database.execSQL(sql);
        } catch (Throwable e) {
            throw new Exception(e);
        }
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

    private void debugSql(String sql) {
        if (debug) {
            LogUtils.e("sql-->" + sql);
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

   /* public void saveOrUpdate(Object entity) throws Exception {
        try {
            beginTransaction();

            createTableIfNotExist(entity.getClass());
            saveOrUpdateWithoutTransaction(entity);

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }*/

    /*public void saveOrUpdateAll(List<?> entities) throws Exception {
        if (entities == null || entities.size() == 0) return;
        try {
            beginTransaction();

            createTableIfNotExist(entities.get(0).getClass());
            for (Object entity : entities) {
                saveOrUpdateWithoutTransaction(entity);
            }

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }*/

   /* public void replace(Object entity) throws Exception {
        try {
            beginTransaction();

            createTableIfNotExist(entity.getClass());
            execNonQuery(SqlInfoBuilder.buildReplaceSqlInfo( entity));

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }*/

   /* public void replaceAll(List<?> entities) throws Exception {
        if (entities == null || entities.size() == 0) return;
        try {
            beginTransaction();

            createTableIfNotExist(entities.get(0).getClass());
            for (Object entity : entities) {
                execNonQuery(SqlInfoBuilder.buildReplaceSqlInfo( entity));
            }

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }
*/
   /* public void save(Object entity) throws Exception {
        try {
            beginTransaction();

            createTableIfNotExist(entity.getClass());
            execNonQuery(SqlInfoBuilder.buildInsertSqlInfo( entity));

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    public void saveAll(List<?> entities) throws Exception {
        if (entities == null || entities.size() == 0) return;
        try {
            beginTransaction();

            createTableIfNotExist(entities.get(0).getClass());
            for (Object entity : entities) {
                execNonQuery(SqlInfoBuilder.buildInsertSqlInfo( entity));
            }

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    public boolean saveBindingId(Object entity) throws Exception {
        boolean result = false;
        try {
            beginTransaction();

            createTableIfNotExist(entity.getClass());
            result = saveBindingIdWithoutTransaction(entity);

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
        return result;
    }

    public void saveBindingIdAll(List<?> entities) throws Exception {
        if (entities == null || entities.size() == 0) return;
        try {
            beginTransaction();

            createTableIfNotExist(entities.get(0).getClass());
            for (Object entity : entities) {
                if (!saveBindingIdWithoutTransaction(entity)) {
                    throw new Exception("saveBindingId error, transaction will not commit!");
                }
            }

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    public void deleteById(Class<?> entityType, Object idValue) throws Exception {
        if (!tableIsExist(entityType)) return;
        try {
            beginTransaction();

            execNonQuery(SqlInfoBuilder.buildDeleteSqlInfo( entityType, idValue));

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    public void delete(Object entity) throws Exception {
        if (!tableIsExist(entity.getClass())) return;
        try {
            beginTransaction();

            execNonQuery(SqlInfoBuilder.buildDeleteSqlInfo( entity));

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    public void delete(Class<?> entityType, WhereBuilder whereBuilder) throws Exception {
        if (!tableIsExist(entityType)) return;
        try {
            beginTransaction();

            execNonQuery(SqlInfoBuilder.buildDeleteSqlInfo( entityType, whereBuilder));

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    public void deleteAll(List<?> entities) throws Exception {
        if (entities == null || entities.size() == 0 || !tableIsExist(entities.get(0).getClass()))
            return;
        try {
            beginTransaction();

            for (Object entity : entities) {
                execNonQuery(SqlInfoBuilder.buildDeleteSqlInfo( entity));
            }

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    public void deleteAll(Class<?> entityType) throws Exception {
        delete(entityType, null);
    }

    public void update(Object entity, String... updateColumnNames) throws Exception {
        if (!tableIsExist(entity.getClass())) return;
        try {
            beginTransaction();

            execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo( entity, updateColumnNames));

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    public void update(Object entity, WhereBuilder whereBuilder, String... updateColumnNames) throws Exception {
        if (!tableIsExist(entity.getClass())) return;
        try {
            beginTransaction();

            execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo( entity, whereBuilder, updateColumnNames));

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    public void updateAll(List<?> entities, String... updateColumnNames) throws Exception {
        if (entities == null || entities.size() == 0 || !tableIsExist(entities.get(0).getClass()))
            return;
        try {
            beginTransaction();

            for (Object entity : entities) {
                execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo( entity, updateColumnNames));
            }

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    public void updateAll(List<?> entities, WhereBuilder whereBuilder, String... updateColumnNames) throws Exception {
        if (entities == null || entities.size() == 0 || !tableIsExist(entities.get(0).getClass()))
            return;
        try {
            beginTransaction();

            for (Object entity : entities) {
                execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo( entity, whereBuilder, updateColumnNames));
            }

            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T findById(Class<T> entityType, Object idValue) throws Exception {
        if (!tableIsExist(entityType)) return null;

        Table table = Table.get( entityType);
        Selector selector = Selector.from(entityType).where(table.id.getColumnName(), "=", idValue);

        String sql = selector.limit(1).toString();
        long seq = CursorUtils.FindCacheSequence.getSeq();
        findTempCache.setSeq(seq);
        Object obj = findTempCache.get(sql);
        if (obj != null) {
            return (T) obj;
        }

        Cursor cursor = execQuery(sql);
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    T entity = (T) CursorUtils.getEntity( cursor, entityType, seq);
                    findTempCache.put(sql, entity);
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

    @SuppressWarnings("unchecked")
    public <T> T findFirst(Selector selector) throws Exception {
        if (!tableIsExist(selector.getEntityType())) return null;

        String sql = selector.limit(1).toString();
        long seq = CursorUtils.FindCacheSequence.getSeq();
        findTempCache.setSeq(seq);
        Object obj = findTempCache.get(sql);
        if (obj != null) {
            return (T) obj;
        }

        Cursor cursor = execQuery(sql);
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    T entity = (T) CursorUtils.getEntity( cursor, selector.getEntityType(), seq);
                    findTempCache.put(sql, entity);
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

    public <T> T findFirst(Class<T> entityType) throws Exception {
        return findFirst(Selector.from(entityType));
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> findAll(Selector selector) throws Exception {
        if (!tableIsExist(selector.getEntityType())) return null;

        String sql = selector.toString();
        long seq = CursorUtils.FindCacheSequence.getSeq();
        findTempCache.setSeq(seq);
        Object obj = findTempCache.get(sql);
        if (obj != null) {
            return (List<T>) obj;
        }

        List<T> result = new ArrayList<T>();

        Cursor cursor = execQuery(sql);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    T entity = (T) CursorUtils.getEntity( cursor, selector.getEntityType(), seq);
                    result.add(entity);
                }
                findTempCache.put(sql, result);
            } catch (Throwable e) {
                throw new Exception(e);
            } finally {
                IOUtils.closeQuietly(cursor);
            }
        }
        return result;
    }

    public <T> List<T> findAll(Class<T> entityType) throws Exception {
        return findAll(Selector.from(entityType));
    }

    public DbModel findDbModelFirst(SqlInfo sqlInfo) throws Exception {
        Cursor cursor = execQuery(sqlInfo);
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    return CursorUtils.getDbModel(cursor);
                }
            } catch (Throwable e) {
                throw new Exception(e);
            } finally {
                IOUtils.closeQuietly(cursor);
            }
        }
        return null;
    }

    public DbModel findDbModelFirst(DbModelSelector selector) throws Exception {
        if (!tableIsExist(selector.getEntityType())) return null;

        Cursor cursor = execQuery(selector.limit(1).toString());
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    return CursorUtils.getDbModel(cursor);
                }
            } catch (Throwable e) {
                throw new Exception(e);
            } finally {
                IOUtils.closeQuietly(cursor);
            }
        }
        return null;
    }

    public List<DbModel> findDbModelAll(SqlInfo sqlInfo) throws Exception {
        List<DbModel> dbModelList = new ArrayList<DbModel>();

        Cursor cursor = execQuery(sqlInfo);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    dbModelList.add(CursorUtils.getDbModel(cursor));
                }
            } catch (Throwable e) {
                throw new Exception(e);
            } finally {
                IOUtils.closeQuietly(cursor);
            }
        }
        return dbModelList;
    }

    public List<DbModel> findDbModelAll(DbModelSelector selector) throws Exception {
        if (!tableIsExist(selector.getEntityType())) return null;

        List<DbModel> dbModelList = new ArrayList<DbModel>();

        Cursor cursor = execQuery(selector.toString());
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    dbModelList.add(CursorUtils.getDbModel(cursor));
                }
            } catch (Throwable e) {
                throw new Exception(e);
            } finally {
                IOUtils.closeQuietly(cursor);
            }
        }
        return dbModelList;
    }

    public long count(Selector selector) throws Exception {
        Class<?> entityType = selector.getEntityType();
        if (!tableIsExist(entityType)) return 0;

        Table table = Table.get( entityType);
        DbModelSelector dmSelector = selector.select("count(" + table.id.getColumnName() + ") as count");
        return findDbModelFirst(dmSelector).getLong("count");
    }

    public long count(Class<?> entityType) throws Exception {
        return count(Selector.from(entityType));
    }
*/


    //***************************** private operations with out transaction *****************************
   /* private void saveOrUpdateWithoutTransaction(Object entity) throws Exception {
        Table table = Table.get( entity.getClass());
        Id id = table.id;
        if (id.isAutoIncrement()) {
            if (id.getColumnValue(entity) != null) {
                execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo( entity));
            } else {
                saveBindingIdWithoutTransaction(entity);
            }
        } else {
            execNonQuery(SqlInfoBuilder.buildReplaceSqlInfo( entity));
        }
    }

    private boolean saveBindingIdWithoutTransaction(Object entity) throws Exception {
        Class<?> entityType = entity.getClass();
        Table table = Table.get(entityType);
        Id idColumn = table.id;
        if (idColumn.isAutoIncrement()) {
            execNonQuery(SqlInfoBuilder.buildInsertSqlInfo( entity));
            long id = getLastAutoIncrementId(table.tableName);
            if (id == -1) {
                return false;
            }
            idColumn.setAutoIncrementId(entity, id);
            return true;
        } else {
            execNonQuery(SqlInfoBuilder.buildInsertSqlInfo( entity));
            return true;
        }
    }*/

    //************************************************ tools ***********************************

    /*private long getLastAutoIncrementId(String tableName) throws Exception {
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
*/

    /////////////////////// temp cache ////////////////////////////////////////////////////////////////
/*    private final FindTempCache findTempCache = new FindTempCache();

    private class FindTempCache {
        private FindTempCache() {
        }

        *//**
     * key: sql;
     * value: find result
     *//*
        private final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<String, Object>();

        private long seq = 0;

        public void put(String sql, Object result) {
            if (sql != null && result != null) {
                cache.put(sql, result);
            }
        }

        public Object get(String sql) {
            return cache.get(sql);
        }

        public void setSeq(long seq) {
            if (this.seq != seq) {
                cache.clear();
                this.seq = seq;
            }
        }
    }*/
}