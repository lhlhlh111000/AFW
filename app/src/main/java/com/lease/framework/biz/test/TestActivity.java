package com.lease.framework.biz.test;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.lease.framework.core.ToastUtils;
import com.lease.fw.ui.act.TaoqiActivity;
import com.lease.fw.ui.config.MenuAction;
import com.lease.fw.ui.config.TitleBarConfig;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends TaoqiActivity<TestViewModel> {

    @Override
    protected int obtainContentLayout() {
        return R.layout.ac_container;
    }

//    @Override
//    protected boolean isOverlayTitle() {
//        return true;
//    }

    @Override
    protected boolean needTitle() {
        return false;
    }

    @Override
    protected List<MenuAction> buildMenuActions() {
        List<MenuAction> actions = new ArrayList<>();
        actions.add(new MenuAction(R.mipmap.icon_saoyisaotext_blank, R.string.scan) {
            @Override
            public void onAction() {
                ToastUtils.showToast(TestActivity.this, "扫码点击");
            }
        });
        actions.add(new MenuAction(R.string.phone) {
            @Override
            public void onAction() {
                ToastUtils.showToast(TestActivity.this, "联系客服");
            }
        });
        return actions;
    }

    @Override
    protected void updateTitleBarConfig() {
        super.updateTitleBarConfig();
        if(null == titleBarConfig.getValue()) {
            return;
        }

        TitleBarConfig config = titleBarConfig.getValue();
        config.setTitleText("测试标题");
        config.setTitleBarBackgroundColor(Color.TRANSPARENT);
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
