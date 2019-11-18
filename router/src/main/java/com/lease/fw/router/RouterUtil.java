package com.lease.fw.router;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

/**
 * Created by zlh on 2017/5/5.
 */
public class RouterUtil {

    /**
     * 获取对应协议头
     * @param uri
     * @return
     */
    static String getSchemeStr(String uri) {
        try {
            return Uri.parse(uri).getScheme();
        }catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static boolean isActivity(Class clazz) {
        if(clazz.equals(Activity.class)) {
            return true;
        }else if(null != clazz.getSuperclass()) {
            return isActivity(clazz.getSuperclass());
        }
        return false;
    }

    public static boolean isFragment(Class clazz) {
        if(clazz.equals(Fragment.class)) {
            return true;
        }else if(null != clazz.getSuperclass()) {
            return isFragment(clazz.getSuperclass());
        }
        return false;
    }

    public static boolean isDialog(Class clazz) {
        if(clazz.equals(DialogFragment.class)) {
            return true;
        }else if(null != clazz.getSuperclass()) {
            return isFragment(clazz.getSuperclass());
        }
        return false;
    }
}
