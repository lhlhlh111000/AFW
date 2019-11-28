package com.lease.framework.persistence.database;

import android.net.Uri;

import com.lease.framework.core.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class UriHelper {
    /**
     * scheme
     */
    public static final String SCHEME = "content://";


    /**
     * uri定义
     */
    public static final String URI_PATH_DB_TABLE = "db/table/";
    public static final String URI_PATH_DB_SQL = "db/sql/";
    public static final String URI_PATH_DB_REPLACE = "db/replace/";
    public static final String URI_PATH_CACHE = "cache/";
    public static final String URI_PATH_FILE = "file/";

    public static Uri makeTableAccessUri(String tableName,String clazzName,String appAuthority){
        String uriStr = new StringBuilder(SCHEME).append(appAuthority).append("/" + URI_PATH_DB_TABLE)
                .append(tableName).append("#").append(clazzName).toString();
        return Uri.parse(uriStr);
    }

    public static Uri makeTableReplaceAccessUri(String tableName,String clazzName,String appAuthority){
        String uriStr = new StringBuilder(SCHEME).append(appAuthority).append("/" + URI_PATH_DB_REPLACE)
                .append(tableName).append("#").append(clazzName).toString();
        return Uri.parse(uriStr);
    }

    public static Uri makeRowSQLUri(String clazzName,String sql,String appAuthority){
        try {
            sql = URLEncoder.encode(sql,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            // ignore
        }
        String uriStr=new StringBuilder(SCHEME).append(appAuthority).append("/"+URI_PATH_DB_SQL).
                append(sql).append("#").append(clazzName).toString();
        return Uri.parse(uriStr);
    }

    /**
     * 从uri中解析数据库id
     * @param uri 不能为null
     * @return 解析失败返回null
     */
    public static int parseTableId(Uri uri){
        if(uri==null){
            throw new IllegalArgumentException("uri must not null.");
        }
        String idStr = uri.getLastPathSegment();
        if(isDigits(idStr)){
            return Integer.parseInt(idStr);
        }
        return 0;
    }
    public static boolean isDigits(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
