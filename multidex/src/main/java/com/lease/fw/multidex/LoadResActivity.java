package com.lease.fw.multidex;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.view.Window;
import android.view.WindowManager;

/**
 * 欢迎页面activity
 * <p/>
 * Created by WenHui on 2015/7/27.
 */
public class LoadResActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        overridePendingTransition(R.anim.null_anim, R.anim.null_anim);
        setContentView(MultiDexCentre.getInstance().getPreLoadConfig().getLoadResId());
        new LoadDexTask().execute();
    }

    class LoadDexTask extends AsyncTask {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                MultiDex.install(getApplication());
                ((BaseApplication) getApplication()).installFinish(getApplication());
                while (!((BaseApplication) getApplication()).initFinish(getApplicationContext())){
                    Thread.sleep(200);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            finish();
            System.exit(0);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.null_anim, R.anim.null_anim);
    }

    @Override
    public void onBackPressed() {
        //cannot backpress
    }
}