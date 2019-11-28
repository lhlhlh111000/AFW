package com.lease.framework.core;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

/**
 * Created by WenHui on 2015/6/26.
 */
public class ResourceUtils {


    /**
     * 根据api版本调用对应的getDrawable
     *
     * @param context
     * @param resId
     * @return
     */
    public static Drawable getDrawableCompatible(Context context, int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getDrawable(resId);
        } else {
            return context.getResources().getDrawable(resId);
        }
    }

    /**
     * 根据api版本调用对应的setBackground
     */
    public static void setBackgroundDrawableCompatible(Context context,View view,int resId){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            view.setBackground(context.getResources().getDrawable(resId));
        }else{
            view.setBackgroundDrawable(context.getResources().getDrawable(resId));
        }
    }

}
