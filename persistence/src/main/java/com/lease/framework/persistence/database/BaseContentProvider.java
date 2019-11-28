package com.lease.framework.persistence.database;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.lease.framework.core.LogUtils;
import com.lease.framework.core.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;


public abstract class BaseContentProvider extends ContentProvider {

    private static final String TAG = "BaseContentProvider";
    /**
     * uri 匹配规则code定义
     */
    private static final int URI_CODE_DB_TABLE = 1;
    private static final int URI_CODE_DB_SQL = URI_CODE_DB_TABLE + 1;
    private static final int URI_PATH_DB_REPLACE = URI_CODE_DB_SQL + 1;
    private static final int URI_CODE_CACHE = URI_PATH_DB_REPLACE + 1;

    /**
     * UriMatcher初始化
     */
    private UriMatcher uriMatcher;

    private void initUriMatcher(String authority){
        if(uriMatcher!=null){
            return;
        }
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(authority, UriHelper.URI_PATH_DB_TABLE+"*", URI_CODE_DB_TABLE);
        uriMatcher.addURI(authority, UriHelper.URI_PATH_DB_SQL+"*", URI_CODE_DB_SQL);
        uriMatcher.addURI(authority, UriHelper.URI_PATH_DB_REPLACE+"*", URI_PATH_DB_REPLACE);
        uriMatcher.addURI(authority, UriHelper.URI_PATH_CACHE+"*", URI_CODE_CACHE);
    }
    /**
     * dbhelper，处理db操作
     */
    private DaoConfig daoConfig;
    private SQLiteDatabase db;
    private DbManager dbManager;
    protected abstract DaoConfig genDaoConfig();

    /**
     * provider is not created until a ContentResolver object tries to access it
     * @return
     */
    @Override
    public boolean onCreate() {
        daoConfig = genDaoConfig();
        dbManager = DbManager.create(daoConfig).init();
        db = dbManager.getDatabase();
        initUriMatcher(daoConfig.getAuthority());
        return true;
    }

    /**
     * 查询，有效参数为
     * @param uri
     * @param projection columns, null for all columns.
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @throws IllegalArgumentException
     * @return
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        try{
            createIfNotExist(uri.getEncodedFragment());
            switch (uriMatcher.match(uri)) {
                case URI_CODE_DB_TABLE:     // 协议：path="db/table/XXX
                    List<String> segments = uri.getPathSegments();

                    if (segments.size() < 3 || StringUtils.isEmpty(segments.get(2))) {
                        throw new IllegalArgumentException("illegal query uri " + uri);
                    } else {
                        String tableName = uri.getPathSegments().get(2);
                        return db.query(tableName, projection, selection, selectionArgs, null, null, sortOrder);
                    }

                case URI_CODE_DB_SQL:       // 协议：path="db/sql/XXX
                    List<String> pathSegments = uri.getPathSegments();
                    if (pathSegments.size() < 3 || StringUtils.isEmpty(pathSegments.get(2))) {
                        throw new IllegalArgumentException("illegal query uri " + uri);
                    } else {
                        try {
                            String sqlStr = uri.getPathSegments().get(2);
                            String sql = URLDecoder.decode(sqlStr, "UTF-8");
                            return db.rawQuery(sql, selectionArgs);
                        } catch (UnsupportedEncodingException e) {
                            throw new IllegalArgumentException("illegal query uri, decode sql failed " + uri);
                        }
                    }
                case URI_CODE_CACHE:
                    break;
                default:
                    throw new IllegalArgumentException("unknown query uri " + uri);
            }
        }catch (Exception e){
            LogUtils.e(TAG, e);
        }
        return null;

    }

    private void createIfNotExist(String clazzName)  {
        String clazzNameReal = clazzName.replace("_",".");
        Class<?>clazz = null;
        try {
            clazz = Class.forName(clazzNameReal);
            dbManager.createTableIfNotExist(clazz);
        } catch (ClassNotFoundException e) {
            LogUtils.e(e.getLocalizedMessage());
        } catch (Exception e) {
            LogUtils.e(e.getLocalizedMessage());
        }

    }

    @Override
    public String getType(Uri uri) {
        return null;
    }


    /**
     * 插入数据：
     * /table 接口成功则返回uri，否则null
     * /sql 不支持
     * @param uri
     * @param values
     * @return
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        try{
            createIfNotExist(uri.getEncodedFragment());
            List<String> segments = uri.getPathSegments();
            switch (uriMatcher.match(uri)) {
                case URI_CODE_DB_TABLE:     // 协议：path="db/table/XXX
                    if (segments.size() < 3 || StringUtils.isEmpty(segments.get(2))) {
                        throw new IllegalArgumentException("illegal insert uri " + uri);
                    } else {
                        String tableName = uri.getPathSegments().get(2);
                        long row = db.insert(tableName, null, values);
                        if (row < 0) {
                            return null;
                        } else {
                            return ContentUris.withAppendedId(uri, row);
                        }
                    }

                case URI_PATH_DB_REPLACE:     // 协议：path="db/replace/XXX, XXX代表table name
                    if (segments.size() < 3 || StringUtils.isEmpty(segments.get(2))) {
                        throw new IllegalArgumentException("illegal replace uri " + uri);
                    } else {
                        String tableName = uri.getPathSegments().get(2);
                        long row = db.replace(tableName, null, values);
                        if (row < 0) {
                            return null;
                        } else {
                            return ContentUris.withAppendedId(uri, row);
                        }
                    }

                case URI_CODE_DB_SQL:       // 协议：path="db/sql/XXX
                    throw new IllegalArgumentException("not supported uri " + uri);
                /*
                List<String> pathSegments = uri.getPathSegments();
                if (pathSegments.size() < 3 || StringUtils.isEmpty(pathSegments.get(2))) {
                    throw new IllegalArgumentException("illegal insert uri " + uri);
                } else {
                    String sql = uri.getPathSegments().get(2);
                    db.execSQL(sql);
                }*/

                case URI_CODE_CACHE:
                    break;

                default:
                    throw new IllegalArgumentException("unknown uri " + uri);
            }
        }catch (Exception e){
            LogUtils.e(TAG,e);
        }

        return null;
    }
    /**
     *
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return the number of rows affected.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        try{
            createIfNotExist(uri.getEncodedFragment());
            switch (uriMatcher.match(uri)) {
                case URI_CODE_DB_TABLE:     // 协议：path="db/table/XXX
                    List<String> segments = uri.getPathSegments();
                    if (segments.size() < 3 || StringUtils.isEmpty(segments.get(2))) {
                        throw new IllegalArgumentException("illegal delete uri " + uri);
                    } else {
                        String tableName = uri.getPathSegments().get(2);
                        return db.delete(tableName, selection, selectionArgs);
                    }

                case URI_CODE_DB_SQL:       // 协议：path="db/sql/XXX
                    List<String> pathSegments = uri.getPathSegments();
                    if (pathSegments.size() < 3 || StringUtils.isEmpty(pathSegments.get(2))) {
                        throw new IllegalArgumentException("illegal delete_sql uri " + uri);
                    } else {
                        try {
                            String sqlStr = uri.getPathSegments().get(2);
                            String sql = URLDecoder.decode(sqlStr, "UTF-8");
                            db.execSQL(sql);
                            return 1;
                        } catch (UnsupportedEncodingException e) {
                            throw new IllegalArgumentException("illegal delete_sql uri, decode sql failed " + uri);
                        }
                    }

                case URI_CODE_CACHE:
                    break;

                default:
                    throw new IllegalArgumentException("unknown uri " + uri);
            }
        }catch (Exception e){
            LogUtils.e(TAG,e);
        }

        return 0;
    }


    /**
     *
     * @param uri
     * @param values
     * @param selection
     * @param selectionArgs
     * @return the number of rows affected.
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        try{
            createIfNotExist(uri.getEncodedFragment());
            switch (uriMatcher.match(uri)) {
                case URI_CODE_DB_TABLE:     // 协议：path="db/table/XXX
                    List<String> segments = uri.getPathSegments();
                    if (segments.size() < 3 || StringUtils.isEmpty(segments.get(2))) {
                        throw new IllegalArgumentException("illegal delete uri " + uri);
                    } else {
                        String tableName = uri.getPathSegments().get(2);
                        return db.update(tableName, values, selection, selectionArgs);
                    }

                case URI_CODE_DB_SQL:       // 协议：path="db/sql/XXX
                    throw new IllegalArgumentException("not supported uri " + uri);

                case URI_CODE_CACHE:
                    break;

                default:
                    throw new IllegalArgumentException("unknown uri " + uri);
            }

        }catch (Exception e){
            LogUtils.e(TAG,e);
        }
        return 0;
    }


    /**
     * 用于批量接口，支持事务处理
     * @param operations
     * @return
     * @throws OperationApplicationException
     */
    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        try{
            db.beginTransaction();
            try {
                ContentProviderResult[] results = super.applyBatch(operations);
                db.setTransactionSuccessful();
                return results;
            } finally {
                db.endTransaction();
            }
        }catch (Exception e){
            LogUtils.e(TAG,e);
        }
        return null;
    }

}