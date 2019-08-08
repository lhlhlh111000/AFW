package com.lease.framework.biz.test;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.lease.framework.core.ToastUtils;
import com.lease.fw.ui.base.BaseViewModel;
import com.lease.fw.ui.config.MenuAction;
import com.lease.fw.ui.config.TitleBarConfig;
import com.lease.fw.ui.frg.TaoqiFragment;

import java.util.ArrayList;
import java.util.List;

public class TestFragment extends TaoqiFragment<BaseViewModel> {

    @Override
    protected int obtainContentLayout() {
        return R.layout.act_test;
    }

    @Override
    protected void updateTitleBarConfig() {
        super.updateTitleBarConfig();
        TitleBarConfig config = titleBarConfig.getValue();
        config.setTitleText("测试");
    }

    @Override
    protected List<MenuAction> buildMenuActions() {
        List<MenuAction> actions = new ArrayList<>();
        actions.add(new MenuAction(R.mipmap.icon_saoyisaotext_blank, R.string.scan) {
            @Override
            public void onAction() {
                ToastUtils.showToast(getActivity(), "扫码点击");
            }
        });
        actions.add(new MenuAction(R.string.phone) {
            @Override
            public void onAction() {
                ToastUtils.showToast(getActivity(), "联系客服");
                showAlertDialog();
            }
        });
        return actions;
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Tips")
                .setMessage("Message")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

    }
}