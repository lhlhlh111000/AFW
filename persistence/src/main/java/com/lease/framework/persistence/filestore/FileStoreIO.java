package com.lease.framework.persistence.filestore;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;


import com.lease.framework.core.LogUtils;

import java.util.Map;

/**
 * 负责整个FileCache模块的数据的读取与存储
 */
class FileStoreIO {
    private static final String sTAG = "FileStoreIO";
    private static final FileStoreIO INSTANCE = new FileStoreIO();

    static FileStoreIO getInstance() {
        return INSTANCE;
    }

    private FileStoreIO() {
        this.sp = FileStoreSP.getInstance();
    }

    ;

    FileStoreSP sp;

    public void init(Context context) {
        sp.init(context);
    }

    /**
     * 主要用于批量setValue，setValue比较耗时，当有多个的时候，把数据按k-v的形式封装在map作为参数传进来
     *
     * @param spName 可以指定SharedPreferences文件名,为空则为默认的SharedPreferences
     * @return 是否成功
     */
    boolean setValueByTransaction(Map<String, String> data, String spName) {
        boolean flag = sp.writeDataByTransaction(data, spName);
        if (!flag) {
            LogUtils.d(sTAG, "FileStoreIO#setValueByTransaction()# Transaction failed");
        }
        return flag;
    }

    /**
     * 根据key来存储value
     *
     * @param spName 可以指定SharedPreferences文件名,为空则为默认的SharedPreferences
     */
    boolean setValue(String key, String value, String spName) {
        boolean flag = sp.writeData(key, value, spName);
        if (!flag) {
            LogUtils.d("FileStoreIO#setValueByKey()#key  save failed" + key);
        }
        return flag;
    }

    /**
     * 根据key来获取文件存储数据的value值
     *
     * @param key1   一级key
     * @param spName 可以指定SharedPreferences文件名,为空则为默认的SharedPreferences
     * @return 根据key取到的value值
     */
    String getValue(String key1, String spName) {
        String data = sp.readData(key1, spName);
        if (TextUtils.isEmpty(data)) {
            LogUtils.d(sTAG, "FileStoreIO#getValueByKey(key1)#key  value is null : " + key1);
            return null;
        }
        return data;
    }

    /**
     * 根据key来获取文件存储数据的value值
     *
     * @param key1   一级key，用于归类属于哪个Json数据
     * @param spName 可以指定SharedPreferences文件名,为空则为默认的SharedPreferences
     * @return 根据key取到的value值
     */
    String getValue(String key1, String defaultValue, String spName) {
        String data = sp.readData(key1, defaultValue, spName);
        if (TextUtils.isEmpty(data)) {
            LogUtils.d(sTAG, "FileStoreIO#getValueByKey(key1)#key  value is null :" + key1);
            return null;
        }
        return data;
    }

    /**
     * 根据key来获取文件存储数据的value值
     *
     * @param key1   一级key，根据这个key来取json数据
     * @param key2   二级key, 用于取Json数据里面的value值
     * @param spName 可以指定SharedPreferences文件名,为空则为默认的SharedPreferences
     * @return 根据key取到的value值
     */
    String getJsonValue(String key1, String key2, String spName) {
        String data = sp.readData(key1, spName);
        if (TextUtils.isEmpty(data)) {
            LogUtils.d("FileStoreIO#getValueByKey(key1,key2)#key=" + key1 + " : value is null");
            return null;
        }
        String value = FileStoreFormat.formatJsonGet(data, key2);
        return value;
    }

    /**
     * 根据key存储value值到文件，同步操作，防止多线程并发问题
     *
     * @param key1   一级key，根据这个key来存储json数据
     * @param key2   二级key, 用于取Json数据里面的value值
     * @param spName 可以指定SharedPreferences文件名,为空则为默认的SharedPreferences
     * @return 根据key取到的value值
     */
    void setJsonValue(String key1, String key2, String value, String spName) {
        String data = sp.readData(key1, spName);
        data = FileStoreFormat.formatJsonSet(data, key2, value);
        if (data == null) {
            return;
        }
        boolean flag = sp.writeData(key1, data, spName);
        if (!flag) {
            LogUtils.d("FileStoreIO#setJsonValueByKey()#key=" + key1 + " : save failed");
        }
    }

    /**
     * 设置默认的SharedPreferences文件名
     */
    void setDefaultSpName(String spName) {
        sp.setDefaultSP(spName);
    }

    /**
     * 获取SharedPreferences 对象
     *
     * @param spName 可以指定文件名
     * @return
     */
    SharedPreferences getSharedPreference(String spName) {
        return sp.loadSP(spName);
    }

    /**
     * 清除默认SharedPreferences缓存的数据
     */
    void cleanUp() {
        sp.cleanUp();
    }

    /**
     * 清除指定SharedPreferences的数据
     *
     * @param spName
     */
    void cleanUp(String spName) {
        sp.cleanUp(spName);
    }

    /**
     * 删除默认的SharedPreferences文件
     */
    void remove() {
        sp.remove();
    }

    /**
     * 删除指定的SharedPreferences文件
     */
    void remove(String spName) {
        sp.remove(spName);
    }

    /**
     * 删除指定SharedPreferences的指定数据
     *
     * @param key
     * @param spName
     */
    void removeWithKey(String key, String spName) {
        sp.removeData(key, spName);
    }


    /*******************************Transaction**********************************************/


}
