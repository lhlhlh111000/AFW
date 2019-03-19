package com.lease.framework.task.core;

import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class DefaultErrorTransformer extends ErrorTransformer {

    public DefaultErrorTransformer() {
        super(new Function() {
            @Override
            public Object apply(Object o) throws Exception {
                return o;
            }
        }, new Function<Throwable, Observable>() {
            @Override
            public Observable apply(Throwable throwable) throws Exception {
                return Observable.error(throwable);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                // Do Nothing;
                Log.e("NetworkCentre", "Error transformer error handler");
            }
        });
    }
}
