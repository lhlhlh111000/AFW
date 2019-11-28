package com.lease.framework.persistence.database;

import android.content.Context;
import android.text.TextUtils;

/**数据库配置文件
 * Created by Administrator on 2015/6/25.
 */
//******************************************** config ******************************************************
public  abstract class DaoConfig implements DbUpgradeListener {
    private Context context;
    protected String dbName = "app.db"; // default db name
    private int dbVersion = 1;

    private String dbDir;

    public abstract String getAuthority();

    public DaoConfig(Context context) {
        this.context = context.getApplicationContext();
    }

    public Context getContext() {
        return context;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        if (!TextUtils.isEmpty(dbName)) {
            this.dbName = dbName;
        }
    }

    public int getDbVersion() {
        return dbVersion;
    }

    public void setDbVersion(int dbVersion) {
        this.dbVersion = dbVersion;
    }

    public String getDbDir() {
        return dbDir;
    }

    /**
     * set database dir
     *
     * @param dbDir If dbDir is null or empty, use the app default db dir.
     */
    public void setDbDir(String dbDir) {
        this.dbDir = dbDir;
    }

    abstract public Class<?>[] getAllTableClassList();
}
