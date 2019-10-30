package com.lease.fw.ui.act;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.lease.fw.ui.R;
import com.lease.fw.ui.base.BaseViewModel;

/**
 * fragment承载容器
 */
public class ContainerActivity extends TaoqiActivity<BaseViewModel> {

    public static String F_NAME = "FRAGMENT_NAME";

    public static String F_BUNDLE = "FRAGMENT_BUNDLE";

    @Override
    protected int obtainContentLayout() {
        return R.layout.layout_base_no_title;
    }

    @Override
    protected void setupView() {
        super.setupView();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        String fragmentName = getIntent().getStringExtra(F_NAME);
        Bundle bundle = getIntent().getBundleExtra(F_BUNDLE);
        if(null == bundle) {
            bundle = new Bundle();
        }
        Fragment fragment = Fragment.instantiate(this, fragmentName, bundle);
        ft.replace(R.id.layout_base_container, fragment, fragmentName);
        ft.commitAllowingStateLoss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(getIntent().getStringExtra(F_NAME));
        if(null == fragment) {
            return;
        }
        fragment.onActivityResult(requestCode, resultCode, data);
    }
}