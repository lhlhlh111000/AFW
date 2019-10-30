package com.lease.fw.multidex;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
            Log.e(TAG, e.getLocalizedMessage());
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
            Log.e(TAG, e.getLocalizedMessage());
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
            Log.d(TAG, "onStop getClass().package_other()：" + package_other + "-------->package_mine:" + package_mine);
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
            if(TextUtils.isEmpty(packageName))
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

    public static boolean isMainProcess(Context context) {
        if (context == null) {
            return false;
        }

        String packageName = context.getApplicationContext().getPackageName();
        String processName = getProcessName(context);
        return packageName.equals(processName);
    }

    public static String getProcessName(Context context) {
        String processName = getProcessFromFile();
        if (processName == null) {
            // 如果装了xposed一类的框架，上面可能会拿不到，回到遍历迭代的方式
            processName = getProcessNameByAM(context);
        }
        return processName;
    }

    private static String getProcessFromFile() {
        BufferedReader reader = null;
        try {
            int pid = android.os.Process.myPid();
            String file = "/proc/" + pid + "/cmdline";
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "iso-8859-1"));
            int c;
            StringBuilder processName = new StringBuilder();
            while ((c = reader.read()) > 0) {
                processName.append((char) c);
            }
            return processName.toString();
        } catch (Exception e) {
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String getProcessNameByAM(Context context) {
        String processName = null;

        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        if (am == null) {
            return null;
        }

        while (true) {
            List<ActivityManager.RunningAppProcessInfo> plist = am.getRunningAppProcesses();
            if (plist != null) {
                for (ActivityManager.RunningAppProcessInfo info : plist) {
                    if (info.pid == android.os.Process.myPid()) {
                        processName = info.processName;

                        break;
                    }
                }
            }

            if (!TextUtils.isEmpty(processName)) {
                return processName;
            }

            try {
                Thread.sleep(100L); // take a rest and again
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
