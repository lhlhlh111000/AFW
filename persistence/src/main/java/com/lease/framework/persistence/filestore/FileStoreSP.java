package com.lease.framework.persistence.filestore;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.lease.framework.core.LogUtils;
import com.lease.framework.core.StringUtils;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

/**
 *  文件缓存的SharePreferrence类型
 *  
 * @author wb-wangxiaolong
 *
 */
class FileStoreSP {

	/**
	 * 默认的SharedPreferences 文件名
	 */
	private String defaultSpName = "default";

	private Context context;
	private FileStoreSP(){

	}
	public static FileStoreSP getInstance(){
		return Holder.sp;
	}
	public static class Holder{
		static FileStoreSP  sp = new FileStoreSP();
	}

	public void init(Context context){
		if(this.context==null) this.context = context;
	}
    /**
     *  主要用于批量setValue，setValue比较耗时，当有多个的时候，把数据按k-v的形式封装在map作为参数传进来
     * @param data
     * @param newSp 可以指定SharedPreferences文件名,为空则为默认的SharedPreferences
     * @return 是否成功
     */
    final boolean writeDataByTransaction(Map<String,String> data,String newSp) {
        boolean flag = false;
        SharedPreferences sp = null;
        sp = loadSP(newSp);
        if ( sp != null && data != null) {
            SharedPreferences.Editor editor = sp.edit();
            Iterator<Map.Entry<String,String>> iterator = data.entrySet().iterator();
            while ( iterator.hasNext() ) {
                Map.Entry<String,String> entry = iterator.next();
                editor.putString(entry.getKey(),entry.getValue());
            }
            editor.commit();
            flag = true;
        }
        return flag;
    }

	final boolean writeData(String key,String value,String newSp) {
        SharedPreferences sp = null;
		sp = loadSP(newSp);
		if ( sp != null ) {
			boolean flag = false;
			flag = sp.edit().putString(key, value).commit();
            return flag;
		}

        return false;
	}

    final void removeData(String key,String newSp){
        SharedPreferences sp = null;
        sp = loadSP(newSp);
        if ( sp != null ) {
            sp.edit().remove(key).commit();
        }
    }
	
	final String readData(String key, String newSp) {
		SharedPreferences sp = null;
		sp = loadSP(newSp);
		if ( sp != null ) {
			return sp.getString(key, null);
		}
		return null;
	}
	
	final String readData(String key, String defaultValue, String newSp ) {
		SharedPreferences sp = null;
		sp = loadSP(newSp);
		if ( sp != null ) {
			return sp.getString(key, defaultValue);
		}
		return null;
	}
	
	/**
	 * 设置默认的SharedPreferences文件名
	 */
	final void setDefaultSP(String spName) {
		this.defaultSpName = spName;
	}
	
	/**
	 *  加载默认的SharedPreferences
	 */
	final SharedPreferences loadDefaultSP() {
		return loadSP(defaultSpName);
	}
	
	/**
	 *  获取SharedPreferences根据指定的SharedPreferences Name
	 */
	final SharedPreferences loadSP(String newSp) {
		String spName = null;
		if ( !StringUtils.isEmpty(newSp) ) {
			if (! newSp.equals(defaultSpName) ) {
				spName = newSp;
			}
		}
		if ( spName == null ) {
			spName = defaultSpName;
		}
        try{
            int sdkLevel = Build.VERSION.SDK_INT;
            return context.getSharedPreferences(spName, (sdkLevel > 8) ?  4 : 0);
        }catch (Exception e){
            LogUtils.e("FileCacheSP", e.getMessage());
            return null;
        }
	}
	

	/**
	 *  清除默认SharedPreferences缓存的数据
	 */
	final void cleanUp() {
		loadDefaultSP().edit().clear().commit();
	}
	
	/**
	 *  清除指定SharedPreferences的数据
	 * @param spName
	 */
	final void cleanUp(String spName) {
        loadSP(spName).edit().clear().commit();
    }

    /**
     *  删除默认的SharedPreferences文件
     */
    final void remove() {
        remove(defaultSpName);
    }

    /**
     *  删除指定的SharedPreferences文件
     */
    final void remove(String spName) {
        File file= context.getDatabasePath(spName + ".xml");
        if(file.exists()){
            file.delete();
        }
    }

}
