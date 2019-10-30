package com.lease.fw.multidex;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;


/**
 *
 */
public class BaseApplication extends Application {

    static final String sTAG = "loadDex";
    //public static final String KEY_DEX2_SHA1 = "dex2-SHA1-Digest";
    private static Application application;
    private long waitTime = 40 * 1000;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //LogUtils.d(sTAG, "Application attachBaseContext ");
        if (!quickStart(base) && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (has2Dex(base) && needWait(base) && startFromActivity(base)) {
                waitForDexopt(base, getStartIntent());
            }
            MultiDex.install(this);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        if (quickStart(getBaseContext())) {
            return;
        }

        if(PackageUtil.isMainProcess(this)) {
            initConf();
        }
        writeInitFinish(getBaseContext());
    }

    public static Application getApplication() {
        return application;
    }

    protected void initConf() {

    }

    public static boolean quickStart(Context context) {
        String curProcessName = getCurProcessName(context);
        if(TextUtils.isEmpty(curProcessName)) {
            return false;
        }

        return curProcessName.contains(":mini");
    }


    protected boolean needWait(Context context) {
        File file = getFlagFile(context);
        return !file.exists();
    }

    protected boolean has2Dex(Context context) {
        String sha1 = get2thDexSHA1(context);
        return !TextUtils.isEmpty(sha1);
    }

    private File getFlagFile(Context context) {
        String flag = Uri.encode(get2thDexSHA1(context));
        Log.d(sTAG, "dex2-sha1 " + flag);
        File file = new File(context.getFilesDir() + "/" + flag);
        Log.d(sTAG, "need file exist?" + file.exists());
        return file;
    }

    private boolean startFromActivity(Context context) {
        try {
            ActivityManager activityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> list = activityManager.getRunningTasks(1);
            if (list != null && !list.isEmpty()) {
                PackageInfo packageInfo = PackageUtil.getPackageInfo(context);
                if(list.get(0).topActivity.getPackageName()
                        .equals(packageInfo.packageName)) {
                    return true;
                }
            }
        } catch (Exception e) {
            Log.e(sTAG, e.getLocalizedMessage());
        }

        return false;
    }

    /**
     * Get classes.dex file signature
     *
     * @param context
     * @return
     */
    private String get2thDexSHA1(Context context) {
        ApplicationInfo ai = context.getApplicationInfo();
        String source = ai.sourceDir;
        JarFile jar = null;
        try {
            jar = new JarFile(source);
            Manifest mf = jar.getManifest();
            Map<String, Attributes> map = mf.getEntries();
            Attributes a = map.get("classes2.dex");
            jar.close();
            return a == null ? "" : a.getValue("SHA1-Digest");
        } catch (Exception e) {
            e.printStackTrace();
            if (jar != null) {
                try {
                    jar.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return null;
    }

    public boolean initFinish(Context context) {
        File file = new File(context.getFilesDir() + "/finish.txt");
        return file.exists();
    }

    /**
     * 也可以放到 子类里调用 效果更好
     *
     * @param context context
     */
    private void writeInitFinish(Context context) {
        File file = new File(context.getFilesDir() + "/finish.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void installFinish(Context context) {
        File file = getFlagFile(context);
        if (!file.exists()) {
            try {
                file.createNewFile();
                Log.d(sTAG, "create file");
            } catch (IOException e) {
                Log.e(sTAG, e.getLocalizedMessage());
            }
        }
    }


    public static String getCurProcessName(Context context) {
        try {
            int pid = android.os.Process.myPid();
            ActivityManager mActivityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                    .getRunningAppProcesses()) {
                if (appProcess.pid == pid) {
                    return appProcess.processName;
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    protected Intent getStartIntent() {
        Intent intent = new Intent(getBaseContext(), LoadResActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public void waitForDexopt(Context base, Intent intent) {
        if (intent == null) {
            return;
        }
        base.startActivity(intent);
        long startWait = System.currentTimeMillis();

        while (needWait(base)) {
            try {
                long nowWait = System.currentTimeMillis() - startWait;
                Log.d("loadDex", "wait ms :" + nowWait);
                if (nowWait >= waitTime) {
                    return;
                }
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}