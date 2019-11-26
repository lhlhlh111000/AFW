package com.lease.framework.biz.test;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.lease.fw.ui.base.BaseViewModel;
import com.lease.fw.ui.config.MenuAction;
import com.lease.fw.ui.frg.TitleFragment;
import com.lease.fw.ui.title.DefaultTitleBarView;

import java.util.ArrayList;
import java.util.List;

public class TestFragment extends TitleFragment<BaseViewModel, DefaultTitleBarView> {

    @Override
    protected int obtainContentLayout() {
        return R.layout.act_test;
    }

    @Override
    protected void setupView() {
        super.setupView();
        setTitle("测试标题");

        List<MenuAction> actions = new ArrayList<>();
        actions.add(new MenuAction(R.mipmap.icon_saoyisaotext_blank, R.string.scan) {
            @Override
            public void onAction() {
                ToastUtils.showToast(getActivity(), "扫码点击");

                setTitle("扫码点击");
            }
        });
        actions.add(new MenuAction(R.string.phone) {
            @Override
            public void onAction() {
                ToastUtils.showToast(getActivity(), "联系客服");
                showAlertDialog();

            }
        });
        titleBarView.setupMenuActions(actions);
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Tips")
                .setMessage("Message")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setTitle("取消");
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setTitle("确定");
                    }
                }).show();

    }
}