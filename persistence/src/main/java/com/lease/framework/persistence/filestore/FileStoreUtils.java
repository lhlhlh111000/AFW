package com.lease.framework.persistence.filestore;


/**
 */
class FileStoreUtils {

    public static boolean setValue(String key,String value, long userId) {
        return FileStoreIO.getInstance().setValue(key, value,  getSpNameNew(userId));
    }

    public static boolean setValue(String key,int value, long userId) {
        return FileStoreIO.getInstance().setValue(key, value + "",  getSpNameNew(userId));
    }

    public static boolean setValue(String key,long value, long userId) {
        return FileStoreIO.getInstance().setValue(key, value + "",  getSpNameNew(userId));
    }

    public static boolean setValue(String key,boolean value, long userId) {
        return FileStoreIO.getInstance().setValue(key, value + "",  getSpNameNew(userId));
    }

    public static String getValue(String key1, String defaultValue, long userId) {
        return FileStoreIO.getInstance().getValue(key1,defaultValue,  getSpNameNew(userId));
    }

    public static int getIntValue(long userId, String key1,int defaultValue){
        return FileStoreProxy.getIntValue(key1,  getSpNameNew(userId), defaultValue);
    }

    public static long getLongValue(long userId, String key1, long defaultValue){
        return FileStoreProxy.getLongValue(key1,  getSpNameNew(userId), defaultValue);
    }

    public static boolean getBooleanValue(long userId, String key1, boolean defaultValue) {
        return FileStoreProxy.getBooleanValue(key1,  getSpNameNew(userId), defaultValue);
    }

    private static String getSpNameNew(long userId){
        return "sp_" + userId + "_";
    }
}
