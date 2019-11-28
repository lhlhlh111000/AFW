package com.lease.framework.core;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.TimeZone;

/**
 * Created by WenHui on 2015/6/26.
 * 获取设备相关信息
 * 获取 deviceId see@UniqueIdUtils
 */
@SuppressLint("DefaultLocale")
public class DeviceUtils {

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public static int getScreenWidth(Context context) {
        try {
            return context.getResources().getDisplayMetrics().widthPixels;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 480;
    }

    public static String getDeviceId(Context context) {
        return UniqueIdUtils.getDeviceInfoByType(context, UniqueIdUtils.DEVICES_INFO.MAC);
    }

    public static String getImei(Context context) {
        return UniqueIdUtils.getDeviceInfoByType(context, UniqueIdUtils.DEVICES_INFO.IMEI);
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenHeight(Context context) {
        try {
            return context.getResources().getDisplayMetrics().heightPixels;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 800;

    }

    public static float getDeviceDensity(Context context) {
        try {
            float scale = context.getResources().getDisplayMetrics().density;
            return scale;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 480;

    }

    public static int getDeviceDensityValue(Context context) {
        try {
            int scale = context.getResources().getDisplayMetrics().densityDpi;
            return scale;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 480;

    }

    /**
     * dip到px的转换
     *
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int getMarginTopWithoutWave(Context context) {
        return dip2px(context, 20);
    }


    /**
     * sp -> px
     *
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scale + 0.5f);
    }

    /**
     * px到dip的转换
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 隐藏键盘
     *
     * @param activity
     */
    public static void hideKeyboard(Activity activity) {
        try {
            ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    public static void hideKeyboard(Activity activity, View view) {
        try {
            ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示键盘
     *
     * @param activity
     * @param view
     */
    public static void showkeyboard(Activity activity, View view) {
        try {
            ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(view, 0);
        } catch (Exception e) {
            e.printStackTrace();//
        }
    }


    //是否显示了键盘
    public static boolean isShowkeyboard(Context context) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            return imm.isActive();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;

    }


    public static String getTimeZoneName() {
        try {
            TimeZone tz = TimeZone.getDefault();
            return tz.getDisplayName(false, TimeZone.SHORT);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public static boolean isInstall(Context context, String packagename) {
        boolean isInstall = false;
        try {
            PackageInfo packageInfo;
            try {
                packageInfo = context.getPackageManager().getPackageInfo(
                        packagename, PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                packageInfo = null;
                e.printStackTrace();
            }
            if (packageInfo == null) {
                isInstall = false;
            } else {
                isInstall = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isInstall;
    }

    public static int getMarginTopWithWave(Context context) {
        return DeviceUtils.dip2px(context, 4);
    }

    /**
     * 获取状态栏高度
     *
     * @param activity
     * @return
     * @deprecated 直接拿rootView的top，这个方法不准！！！有的时候拿出来是0！！
     */
    public static int getStatusBarHeight(Activity activity) {
        try {
            Rect frame = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            int statusBarHeight = frame.top;
            return statusBarHeight;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 20;
    }
}
