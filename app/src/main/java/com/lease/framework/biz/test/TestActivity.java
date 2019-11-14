package com.lease.framework.biz.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.lease.fw.router.param.BindParam;
import com.lease.fw.ui.act.TaoqiActivity;
import com.lease.fw.ui.toast.ToastUtil;

public class TestActivity extends TaoqiActivity<TestViewModel> {

    @BindParam("hello")
    public String msg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ToastUtil.show(msg);
    }

    @Override
    protected int obtainContentLayout() {
        return R.layout.ac_container;
    }

    @Override
    protected void setupView() {
        super.setupView();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = Fragment.instantiate(this, TestFragment.class.getName());
        ft.replace(R.id.frl_container, fragment);
        ft.commitAllowingStateLoss();
    }
}