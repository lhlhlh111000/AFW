package com.lease.framework.biz.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lease.fw.ui.act.TaoqiActivity;
import com.lease.fw.ui.base.BaseViewModel;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.List;

public class MainActivity extends TaoqiActivity<BaseViewModel> {

    @Override
    protected int obtainContentLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void setupView() {
        super.setupView();
        findViewById(R.id.btn_switch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TestActivity.class));
            }
        });
        findViewById(R.id.btn_network).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NetworkTestActivity.class));
            }
        });
        findViewById(R.id.btn_permission).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testPermission();
            }
        });
        findViewById(R.id.btn_container_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("hello_msg", "这是传递参数");
                startContainerActivity(TestContainerFragment.class.getCanonicalName(), bundle);
            }
        });

        findViewById(R.id.btn_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startContainerActivity(ListFragment.class.getCanonicalName());
            }
        });
    }

    private void testPermission() {
        AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.CAMERA)
                .rationale(new RuntimeRationale())
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        ToastUtils.showToast(MainActivity.this, "允许");
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        if(AndPermission.hasAlwaysDeniedPermission(MainActivity.this, data)) {
                            AndPermission.with(MainActivity.this).runtime().setting().start(1000);
                        }else {
                            ToastUtils.showToast(MainActivity.this, "拒绝");
                        }
                    }
                })
                .start();
    }
}