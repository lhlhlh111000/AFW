package com.lease.framework.biz.test;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.lease.fw.ui.act.TaoqiActivity;

public class TestActivity extends TaoqiActivity<TestViewModel> {

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