package com.lease.framework.biz.test;

import android.os.Build;

import com.lease.framework.core.StringUtils;
import com.lease.framework.core.UniqueIdUtils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * 公用头部工具类
 * Created by zlh on 2016/11/18.
 */
public class HeaderUtils {

    public static Map<String, String> generateMallHeader() {
        Map<String ,String> heads = new HashMap<String, String>();

        heads = getDeviceInfo(heads);
        heads = getUserInfo(heads);
        heads = getSignInfo(heads);

        return heads;
    }

    /**
     * 获取设备信息
     * @param protocol
     * @return
     */
    private static Map<String, String> getDeviceInfo(Map<String, String> protocol) {
        String clientV = "3.6.0";
        String versionCode = 360 + "";
        String os = "Android";
        String osV = Build.VERSION.SDK_INT + "";
        String uuid = UniqueIdUtils.getUniqueId(TestApplication.getContext());
        uuid = null == uuid ? "" : uuid;
        String imei = UniqueIdUtils.getDeviceInfoByType(TestApplication.getContext(), UniqueIdUtils.DEVICES_INFO.IMEI);
        imei = null == imei ? "" : imei;
        String mac = UniqueIdUtils.getDeviceInfoByType(TestApplication.getContext(), UniqueIdUtils.DEVICES_INFO.MAC);
        mac = null == mac ? "" : mac;
        String androidId = UniqueIdUtils.getDeviceInfoByType(TestApplication.getContext(), UniqueIdUtils.DEVICES_INFO.ANDROID_ID);
        androidId = null == androidId ? "" : androidId;
        String osName = Build.MODEL;
        String channel = "0";

        protocol.put("client_b_v", versionCode);
        protocol.put("client_v", clientV);
        protocol.put("os", os);
        protocol.put("os_v", osV);
        protocol.put("uuid", uuid);
        protocol.put("imei", imei);
        protocol.put("mac", mac);
        protocol.put("android_id", androidId);
        protocol.put("os_name", osName);
        protocol.put("ch_id", channel);
        return protocol;
    }

    /**
     * 获取用户信息
     * @param protocol
     * @return
     */
    private static Map<String, String> getUserInfo(Map<String, String> protocol) {
        if(null == UserCentre.getInstance().getUserDO()) {
            return protocol;
        }

        UserDO userDO = UserCentre.getInstance().getUserDO();
        long userId = userDO.getUserId();
        String token = userDO.getToken();
        String userType = userDO.getType() + "";
        int isLogin = 1 != userDO.getType() ? 1 : 0;

        protocol.put("user_id", userId + "");
        protocol.put("token", null != token ? token : "");
        protocol.put("user_type", null != userType ? userType : "");
        protocol.put("isLogin", isLogin + "");
        protocol.put("phone", StringUtils.isEmpty(userDO.getPhoneNum()) ? "" : userDO.getPhoneNum());

        return protocol;
    }

    /**
     * 获取验证信息
     * @param protocol
     * @return
     */
    private static Map<String, String> getSignInfo(Map<String, String> protocol) {
        String timestamp = System.currentTimeMillis() + "";
        String tqAppId = "0";
        String ran = createRandomString(16);
        String sign = encodeHmacMd5Str(ran.concat(tqAppId).concat(timestamp), "8ee713e7-cc20-4b51-ad5f-1d9b87689f7c");

        protocol.put("timestamp", timestamp);
        protocol.put("tq_app_id", tqAppId);
        protocol.put("sign", sign);
        protocol.put("ran", ran);
        return protocol;
    }

    private static String createRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }

        return sb.toString();
    }

    /**
     * 进行HmacMD5加密
     * @param s 原始串
     * @param keyString 加密key
     * @return
     */
    private static String encodeHmacMd5Str(String s, String keyString) {
        String sEncodedString = null;
        try {
            SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), "HmacMD5");
            Mac mac = Mac.getInstance("HmacMD5");
            mac.init(key);
            byte[] bytes = mac.doFinal(s.getBytes("ASCII"));
            StringBuffer hash = new StringBuffer();
            for (int i=0; i<bytes.length; i++) {
                String hex = Integer.toHexString(0xFF &  bytes[i]);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }


            sEncodedString = hash.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch(InvalidKeyException e){
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return sEncodedString ;
    }
}
