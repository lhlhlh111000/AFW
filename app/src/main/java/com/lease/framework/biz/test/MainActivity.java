package com.lease.framework.biz.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.lease.framework.biz.test.frg.MultiRecyclerStatusFragment;
import com.lease.framework.biz.test.frg.MultiStatusFragment;
import com.lease.framework.biz.test.frg.ParamTestFragment;
import com.lease.framework.biz.test.frg.ToastFragment;
import com.lease.fw.router.Router;
import com.lease.fw.router.RouterUtil;
import com.lease.fw.router.callback.RouterCallBacker;
import com.lease.fw.router.config.RouterConfig;
import com.lease.fw.router.config.SwitchTargetClass;
import com.lease.fw.ui.act.TaoqiActivity;
import com.lease.fw.ui.base.BaseViewModel;
import com.lease.fw.ui.toast.ToastUtil;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends TaoqiActivity<BaseViewModel> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RouterConfig.Builder builder = new RouterConfig.Builder();
        builder.switchTargetClass(new SwitchTargetClass() {
            @Override
            public void start(Context context, Class clazz, Bundle bundle) {
                if(context instanceof TaoqiActivity) {
                    if(RouterUtil.isFragment(clazz)) {
                        ((TaoqiActivity) context).startContainerActivity(clazz.getCanonicalName(), bundle);
                    }else {
                        ((TaoqiActivity) context).startActivity(clazz, bundle);
                    }
                }
            }
        });
        Router.setConfig(builder.build());
    }

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
                Bundle bundle = new Bundle();
                bundle.putString("hello", "hello");
                startActivity(TestActivity.class, bundle);
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

        findViewById(R.id.btn_toast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startContainerActivity(ToastFragment.class.getCanonicalName());
            }
        });

        findViewById(R.id.btn_status).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startContainerActivity(MultiStatusFragment.class.getCanonicalName());
                startContainerActivity(MultiRecyclerStatusFragment.class.getCanonicalName());
            }
        });

        findViewById(R.id.btn_param).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                ArrayList<String> list = new ArrayList<>();
                list.add("hello");
                list.add("hello");
                list.add("hello");
                list.add("hello");
                list.add("hello");
                list.add("hello");
                bundle.putStringArrayList("list", list);
                bundle.putString("message", "hello, i'm the trans param");
                ParamTestFragment.SEntity sEntity = new ParamTestFragment.SEntity();
                sEntity.setName("this a Serializable entity");
                bundle.putSerializable("sEntity", sEntity);
                ParamTestFragment.PEntity pEntity = new ParamTestFragment.PEntity();
                pEntity.setName("this a Parcelable entity");
                bundle.putParcelable("pEntity", pEntity);

                ArrayList<ParamTestFragment.PEntity> pEntities = new ArrayList<>();
                pEntities.add(pEntity);
                pEntities.add(pEntity);
                pEntities.add(pEntity);
                pEntities.add(pEntity);
                bundle.putParcelableArrayList("pEntitys", pEntities);
//                startContainerActivity(ParamTestFragment.class.getCanonicalName(), bundle);
                Router.router(MainActivity.this,
                        ParamTestFragment.class,
                        new RouterCallBacker<String>() {
                            @Override
                            public void callBackResult(String result) {
                                ToastUtil.show(result);
                            }
                        },
                        bundle);
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