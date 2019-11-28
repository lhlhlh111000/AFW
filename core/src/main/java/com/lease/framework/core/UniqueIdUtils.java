package com.lease.framework.core;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * 设备身份指纹
 * 可能会遇到无权限的异常, 这里直接吃掉,然后抛出
 * Created by hxd on 16/5/4.
 */
public class UniqueIdUtils {

    public enum DEVICES_INFO {
        IMEI,
        MAC,
        SERIAL,
        ANDROID_ID
    }

    private static String SDCARD_FILE_NAME = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "taoqicar" + File.separator + "info.txt"; // 外部sdcard缓存文件
    private static final String SP_NAME = "GUID"; // app本地缓存
    private static final String SP_NAME_KEY = "DEVICE_INFO";
    private static Map<String, String> result = new HashMap<>();
    private static boolean isNeedSaveSP = true;

    /**
     * 获取指定设备信息
     * @param context
     * @param info
     * @return
     */
    public static String getDeviceInfoByType(Context context, DEVICES_INFO info) {
        Map<String, String> map = getDeviceInfo(context);
        return null == map.get(info.name()) ? "" : map.get(info.name());
    }

    /**
     * 获取设备唯一标识
     * @param context
     * @return
     */
    public static String getUniqueId(Context context) {
        // 取Android ID作为唯一标识，优先级最高
        String result = getDeviceInfo(context).get(DEVICES_INFO.ANDROID_ID.name());
        // 若Android ID为空，遍历取设备信息唯一id
        if(TextUtils.isEmpty(result)) {
            for(int i = 0; i< DEVICES_INFO.values().length; i++) {
                result = getDeviceInfo(context).get(DEVICES_INFO.values()[i].name());
                if(!TextUtils.isEmpty(result)) {
                    return result;
                }
            }
        }

        return result;
    }

    /**
     * 设备信息集合获取
     * @param context
     * @return
     */
    private static Map<String,String> getDeviceInfo(Context context) {
        if (!result.keySet().isEmpty()) {
            save(context, result);
            return result;
        }
        // 从本地缓存中获取设备信息
        result = fetchCache(context);
        // 初始设备信息
        if(result.keySet().isEmpty()) {
            result = fetchInit(context);
        }
        save(context, result);

        return result;
    }

    /**
     * 缓存设备信息，分本地缓存和sdcard缓存
     * @param context
     * @param result
     */
    private static void save(Context context, Map<String, String> result) {
        if (result == null || result.isEmpty()) {
            return;
        }

        // 补偿imei信息
        String imei = result.get(DEVICES_INFO.IMEI.name());
        if(StringUtils.isEmpty(imei)) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if((ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                        == PackageManager.PERMISSION_GRANTED)) {
                    try {
                        imei = getIMEI(context);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        if(StringUtils.isEmpty(imei)) { // 未获取到，补偿Android ID
                            imei = result.get(DEVICES_INFO.ANDROID_ID.name());
                        }
                    }
                }
            }else {
                try {
                    imei = getIMEI(context);
                }catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if(StringUtils.isEmpty(imei)) { // 未获取到，补偿Android ID
                        imei = result.get(DEVICES_INFO.ANDROID_ID.name());
                    }
                }
            }
            if(StringUtils.isNotEmpty(imei)) {
                result.put(DEVICES_INFO.IMEI.name(), imei);
                isNeedSaveSP = true;
                try {
                    File file = new File(SDCARD_FILE_NAME);
                    file.deleteOnExit();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if(isNeedSaveSP) {
            SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
            try {
                JSONObject object = new JSONObject();
                for(String key : result.keySet()) {
                    object.put(key, result.get(key));
                }
                sp.edit().putString(SP_NAME_KEY, object.toString()).apply();
            }catch (Exception e) {
                e.printStackTrace();
            }
            isNeedSaveSP = false;
        }

        try { // 权限异常直接忽略
            File file = new File(SDCARD_FILE_NAME);
            if(file.exists()) {
                return;
            }

            JSONObject object = new JSONObject();
            for(String key : result.keySet()) {
                object.put(key, result.get(key));
            }
            String info = object.toString();
            if(!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(info.getBytes());
            outStream.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从缓存、外部存储获取（做本地保存，确保安装期间一致性）
     * @param context
     * @return
     */
    private static Map<String, String> fetchCache(Context context) {
        Map<String, String> map = new HashMap<>();
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        String value = sp.getString(SP_NAME_KEY, "");
        try {
            if(!TextUtils.isEmpty(value)) {
                JSONObject object = new JSONObject(value);
                Iterator<String> iterators = object.keys();
                while (iterators.hasNext()) {
                    String key = iterators.next();
                    map.put(key, object.optString(key));
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        try { // 权限异常直接忽略
            File file = new File(SDCARD_FILE_NAME);
            if(map.keySet().isEmpty() && file.exists()) {
                FileInputStream is = new FileInputStream(file);
                byte[] b = new byte[is.available()];
                is.read(b);
                String info = new String(b);
                if(!TextUtils.isEmpty(info)) {
                    JSONObject object = new JSONObject(info);
                    Iterator<String> iterators = object.keys();
                    while (iterators.hasNext()) {
                        String key = iterators.next();
                        map.put(key, object.optString(key));
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        // 检测是否都为空
        for(String key : map.keySet()) {
            if(!TextUtils.isEmpty(map.get(key))) {
                return map;
            }
        }

        return new HashMap<>();
    }

    /**
     * 初始化获取设备信息
     * @param context
     * @return
     */
    private static Map<String, String> fetchInit(Context context) {
        Map<String, String> map = new HashMap<>();

        try {
            String imei = getIMEI(context);
            map.put(DEVICES_INFO.IMEI.name(), imei);
        } catch (UniqueException e) {
            e.printStackTrace();
        }
        try {
            String mac = getMacId(context);
            map.put(DEVICES_INFO.MAC.name(), mac);
        } catch (UniqueException e) {
            e.printStackTrace();
        }
        try {
            String androidId = getAndroidId(context);
            // 如果android ID为空，设置设备系列号作为android id
            if(TextUtils.isEmpty(androidId)) {
                androidId = getSerialNumber(context);
            }
            map.put(DEVICES_INFO.ANDROID_ID.name(), androidId);
        } catch (UniqueException e) {
            e.printStackTrace();
        }
        try {
            String serial = getSerialNumber(context);
            map.put(DEVICES_INFO.SERIAL.name(), serial);
        } catch (UniqueException e) {
            e.printStackTrace();
        }

        //如果全部都没有获取到 设置uuid作为Android ID
        if (map.keySet().isEmpty()) {
            map.put(DEVICES_INFO.ANDROID_ID.name(), getUUID());
        }

        return map;
    }

    /**
     * 可伪造、需权限分配
     * @param context
     * @return
     * @throws UniqueException
     */
    private static String getIMEI(Context context) throws UniqueException {
        try {
            String imei = "";
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            imei = tm.getDeviceId();
            if("000000000000000".equals(imei)) { // 过滤不可用imei号，虚拟机或刷机后
                imei = "";
            }
            return imei;
        } catch (Exception e) {
            throw new UniqueException(e);
        }
    }

    /**
     * 可伪造、需权限分配
     * @param context
     * @return
     * @throws UniqueException
     */
    private static String getMacId(Context context) throws UniqueException {
        try {
            WifiManager wifi = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            String address = info.getMacAddress();
            if (validMac(address)) {
                throw new UniqueException("mac addr is empty");
            }
            return address;
        } catch (Exception e) {
            throw new UniqueException(e);
        }
    }

    /**
     * 是否非法mac地址
     * @param address
     * @return
     */
    private static boolean validMac(String address) {
        return !TextUtils.isEmpty(address)
                && !TextUtils.equals(address, "00:00:00:00:00:00")
                && !TextUtils.equals(address, "ff:ff:ff:ff:ff:ff")
                && !TextUtils.equals(address, "02:00:00:00:00:00");
    }

    /**
     * 在设备首次启动时，系统会随机生成一个64位的数字，并把这个数字以16进制字符串的形式保存下来，这个16进制的字符串就是ANDROID_ID
     * 厂商定制系统的Bug：不同的设备可能会产生相同的ANDROID_ID：9774d56d682e549c。
     * 厂商定制系统的Bug：有些设备返回的值为null。
     * 恢复出厂设置会被重置，手机root后可修改模拟
     * @param context
     * @return
     * @throws UniqueException
     */
    private static String getAndroidId(Context context) throws UniqueException {
        try {
            String androidId = Settings.System.getString(
                    context.getContentResolver(), Settings.Secure.ANDROID_ID);
            return androidId;
        } catch (Exception e) {
            throw new UniqueException(e);
        }
    }

    /**
     * 根据设备 厂商、rom、cpu型号等一系列信息生成串
     * 这里得到的值不一定唯一,同批次机型有可能一致，概率比较小
     * @param context context
     * @return str
     * @throws UniqueException
     */
    private static String getSerialNumber(Context context) throws UniqueException {
        try {
            return "35" + //we make this look like a valid IMEI
                    Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                    Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                    Build.DISPLAY.length() % 10 + Build.VERSION.RELEASE.length() % 10 +
                    Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                    Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                    Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                    Build.USER.length() % 10; //13 digits
        } catch (Exception e) {
            throw new UniqueException(e);
        }
    }

    /**
     * JDK 提供的主键生成方法，同一时空理论上是生成相同的。
     * 由以下几部分的组合：当前日期和时间(UUID的第一个部分与时间有关，如果你在生成一个UUID之后，过几秒又生成一个UUID，则第一个部分不同，其余相同)，
     * 时钟序列，全局唯一的IEEE机器识别号（如果有网卡，从网卡获得，没有网卡以其他方式获得），UUID的唯一缺陷在于生成的结果串会比较长
     * @return
     */
    private static String getUUID() {
        return UUID.randomUUID().toString();
    }

}
