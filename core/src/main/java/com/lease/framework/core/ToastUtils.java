package com.lease.framework.core;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by WenHui on 2015/6/26.
 * 处理toast相关
 */
public class ToastUtils {
    private static Toast mToast;
    private static View toastRoot;
    private static Handler mHandler;

    public static void showToast(final Context context, final String content) {
        try {
            if (TextUtils.isEmpty(content))
                return;
            if (context instanceof Activity) {
                ((Activity) context).runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            /*if (mToast == null) {
                                mToast = Toast.makeText(context.getApplicationContext(), content, Integer.valueOf(300));
                            }
                            mToast.setText(content);
                            mToast.setGravity(Gravity.CENTER, 0, 0);
                            mToast.show();*/

                            handleToast(context, content);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }
                });
            } else {
                if(mHandler == null){
                    mHandler = new Handler(Looper.getMainLooper());
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        handleToast(context, content);
                    }
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void handleToast(Context context, String content) {
        if(mToast == null){
            mToast = new Toast(context.getApplicationContext());
        }
        if (toastRoot == null) {
            toastRoot = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.layout_toast, null);
        }
        mToast.setView(toastRoot);
        TextView tv = (TextView) toastRoot.findViewById(R.id.tvToast);
        tv.setText(content);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }

    public static void handleToast(Context context, int content) {
        handleToast(context, context.getResources().getString(content));
    }

    public static void showToast(final Context context, final int content) {
        try {
            if (context instanceof Activity) {
                ((Activity) context).runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            /*if (mToast == null) {
                                mToast = Toast.makeText(context.getApplicationContext(), content, Integer.valueOf(300));
                            }
                            mToast.setText(content);
                            mToast.setGravity(Gravity.CENTER, 0, 0);
                            mToast.show();*/
                            handleToast(context, content);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }
                });
            } else {
                /*if (mToast == null) {
                    mToast = Toast.makeText(context.getApplicationContext(), content, Integer.valueOf(300));
                }
                mToast.setText(content);
                mToast.setGravity(Gravity.CENTER, 0, 0);
                mToast.show();*/
                handleToast(context, content);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
