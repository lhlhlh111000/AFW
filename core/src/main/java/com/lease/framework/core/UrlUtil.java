/**
 * ****************************************************************************
 * Copyright (c) 2013, 2015 linggan.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * *****************************************************************************
 */
package com.lease.framework.core;

import android.net.Uri;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * URL处理工具
 * Created by YiRong on 2015/8/21.
 */
public class UrlUtil {
    public static final String REG_IMG_SUFFER = ".+(.jpg|.png|.gif|.jpeg|.ico|.webp|.bmp).+";//图片
    public static final String REG_IMG_SUFFER_NODOR = ".+(jpg|png|gif|jpeg|ico|webp|bmp).+";//图片

    public static int[] getWidthHeightByUrl(String url) {
        try {
            if (url != null) {
                String[] splits = url.split("_");
                if ( splits.length >= 3) {
                    String widthStr = splits[splits.length - 2];
                    String heightStr = splits[splits.length - 1];
                    int index = heightStr.indexOf(".");
                    if (index > 0) {
                        heightStr = heightStr.substring(0, index);
                    }
                    int width = Integer.parseInt(widthStr);
                    int height = Integer.parseInt(heightStr);

                    return new int[]{width, height};
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean isResUrl(String url){
        if(url.contains(".js") || url.contains(".jpg") || url.contains(".png")
                || url.contains(".gif")|| url.contains(".jpeg")
                || url.contains(".ico")|| url.contains(".webp")
                || url.contains(".bmp") || url.contains(".css")){
            return true;
        }
        return false;
    }

    /**
     * 判断URL是否为图片链接
     *
     * @param url
     * @return
     */
    public static boolean urlIsImg(String url) {
        url = url.toLowerCase();
        Pattern pattern = Pattern.compile(REG_IMG_SUFFER);
        Matcher matcher = pattern.matcher(url);
        return matcher.matches();
    }

    /**
     * 判断URL转换后的Key是否为图片链接
     *
     * @param key
     * @return
     */
    public static boolean keyIsImg(String key) {
        key = key.toLowerCase();
        Pattern pattern = Pattern.compile(REG_IMG_SUFFER_NODOR);
        Matcher matcher = pattern.matcher(key);
        return matcher.matches();
    }

    public static String replaceBlank(String str) {
        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        LogUtils.d("original string:" + str);
        Matcher m = p.matcher(str);
        String after = m.replaceAll("");
        LogUtils.d("after replace:" + after);
        return after;
    }

    /**
     * 获取URI参数
     *
     * @param uri
     * @return
     */
    public static Map<String, String> getParamMapByUri(Uri uri) {
        Map<String, String> queryPairs = new LinkedHashMap<>();
        try {
            String query = uri.getQuery();
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                String key = pair.substring(0, idx);
                String value = pair.substring(idx + 1);
                queryPairs.put(key, value);
            }
        } catch (Exception e) {
         //   LogUtils.e(e.getLocalizedMessage());
        }
        return queryPairs;
    }

}
