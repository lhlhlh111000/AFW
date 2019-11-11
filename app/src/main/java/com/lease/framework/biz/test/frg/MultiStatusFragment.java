package com.lease.framework.biz.test.frg;

import android.view.View;

import com.lease.framework.biz.test.R;
import com.lease.fw.ui.base.BaseViewModel;
import com.lease.fw.ui.frg.TitleFragment;
import com.lease.fw.ui.status.OnReloadListener;
import com.lease.fw.ui.status.StatusDelegate;

/**
 * created time: 2019-11-11
 * author: cqt
 * description:
 */
public class MultiStatusFragment extends TitleFragment<BaseViewModel> {

    private StatusDelegate statusDelegate;

    @Override
    protected int obtainContentLayout() {
        return R.layout.frg_multi_status;
    }

    @Override
    protected void setupView() {
        super.setupView();
        View view = getView().findViewById(R.id.lin_status);
        statusDelegate = new StatusDelegate(view)
            .reloadListener(new OnReloadListener() {
                @Override
                public void reload() {
                    statusDelegate.showContentView();
                }
            }).build();

        getView().findViewById(R.id.btn_load).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusDelegate.showLoadingView();
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        statusDelegate.showContentView();
                    }
                }, 2000);
            }
        });

        getView().findViewById(R.id.btn_error).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusDelegate.showErrorView();
            }
        });

        getView().findViewById(R.id.btn_empty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusDelegate.showEmptyView();
            }
        });

        getView().findViewById(R.id.btn_net).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusDelegate.showNetView();
            }
        });
    }


}
