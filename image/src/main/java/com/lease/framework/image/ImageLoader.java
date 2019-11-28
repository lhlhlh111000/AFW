package com.lease.framework.image;

import android.content.Context;


/**
 * 包装的图片加载器,方便更换具体的实现
 * Created by hxd on 16/9/22.
 */
public class ImageLoader {
    private LoaderProxy proxy;

    private static class Holder {
        static ImageLoader instance = new ImageLoader();
    }

    private ImageLoader() {
        proxy = new FrescoImpl();
    }

    public static ImageLoader getInstance() {
        return Holder.instance;
    }

    public void init(Context context) {
        proxy.init(context);
    }

    public void loadImage(LoadImageParams params) {
        proxy.loadImage(params);
    }

    public void loadImage(String url, LoaderProxy.LoaderCallBack callBack) {
        proxy.loadImage(url, callBack);
    }
}
