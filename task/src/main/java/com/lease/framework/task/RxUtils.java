package com.lease.framework.task;

import android.app.Activity;

import com.lease.framework.task.core.ErrorTransformer;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

/**
 * 和rx相关的工具类
 */
public class RxUtils {

    /**
     * 异常处理转换器
     * @return  转换器
     */
    public static <T> ErrorTransformer<T> handleGlobalError() {
        return TaskCentre.getInstance().getConfig().getErrorTransformer();
    }

    /**
     * 线程切换转换器
     */
    public static <T> ObservableTransformer<T, T> schedulersTransformer() {
        return new ObservableTransformer<T, T>() {
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * 生命周期绑定转换器
     * @param lifecycle
     * @return
     */
    public static <T> LifecycleTransformer<T> bindToLifecycle(@NonNull LifecycleProvider lifecycle) {
        return lifecycle.bindToLifecycle();
    }

    /**
     * 页面结束时结束绑定
     * @param lifecycle
     * @param <T>
     * @return
     */
    public static <T> LifecycleTransformer<T> bindToDestroy(@NonNull LifecycleProvider lifecycle) {
        if(lifecycle instanceof Activity) {
            return lifecycle.bindUntilEvent(ActivityEvent.DESTROY);
        }else {
            return lifecycle.bindUntilEvent(FragmentEvent.DESTROY_VIEW);
        }
    }

    /**
     * 页面暂停时结束绑定
     * @param lifecycle
     * @param <T>
     * @return
     */
    public static <T> LifecycleTransformer<T> bindToPause(@NonNull LifecycleProvider lifecycle) {
        if(lifecycle instanceof Activity) {
            return lifecycle.bindUntilEvent(ActivityEvent.PAUSE);
        }else {
            return lifecycle.bindUntilEvent(FragmentEvent.PAUSE);
        }
    }
}