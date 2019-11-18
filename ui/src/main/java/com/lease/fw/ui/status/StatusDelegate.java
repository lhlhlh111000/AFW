package com.lease.fw.ui.status;

import android.view.View;
import android.view.ViewGroup;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;

/**
 * created time: 2019-11-11
 * author: cqt
 * description: 多状态代理者；管理加载中、空数据等
 */
public class StatusDelegate {

    private MultiStatusView multiStatusView;

    private StatusDelegate() {}

    public StatusDelegate(View needMultiStatusView) {
        this.multiStatusView = wrapperStatusView(needMultiStatusView);
    }

    public MultiStatusView load(int resId) {
        multiStatusView.load(resId);
        return multiStatusView;
    }

    public MultiStatusView error(int resId) {
        multiStatusView.error(resId);
        return multiStatusView;
    }

    public MultiStatusView empty(int resId) {
        multiStatusView.empty(resId);
        return multiStatusView;
    }

    public MultiStatusView netNotWork(int resId) {
        multiStatusView.netNotWork(resId);
        return multiStatusView;
    }

    public MultiStatusView reloadListener(OnReloadListener listener) {
        multiStatusView.reloadListener(listener);
        return multiStatusView;
    }

    public StatusDelegate build() {
        multiStatusView.build();
        showContentView();
        return this;
    }

    public void showContentView() {
        multiStatusView.show(Status.DEFAULT);
    }

    public void showLoadingView() {
        multiStatusView.show(Status.LOADING);
    }

    public void showEmptyView() {
        multiStatusView.show(Status.EMPTY);
    }

    public void showErrorView() {
        multiStatusView.show(Status.ERROR);
    }

    public void showNetView() {
        multiStatusView.show(Status.NET_OFFILEN);
    }

    private MultiStatusView wrapperStatusView(View view) {
        if(null == view.getParent()) {
            throw new IllegalStateException("Can not wrapper multi status view");
        }

        ViewGroup parent = (ViewGroup) view.getParent();
        if(parent instanceof SmartRefreshLayout) {
            // 这边对刷新View做一个简单过滤
            return wrapperStatusView(parent);
        }else {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            int index = parent.indexOfChild(view);
            parent.removeView(view);
            MultiStatusView multiStatusView = new MultiStatusView(view, this);
            parent.addView(multiStatusView, index, params);
            return multiStatusView;
        }
    }
}