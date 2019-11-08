package com.lease.framework.biz.test.frg;

import android.view.View;

import com.lease.framework.biz.test.R;
import com.lease.fw.ui.base.BaseViewModel;
import com.lease.fw.ui.frg.TitleFragment;
import com.lease.fw.ui.toast.ToastUtil;

/**
 * created time: 2019-11-08
 * author: cqt
 * description:
 */
public class ToastFragment extends TitleFragment<BaseViewModel> {

    private int count = 0;

    @Override
    protected int obtainContentLayout() {
        return R.layout.act_toast;
    }

    @Override
    protected void setupView() {
        super.setupView();

        getView().findViewById(R.id.btn_toast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        count += 1;
                        ToastUtil.show("hello" + count);
                    }
                }).start();
            }
        });
    }
}