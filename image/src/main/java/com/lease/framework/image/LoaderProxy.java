package com.lease.framework.image;

import android.content.Context;
import android.net.Uri;

/**
 *
 * Created by hxd on 16/9/22.
 */
public abstract class LoaderProxy {

    abstract public void init(Context context);

    abstract public void loadImage(LoadImageParams params);

    abstract public void loadImage(String url, LoaderCallBack callBack);

    public interface LoaderCallBack<T> {

        void onSuccess(Uri uri, T result);

        void onFailure(Uri uri, Throwable throwable);

        void onCancel(Uri uri);
    }
}
