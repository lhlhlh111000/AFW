package com.lease.fw.ui.act;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;

import com.lease.fw.ui.base.BaseViewModel;
import com.lease.fw.ui.title.ITitleBarAttach;
import com.lease.fw.ui.title.TitleBarDelegate;
import com.lease.fw.ui.title.TitleBarView;

/**
 * 带标题页面承载, 可根据配置信息进行配置标题属性
 * @param <VM>
 */
public abstract class TitleActivity<VM extends BaseViewModel, TB extends TitleBarView>
        extends TaoqiActivity<VM> implements ITitleBarAttach {

    private TitleBarDelegate<VM, TB> delegate;

    protected TB titleBarView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        delegate = new TitleBarDelegate<>(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        delegate.destroy();
        delegate = null;
    }

    @Override
    public void setContentView(int layoutResID) {
        setContentView(delegate.buildContentView(LayoutInflater.from(this), obtainContentLayout()));
    }

    @Override
    public void setContentView(View view) {
        delegate.initTitleBar(this, this, viewModel);
        titleBarView = delegate.getTitleBarView();
        super.setContentView(view);
    }

    @Override
    public boolean isOverlayTitle() {
        return false;
    }

    @Override
    public int obtainTitleBarLayout() {
        return 0;
    }

    @Override
    public void setTitle(String titleStr) {
        titleBarView.setTitle(titleStr);
    }

    @Override
    public void setTitle(int titleId) {
        super.setTitle(titleId);
        titleBarView.setTitle(titleId);
    }
}
