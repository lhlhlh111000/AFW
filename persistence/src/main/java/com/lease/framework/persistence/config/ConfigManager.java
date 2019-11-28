package com.lease.framework.persistence.config;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.lease.framework.core.LogUtils;
import com.lease.framework.core.NumberUtils;
import com.lease.framework.core.StringUtils;
import com.lease.framework.persistence.filestore.FileStoreProxy;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;


/**
 * 配置添加方法：在assets/xxx.properties文件中添加对应环境的配置，
 * 然后在 ConfigKey添加配置项key的常量定义
 */
public class ConfigManager {
    static final String sTag = "ConfigManager";

    private Environment mEnv = null;
    //private Bundle metaData;
    static private final String SP_KEY_ENV_FLAG = "sp-env-flag";
    private ConcurrentMap<String, String> config = new ConcurrentHashMap<String, String>();
    private Context mContext;

    private static ConfigManager instance;

    public static ConfigManager from(final Context context) {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager(context);
                }
            }
        }
        return instance;
    }

    public ConfigManager(Context context) {
        this.mContext = context;
        initEnv(context);
        if (mEnv == null) {
            throw new RuntimeException("error!! no 'ENV_MODE' find in manifest," +
                    " you need set this metaData!!!");
        }
        try {
            initConf(context, mEnv);
        } catch (Exception e) {
            LogUtils.e(sTag, e.getLocalizedMessage());
        }
    }

    private void initEnv(Context context) {
        mEnv = readFromCache();
        if (mEnv == null) {
            mEnv = readFromMeta(context);
        }

    }

    private Environment readFromCache() {
        String value = FileStoreProxy.getGlobalValue(SP_KEY_ENV_FLAG);
        try {
            if (StringUtils.isNotEmpty(value)) {
                return Environment.valueOf(value);
            }
        } catch (Exception e) {
            LogUtils.e(sTag, e.getLocalizedMessage());
        }
        return null;
    }

    private Environment readFromMeta(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo applicationInfo = pm.getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            Bundle metaData = applicationInfo.metaData;
            if (metaData != null) {
                String envFlg = StringUtils.trimToNull(metaData
                        .getString("ENV_MODE"));
                if (envFlg != null) {
                    return Environment.valueOf(envFlg.toUpperCase());
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e(sTag, e.getMessage(), e);
        }
        return null;
    }

    public void switchEnv(Context context, Environment env) {
        config.clear();
        try {
            mEnv = env;
            initConf(context, env);
            //warning: 只有切换环境才写入
            FileStoreProxy.setGlobalValue(SP_KEY_ENV_FLAG, env.name());
        } catch (IOException e) {
            LogUtils.e(sTag, e.getLocalizedMessage());
        }
    }

    private void initConf(Context context, Environment env) throws IOException {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            config.put(ConfigBaseKey.VERSION_CODE, String.valueOf(packageInfo.versionCode));
            config.put(ConfigBaseKey.VERSION_NAME, packageInfo.versionName);
        } catch (Exception e) {
            LogUtils.e(sTag, e.getMessage(), e);
        }
        readSomeConfFile(context, env);
        readConfFile(context, env.getFilePath());
    }

    /**
     * 读取 该环境的相关配置 比如 product-env(.*).properties
     * 后缀名properties
     * 文件名含有 product-env 的文件都视为 合法文件
     * 该文件由于模块/库的需求而配置
     */
    private void readSomeConfFile(Context context, Environment env) {

        try {
            String fileName = env.getFilePath();
            String[] names = fileName.split("\\.");
            Pattern p = Pattern.compile("(.*)" + names[0] + "(.*)");
            String[] files = context.getAssets().list("");
            for (int i = 0; i < files.length; ++i) {
                if (p.matcher(files[i]).find()
                        && !StringUtils.equals(env.getFilePath(), files[i])) {
                    LogUtils.d(sTag, "find module conf file  " + files[i]);
                    readConfFile(context, files[i]);
                }
            }
        } catch (Exception e) {
            LogUtils.e(e.getLocalizedMessage());
        }
    }

    /**
     * 读取最重要的 conf  file 比如product-env.properties
     * 该配置由app 配置
     *
     * @param context
     * @param fileName
     */
    private void readConfFile(Context context, String fileName) {
        InputStream is = null;
        try {
            // 初始化本地配置
            is = context.getAssets().open(fileName);
            Properties properties = new Properties();
            properties.load(is);
            Enumeration<Object> keys = properties.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                String value = properties.getProperty(key);
                config.put(key, value);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {//如果只是关闭时出现异常，先不抛出
                LogUtils.e(sTag, e.getMessage(), e);
            }
        }
    }

    public String getString(String key) {
        return config.get(key);
    }

    public Integer getInteger(String key) {
        String value = getString(key);
        if (NumberUtils.isDigits(value)) {
            return Integer.parseInt(value);
        }
        return null;

    }

    public Long getLong(String key) {
        String value = getString(key);
        if (NumberUtils.isDigits(value)) {
            return Long.parseLong(value);
        }
        return null;
    }

    public Double getDouble(String key) {
        String value = getString(key);
        if (NumberUtils.isNumber(value)) {
            return Double.parseDouble(value);
        }
        return null;
    }

    public JSONObject getJson(String key) {
        String value = getString(key);
        if (value != null) {
            try {
                return new JSONObject(value);
            } catch (JSONException e) {
                LogUtils.e(sTag, e.getMessage(), e);
            }
        }
        return null;
    }

    public ConcurrentMap<String, String> getConfig() {
        return config;
    }

    public Environment getEnvironment() {
        return mEnv;
    }

    /**
     * 环境
     */
    public enum Environment {
        /**
         * 线上
         */
        PRODUCT("product-env.properties", "线上"),
        /**
         * 测试
         */
//        TEST("test-env.properties", "测试"),
        /**
         *  演示环境
         */
        SHOW("show-env.properties", "演示环境"),
        /**
         * 预发布环境
         */
        PRE_PRODUCT("pre-env.properties", "测试环境");

        private String filePath;
        private String showName;

        Environment(String path, String showName) {
            this.filePath = path;
            this.showName = showName;
        }

        public String getShowName() {
            return showName;
        }

        public void setShowName(String showName) {
            this.showName = showName;
        }

        public String getFilePath() {
            return this.filePath;
        }
    }

    /**
     * 是否是 测试环境
     * 我要重命名 你们等着!
     *
     * @return
     */
    public boolean isTest() {
        return mEnv != Environment.PRODUCT;
    }

    /**
     * 是否是debug模式
     * 不要跟 线上环境 测试环境混淆
     *
     * @return
     */
    public boolean isDebug() {
        return (0 != (mContext.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
    }
    /**
     * 是否是debug模式
     * 不要跟 线上环境 测试环境混淆
     *
     * @return
     */
    public static boolean isDebug(Context context) {
        return (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
    }

    public Environment getCurrentEnv() {
        return mEnv;
    }
}
