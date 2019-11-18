package com.lease.fw.ui.act;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;

import com.lease.fw.ui.base.BaseViewModel;
import com.lease.fw.ui.config.MenuAction;
import com.lease.fw.ui.title.ITitleBarAttach;
import com.lease.fw.ui.title.TitleBarConfigWrapper;
import com.lease.fw.ui.title.TitleBarDelegate;

import java.util.List;

/**
 * 带标题页面承载, 可根据配置信息进行配置标题属性
 * @param <VM>
 */
public abstract class TitleActivity<VM extends BaseViewModel>
        extends TaoqiActivity<VM> implements ITitleBarAttach {

    private TitleBarDelegate<VM> delegate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        delegate = new TitleBarDelegate<VM>(this);
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
    public List<MenuAction> buildMenuActions() {
        return null;
    }

    @Override
    public void setTitle(String titleStr) {
        getTitleBarConfig().setTitleText(titleStr);
    }

    @Override
    public void setTitle(int titleId) {
        super.setTitle(titleId);
        setTitle(getString(titleId));
    }

    @Override
    public TitleBarConfigWrapper getTitleBarConfig() {
        return delegate.getTitleBarConfig();
    }
}
