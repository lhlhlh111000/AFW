package com.lease.framework.persistence.filestore;

import android.content.Context;
import android.content.SharedPreferences;


import com.lease.framework.core.StringUtils;

import java.util.Map;

/**
 *  文件缓存数据的管理类
 *  是对外暴露的唯一访问接口
 *  使用前调用 init()
 *  如果有用户信息建议调用 setDefaultSpName（）保证数据与用户相关
 */
public class FileStoreProxy {

    private static final String GLOBAL_SP_NAME = "G_FileStoreProxy";

	private FileStoreProxy(){
        
    };
    final static FileStoreIO fileStoreIO = FileStoreIO.getInstance();
    public static void init(Context context){
        fileStoreIO.init(context);
    }

    public static int getIntValue(String key1,String spName,int defaultValue){
        try{
            String result = fileStoreIO.getValue(key1, spName);
            if ( StringUtils.isEmpty(result))  {
                return defaultValue;
            }
            return Integer.parseInt(result);
        }catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static int getIntValue(String key1,int defaultValue){
      return   getIntValue(key1,null,defaultValue);
    }

    public static long getLongValue(String key1,String spName,long defaultValue){
        try{
            String result = fileStoreIO.getValue(key1, spName);
            if ( StringUtils.isEmpty(result))  {
                return defaultValue;
            }
            return Long.parseLong(result);
        }catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static long getLongValue(String key1,long defaultValue){
        return getLongValue(key1,null,defaultValue);
    }

    public static boolean getBooleanValue(String key1,String spName,boolean defaultValue){
        try{
            String result = fileStoreIO.getValue(key1, spName);
            if ( StringUtils.isEmpty(result))  {
                return defaultValue;
            }
            return Boolean.parseBoolean(result);
        }catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }
    public static boolean getBooleanValue(String key1,boolean defaultValue){
        return getBooleanValue(key1,null,defaultValue);
    }

    public static boolean setValue(String key,int value) {
        return fileStoreIO.setValue(key, value + "", null);
    }

    public static boolean setValue(String key,long value) {
        return fileStoreIO.setValue(key, value + "", null);
    }

    public static boolean setValue(String key,boolean value) {
        return fileStoreIO.setValue(key, value + "", null);
    }

    public static boolean setValue(String key,int value, String spName) {
        return fileStoreIO.setValue(key, value + "", spName);
    }

    public static boolean setValue(String key,long value, String spName) {
        return fileStoreIO.setValue(key, value + "", spName);
    }

    public static boolean setValue(String key,boolean value, String spName) {
        return fileStoreIO.setValue(key, value + "", spName);
    }

    public static boolean setGlobalValue(String key,boolean value) {
        return fileStoreIO.setValue(key, value + "", GLOBAL_SP_NAME);
    }

    public static boolean setGlobalValue(String key,int value) {
        return fileStoreIO.setValue(key, value + "", GLOBAL_SP_NAME);
    }

    public static boolean setGlobalValue(String key,String value) {
        return fileStoreIO.setValue(key, value + "", GLOBAL_SP_NAME);
    }

    public static boolean setGlobalValue(String key,long value) {
        return fileStoreIO.setValue(key, value + "", GLOBAL_SP_NAME);
    }

    public static String getGlobalValue(String key1) {
        return fileStoreIO.getValue(key1, GLOBAL_SP_NAME);
    }

    public static int getGlobalIntValue(String key1,int defaultValue){
        return getIntValue(key1,GLOBAL_SP_NAME,defaultValue);
    }

    public static long getGlobalLongValue(String key1,long defaultValue){
        return getLongValue(key1,GLOBAL_SP_NAME,defaultValue);
    }

    public static boolean getGlobalBooleanValue(String key1 ,boolean defaultValue){
        return getBooleanValue(key1,GLOBAL_SP_NAME,defaultValue);
    }

    public static void removeValue(String key,String spName){
        fileStoreIO.removeWithKey(key,spName);
    }

    public static void removeValue(String key){
        fileStoreIO.removeWithKey(key,null);
    }

    public static void removeGlobalValue(String key){
        fileStoreIO.removeWithKey(key,GLOBAL_SP_NAME);
    }


    /**
	 *  根据key来获取文件存储数据的value值
	 * @param key1   一级key
	 * @return 根据key取到的value值
	 * @param spName  可以指定SharedPreferences文件名,为空则为默认的SharedPreferences
	 */
	public static String getValue(String key1,String spName) {
		return fileStoreIO.getValue(key1, spName);
	}

    /**
     *  根据key来获取文件存储数据的value值,默认的SharedPreferences
     * @param key1   一级key
     * @return 根据key取到的value值
     */
    public static String getValue(String key1) {
        return fileStoreIO.getValue(key1, null);
    }


    /**
     * 根据key来存储value,默认的SharedPreferences
     */
    public static boolean setValue(String key,String value) {
        return fileStoreIO.setValue(key, value, null);
    }

	/**
	 * 根据key来存储value
	 * @param spName  可以指定SharedPreferences文件名,为空则为默认的SharedPreferences
	 */
	public static boolean setValue(String key,String value, String spName) {
		return fileStoreIO.setValue(key, value, spName);
	}
	
	/**
	 *  根据key来获取文件存储数据的value值
	 * @param key1   一级key，用于归类属于哪个Json数据
	 * @param spName  可以指定SharedPreferences文件名,为空则为默认的SharedPreferences
	 * @return 根据key取到的value值
	 */
	public static String getValue(String key1, String defaultValue, String spName) {
		return fileStoreIO.getValue(key1, defaultValue, spName);
	}
	
	/**
	 *  根据key来获取文件存储数据的value值
	 * @param key1   一级key，根据这个key来取json数据
	 * @param key2  二级key, 用于取Json数据里面的value值
	 * @param spName  可以指定SharedPreferences文件名,为空则为默认的SharedPreferences
	 * @return 根据key取到的value值
	 */
	public static String getJsonValue(String key1, String key2, String spName) {
		return fileStoreIO.getJsonValue(key1, key2,spName);
	}
	
	/**
	 *  根据key存储value值到文件，同步操作，防止多线程并发问题
	 * @param key1   一级key，根据这个key来存储json数据
	 * @param key2  二级key, 用于取Json数据里面的value值
	 * @param spName  可以指定SharedPreferences文件名,为空则为默认的SharedPreferences
	 * @return 根据key取到的value值
	 */
	public static void setJsonValue(String key1, String key2, String value, String spName) {
		fileStoreIO.setJsonValue(key1, key2, value, spName);
	}
	
	/**
	 * 设置默认的SharedPreferences文件名
	 */
	public static void setDefaultSpName(String spName) {
		fileStoreIO.setDefaultSpName(spName);
	}
	
	/**
	 * 获取SharedPreferences 对象
	 * @param spName  可以指定文件名，不传则取默认的
	 * @return
	 */
	public static SharedPreferences getSharedPreference(String spName) {
		 return fileStoreIO.getSharedPreference(spName);
	 }
	
	/**
	 *  删除默认SharedPreferences文件
	 */
	public static void remove() {
		fileStoreIO.remove();
	}
	
	
	/**
	 *  删除指定的SharedPreferences文件
	 */
	public static void remove(String spName) {
		fileStoreIO.remove(spName);
	}


    /**
     *  主要用于批量setValue，setValue比较耗时，当有多个的时候，把数据按k-v的形式封装在map作为参数传进来
     * @param spName 可以指定SharedPreferences文件名,为空则为默认的SharedPreferences
     * @return 是否成功
     */
    public static void setValueByTransaction(Map<String,String> data,String spName) {
        fileStoreIO.setValueByTransaction(data, spName);
    }
	
}
