package com.lease.framework.task;

import com.lease.framework.task.core.ErrorTransformer;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;

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
}