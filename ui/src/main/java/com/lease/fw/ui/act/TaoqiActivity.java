package com.lease.fw.ui.act;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;

import com.lease.fw.router.Router;
import com.lease.fw.ui.BuildConfig;
import com.lease.fw.ui.R;
import com.lease.fw.ui.UICentre;
import com.lease.fw.ui.base.BaseViewModel;
import com.lease.fw.ui.config.StatusBarConfig;
import com.lease.fw.ui.utils.LightStatusBarUtils;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import butterknife.ButterKnife;

/**
 * 基础承载页面
 * @param <VM>
 */
public abstract class TaoqiActivity<VM extends BaseViewModel> extends RxAppCompatActivity {

    protected VM viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(BuildConfig.isRouter) {
            Router.initParam(this);
        }
        this.initStatusBar();
        this.initViewModel();
        if(obtainContentLayout() > 0) {
            setContentView(obtainContentLayout());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(BuildConfig.isRouter) {
            setIntent(intent);
            Router.initParam(this);
        }
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        doInit();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        doInit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getLifecycle().removeObserver(viewModel);
    }

    private void initStatusBar() {
        StatusBarConfig statusBarConfig = null;
        if(null == buildStatusBarConfig()) {
            statusBarConfig = UICentre.getInstance().getUiConfig().getStatusBarConfig();
        }
        if(null == statusBarConfig) {
            return;
        }

        if(statusBarConfig.isLightStatus()) {
            LightStatusBarUtils.setLightStatusBar(this);
        }
    }

    private void doInit() {
        if(BuildConfig.isButterKnife) {
            ButterKnife.bind(this);
        }
        this.registerUIChangeLiveDataCallBack();
        this.initParams();
        this.setupView();
        this.initViewObservable();
        this.initData();
    }

    private void initViewModel() {
        this.viewModel = setupViewModel();
        if (null == this.viewModel) {
            Type type = this.getClass().getGenericSuperclass();
            Class modelClass;
            if (type instanceof ParameterizedType) {
                if(((ParameterizedType)type).getActualTypeArguments()[0] instanceof Class) {
                    modelClass = (Class)((ParameterizedType)type).getActualTypeArguments()[0];
                }else {
                    modelClass = BaseViewModel.class;
                }
            } else {
                modelClass = BaseViewModel.class;
            }

            this.viewModel = (VM) this.createViewModel(this, modelClass);
        }

        if(null == this.viewModel) {
            throw new IllegalStateException("viewModel can not be null!");
        }

        getLifecycle().addObserver(viewModel);
        viewModel.injectLifecycleProvider(this);
    }

    // 状态栏部分
    protected StatusBarConfig buildStatusBarConfig() {
        return null;
    }

    // 设置内容是否延伸到状态栏
    protected void setupTranslucentStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
    }

    protected void wrapperStatusBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if(null == toolbar) {
            return;
        }

        int statusBarHeight = (int) getResources().getDimension(R.dimen.statusbar_view_height);
        int actionBarHeight = toolbar.getMeasuredHeight();
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        toolbar.setPadding(0, statusBarHeight, 0, 0);
        toolbar.getLayoutParams().height = statusBarHeight + actionBarHeight;
        toolbar.requestLayout();
    }


    // 业务相关
    /**
     * 创建数据模型
     * @param activity 上下文
     * @param cls 模型类型
     * @param <T> 模型泛指
     * @return 数据模型
     */
    public <T extends ViewModel> T createViewModel(FragmentActivity activity, Class<T> cls) {
        return ViewModelProviders.of(activity).get(cls);
    }

    /**
     * 参数初始化
     */
    protected void initParams() {}

    /**
     * 初始化数据模型，可不重写
     * @return 数据模型
     */
    protected VM setupViewModel() {return null;}

    /**
     * 获取当前View内容
     * @return view内容id
     */
    protected abstract int obtainContentLayout();

    /**
     * view设置
     */
    protected void setupView() {}

    /**
     * view观察者设置
     */
    protected void initViewObservable() {}

    /**
     * 数据初始化
     */
    protected void initData() {}



    //注册ViewModel与View的契约UI回调事件
    private void registerUIChangeLiveDataCallBack() {
        //加载对话框显示
        viewModel.getUC().getShowDialogEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String title) {
                showDialog(title);
            }
        });
        //加载对话框消失
        viewModel.getUC().getDismissDialogEvent().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                dismissDialog();
            }
        });
        //跳入新页面
        viewModel.getUC().getStartActivityEvent().observe(this, new Observer<Map<String, Object>>() {
            @Override
            public void onChanged(@Nullable Map<String, Object> params) {
                Class<?> clz = (Class<?>) params.get(BaseViewModel.ParameterField.CLASS);
                Bundle bundle = (Bundle) params.get(BaseViewModel.ParameterField.BUNDLE);
                startActivity(clz, bundle);
            }
        });
        //跳入ContainerActivity
        viewModel.getUC().getStartContainerActivityEvent().observe(this, new Observer<Map<String, Object>>() {
            @Override
            public void onChanged(@Nullable Map<String, Object> params) {
                String canonicalName = (String) params.get(BaseViewModel.ParameterField.CANONICAL_NAME);
                Bundle bundle = (Bundle) params.get(BaseViewModel.ParameterField.BUNDLE);
                startContainerActivity(canonicalName, bundle);
            }
        });
        //关闭界面
        viewModel.getUC().getFinishEvent().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                finish();
            }
        });
        //关闭上一层
        viewModel.getUC().getOnBackPressedEvent().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                onBackPressed();
            }
        });
    }

    public void showDialog() {
        showDialog("");
    }

    public void showDialog(String title) {
        UICentre.getInstance()
                .getUiConfig()
                .getLoadingDialogConfig()
                .showLoadingDialog(this, title);
    }

    public void dismissDialog() {
        UICentre.getInstance()
                .getUiConfig()
                .getLoadingDialogConfig()
                .dismissDialog();
    }

    /**
     * 跳转页面
     *
     * @param clz 所跳转的目的Activity类
     */
    public void startActivity(Class<?> clz) {
        startActivity(new Intent(this, clz));
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     */
    public void startContainerActivity(String canonicalName) {
        startContainerActivity(canonicalName, null);
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     * @param bundle        跳转所携带的信息
     */
    public void startContainerActivity(String canonicalName, Bundle bundle) {
        Intent intent = new Intent(this, ContainerActivity.class);
        intent.putExtra(ContainerActivity.F_NAME, canonicalName);
        if (bundle != null) {
            intent.putExtra(ContainerActivity.F_BUNDLE, bundle);
        }
        startActivity(intent);
    }
}