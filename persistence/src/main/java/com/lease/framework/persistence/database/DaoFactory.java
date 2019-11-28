package com.lease.framework.persistence.database;

import android.content.Context;

/**
 * Created by lwh on 2015/12/1.
 */
public class DaoFactory {
    private static DaoFactory daoFactory;
    private static final String DB_NAME = "app.db";
    private Context context;
    private DaoConfig daoConfig;

    public static DaoFactory getInstance(Context context) {
        if (daoFactory == null) {
            daoFactory = new DaoFactory(context);
        }
        return daoFactory;
    }

    private DaoFactory(Context context) {
        this.context = context;
    }

    /**
     * 建议 传入自己的config
     *
     * @param config config
     */
    public void init(DaoConfig config) {
        if (config != null) {
            this.daoConfig = config;
        } else {
            daoConfig = new DaoConfig(context) {
                @Override
                public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                }

                @Override
                public String getAuthority() {
                    return null;
                }

                @Override
                public Class<?>[] getAllTableClassList() {
                    return new Class<?>[0];
                }
            };
            daoConfig.setDbName(DB_NAME);
            daoConfig.setDbVersion(1);
        }
        DbManager.create(daoConfig).init();
    }

    public BaseDAO getBaseDao() {
        return new DefaultBaseDAO(DbManager.get(DB_NAME).getDatabase());
    }
}
