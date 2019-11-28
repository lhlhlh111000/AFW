package com.lease.framework.core;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hxd on 15/7/7.
 */
public class PackageUtil {

    private static final String TAG ="PackageUtil" ;

    public static PackageInfo getPackageInfo(Context context){
        PackageManager pm = context.getPackageManager();
        try {
            return pm.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e(e.getLocalizedMessage());
        }
        return  new PackageInfo();
    }

    public static String getApplicationName(Context context) {
        try {
            PackageManager packageManager = null;
            ApplicationInfo applicationInfo = null;
            try {
                packageManager = context.getPackageManager();
                applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                applicationInfo = null;
            }
            String applicationName =
                    (String) packageManager.getApplicationLabel(applicationInfo);
            return applicationName;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }

    public static String getVersionName(Context context){
        return  PackageUtil.getPackageInfo(context.getApplicationContext()).versionName;
    }
    public static int getVersionCode(Context context){
        return  PackageUtil.getPackageInfo(context.getApplicationContext()).versionCode;
    }
    public static PackageInfo getPackageInfoIncludeSignatures(Context context){

        PackageManager pm = context.getPackageManager();
        try {
            return pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e(e.getLocalizedMessage());
        }
        return  new PackageInfo();
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        List<String> pName = new ArrayList<String>();
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName.contains(packageName);
    }
    public static boolean isInstallApk(Context context, String packagename) {
        boolean isInstall = false;
        try {
            PackageInfo packageInfo;
            try {
                packageInfo = context.getPackageManager().getPackageInfo(packagename, PackageManager.GET_META_DATA);
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

    /**
     * 判断app是否在后台，在onStop的时候调用有效
     * @param context
     * @return
     */
    public static boolean isAppInBackground(Context context){
        try {
            ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> services = am.getRunningTasks(Integer.MAX_VALUE);
            String package_other = services.get(0).topActivity.getPackageName().toString();
            String package_mine = context.getPackageName().toString();
            LogUtils.d(TAG, "onStop getClass().package_other()：" + package_other + "-------->package_mine:" + package_mine);
            //同一包名
            if(package_other.equalsIgnoreCase(package_mine)
                    //|| package_other.contains("tencent")
                    //|| package_other.contains("sina")
                    || package_other.contains("camera")
                    ||  package_other.contains("gallery")
                    //||  package_other.contains("taobao")
                    ){
                if(package_other.contains("tencent")){
                     //MobclickAgent.onEvent(context.getApplicationContext(), "appbg_tencent");
                }
                if(package_other.contains("sina")){
                      //MobclickAgent.onEvent(context.getApplicationContext(), "appbg_sina");
                }
                if(package_other.contains("camera")){
                      //MobclickAgent.onEvent(context.getApplicationContext(), "appbg_camera");
                }
                if(package_other.contains("gallery")){
                      //MobclickAgent.onEvent(context.getApplicationContext(), "appbg_gallery");
                }
                if(package_other.contains("taobao")){
                       //MobclickAgent.onEvent(context.getApplicationContext(), "appbg_taobao");
                }
                return false;
                //不同包名
            }else{
                if(package_other.contains("tencent")){
                      //MobclickAgent.onEvent(context.getApplicationContext(), "appbg_tencent");
                }
                if(package_other.contains("sina")){
                      //MobclickAgent.onEvent(context.getApplicationContext(), "appbg_sina");
                }
                if(package_other.contains("camera")){
                      //MobclickAgent.onEvent(context.getApplicationContext(), "appbg_camera");
                }
                if(package_other.contains("gallery")){
                      //MobclickAgent.onEvent(context.getApplicationContext(), "appbg_gallery");
                }
                if(package_other.contains("taobao")){
                       //MobclickAgent.onEvent(context.getApplicationContext(), "appbg_taobao");
                }
                return true;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * app是否正在运行
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAppRunning(Context context,String packageName){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
        for(ActivityManager.RunningAppProcessInfo rapi : infos){
            if(rapi.processName.equals(packageName))
                return true;
        }
        return false;
    }

    /**
     * 启动app
     * @param context
     * @param packageName
     */
    public static void startApp(Context context,String packageName){
        try{
            if(StringUtils.isNull(packageName))
                return;
            if(!isAppInstalled(context,packageName))
                return;
            if(isAppRunning(context,packageName)){
                Intent intent = new Intent();
                ComponentName componentName = new ComponentName(packageName,"com.meiyou.framework.biz.ui.TranslucentActivity");
                intent.setComponent(componentName);
                intent.setAction("com.meiyou.framework.biz.ui.SHOW_ACTIVITY");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }else{
                Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
                context.startActivity(intent);
            }
        }catch (Exception ex){
            ex.printStackTrace();
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            context.startActivity(intent);
        }
    }

}
