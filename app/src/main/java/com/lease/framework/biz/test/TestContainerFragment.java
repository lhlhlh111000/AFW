package com.lease.framework.biz.test;

import com.lease.fw.ui.base.BaseViewModel;
import com.lease.fw.ui.frg.TitleFragment;
import com.lease.fw.ui.title.DefaultTitleBarView;

public class TestContainerFragment extends TitleFragment<BaseViewModel, DefaultTitleBarView> {

    @Override
    protected int obtainContentLayout() {
        return R.layout.act_test;
    }

    @Override
    protected void initParams() {
        super.initParams();
        ToastUtils.showToast(getActivity(), getArguments().getString("hello_msg"));
    }

    @Override
    protected void setupView() {
        super.setupView();

        setTitle("测试Fragment容器");
    }
}
