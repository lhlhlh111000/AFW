package com.lease.fw.ui.frg;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lease.fw.ui.base.BaseViewModel;
import com.lease.fw.ui.title.ITitleBarAttach;
import com.lease.fw.ui.title.TitleBarDelegate;
import com.lease.fw.ui.title.TitleBarView;

/**
 * 带标题的基础Fragment
 * @param <VM>
 */
public abstract class TitleFragment<VM extends BaseViewModel, TB extends TitleBarView>
        extends TaoqiFragment<VM> implements ITitleBarAttach {

    private TitleBarDelegate<VM, TB> delegate;

    protected TB titleBarView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        delegate = new TitleBarDelegate<>(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        delegate.destroy();
        delegate = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return delegate.buildContentView(inflater, obtainContentLayout());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        delegate.initTitleBar(getActivity(), this, viewModel);
        titleBarView = delegate.getTitleBarView();
        super.onViewCreated(view, savedInstanceState);
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
    public void setTitle(int titleRes) {
        titleBarView.setTitle(titleRes);
    }

    @Override
    public void setTitle(String titleStr) {
        titleBarView.setTitle(titleStr);
    }
}
