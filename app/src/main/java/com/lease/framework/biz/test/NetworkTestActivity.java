package com.lease.framework.biz.test;

import android.util.Log;
import android.view.View;

import com.lease.framework.network.RetrofitClient;
import com.lease.framework.task.RxUtils;
import com.lease.fw.ui.act.TaoqiActivity;
import com.lease.fw.ui.base.BaseViewModel;
import com.lease.fw.ui.config.TitleBarConfig;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class NetworkTestActivity extends TaoqiActivity<BaseViewModel> {

    @Override
    protected int obtainContentLayout() {
        return R.layout.act_net_work;
    }

    @Override
    protected void updateTitleBarConfig() {
        super.updateTitleBarConfig();
        if(null == titleBarConfig.getValue()) {
            return;
        }

        TitleBarConfig config = titleBarConfig.getValue();
        config.setTitleText("网络测试");
    }

    @Override
    protected void setupView() {
        super.setupView();

        findViewById(R.id.btn_verify_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrofitClient.getInstance().create(MallApiService.class)
                        .obtainVerifyCode("15695916483", 4)
                        .compose(RxUtils.<VerifyCodeDO>bindToLifecycle(NetworkTestActivity.this))
                        .compose(RxUtils.<VerifyCodeDO>handleGlobalError())
                        .compose(RxUtils.<VerifyCodeDO>schedulersTransformer())
                        .subscribe(new Observer<VerifyCodeDO>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(VerifyCodeDO verifyCodeDO) {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("NetworkCentre", "Observer error handler");
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        });
    }
}
