package com.lease.framework.persistence.filestore;



import com.lease.framework.core.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *  对于不同格式的数据进行转换
 */
class FileStoreFormat {

	/**
	 *  json的数据格式
	 *  @param data json数据
	 * @param key  根据key来取数据
	 */
	static String formatJsonGet(String data, String key) {
		String result = null;
		try {
			JSONObject json = new JSONObject(data);
			result = json.getString(key);
		} catch (JSONException e) {
			LogUtils.d("FileStoreFormat#formatJsonGet()#access data failed");
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 *  json的数据格式
	 *  @param data json数据
	 * @param key  根据key来存储数据
	 */
	static String formatJsonSet(String data, String key, String value) {
        if (value == null || key == null){
            return null;
        }

		JSONObject json = null;
		try {
            if (data == null) {
                json = new JSONObject();
            } else {
                json = new JSONObject(data);
            }
			json.putOpt(key, value);
		} catch (JSONException e) {
			LogUtils.d( "FileStoreFormat#formatJsonSet()#access data failed" );
			e.printStackTrace();
		}
        if (json != null){
            return json.toString();
        }
        return null;
	}
	
}
