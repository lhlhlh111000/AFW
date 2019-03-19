package com.lease.fw.ui.act;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lease.fw.ui.R;
import com.lease.fw.ui.UICentre;
import com.lease.fw.ui.base.BaseViewModel;
import com.lease.fw.ui.config.LifecycleObserver;
import com.lease.fw.ui.config.MenuAction;
import com.lease.fw.ui.config.StatusBarConfig;
import com.lease.fw.ui.config.TitleBarConfig;
import com.lease.fw.ui.title.TitleBarView;
import com.lease.fw.ui.utils.LightStatusBarUtils;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public abstract class TaoqiActivity<VM extends BaseViewModel> extends RxAppCompatActivity {

    protected VM viewModel;

    protected TitleBarView titleBarView;

    protected MutableLiveData<TitleBarConfig> titleBarConfig = new MutableLiveData<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initStatusBar();
        this.doObserverLifecycle(0);
        this.initContentView();
        this.initTitleBar();
        this.initViewModel();
        this.registerUIChangeLiveDataCallBack();
        this.initParams();
        this.setupView();
        this.initViewObservable();
        this.initData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.doObserverLifecycle(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.doObserverLifecycle(2);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.doObserverLifecycle(3);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.doObserverLifecycle(4);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.doObserverLifecycle(5);
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

    private void initContentView() {
        if(!needTitle()) {
            setContentView(R.layout.layout_base_no_title);
        }else {
            if(isOverlayTitle()) {
                setContentView(R.layout.layout_overlay_base);
            }else {
                setContentView(R.layout.layout_base);
            }
        }

        View contentView = LayoutInflater.from(this).inflate(obtainContentLayout(), null);
        if(null != contentView) {
            ((ViewGroup) findViewById(R.id.layout_base_container)).addView(contentView,
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    private void initTitleBar() {
        if(!needTitle()) {
            return;
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 获取titleBar view
        if(-1 == obtainTitleBarLayout()) {
            titleBarView = (TitleBarView) toolbar.getChildAt(0);
        }else {
            toolbar.removeAllViews();
            titleBarView = (TitleBarView) LayoutInflater.from(this).inflate(obtainTitleBarLayout(), null);
            toolbar.addView(titleBarView, Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.MATCH_PARENT);
        }

        // 配置标题栏
        if(null == titleBarConfig.getValue()) {
            titleBarConfig.setValue(UICentre.getInstance().getUiConfig().getTitleBarConfig());
        }
        // 右侧菜单栏
        TitleBarConfig config = titleBarConfig.getValue();
        config.setActions(buildMenuActions());

        // 构建菜单栏
        titleBarView.setupTitleBarConfig(config, viewModel);
        titleBarConfig.observe(this, new Observer<TitleBarConfig>() {
            @Override
            public void onChanged(@Nullable TitleBarConfig config) {
                TitleBarConfig config1 = titleBarConfig.getValue();
                config1.setActions(buildMenuActions());
                titleBarView.setupTitleBarConfig(config1, viewModel);
            }
        });

        updateTitleBarConfig();
    }

    private void initViewModel() {
        this.viewModel = setupViewModel();
        if (null == this.viewModel) {
            Type type = this.getClass().getGenericSuperclass();
            Class modelClass;
            if (type instanceof ParameterizedType) {
                modelClass = (Class)((ParameterizedType)type).getActualTypeArguments()[0];
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


    // 标题栏部分
    /**
     * 是否需要标题
     * @return
     */
    protected boolean needTitle() {
        return true;
    }

    /**
     * 是否覆盖式标题
     * @return
     */
    protected boolean isOverlayTitle() {
        return false;
    }

    /**
     * 设置titleBar view
     * @return titleBar view
     */
    protected int obtainTitleBarLayout() {
        return -1;
    }

    /**
     * 更新标题栏配置信息
     */
    protected void updateTitleBarConfig() {}

    /**
     * 构建右侧菜单列表
     * @return
     */
    protected List<MenuAction> buildMenuActions() {
        return null;
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
    protected void setupView() { }

    /**
     * view观察者设置
     */
    protected void initViewObservable() {}

    /**
     * 数据初始化
     */
    protected void initData() {}





    // 生命周期观察相关
    /**
     * 是否需要观察生命周期
     * @return
     */
    protected boolean needObserverLifecycle() {
        return true;
    }

    /**
     * 获取观察对象class类型
     * @return
     */
    protected Class obtainLifecycleClass() {
        return this.getClass();
    }

    /**
     * 观察生命周期
     */
    private final void doObserverLifecycle(int actionStep) {
        if(!needObserverLifecycle()) {
            return;
        }
        if(null == UICentre.getInstance().getUiConfig().getLifecycleConfig()
                || null == UICentre.getInstance().getUiConfig().getLifecycleConfig().getObservers()) {

        }

        for(LifecycleObserver observer : UICentre.getInstance().getUiConfig().getLifecycleConfig().getObservers()) {
            switch(actionStep) {
                case 0:
                    observer.onCreate(obtainLifecycleClass());
                    break;
                case 1:
                    observer.onStart(obtainLifecycleClass());
                    break;
                case 2:
                    observer.onResume(obtainLifecycleClass());
                    break;
                case 3:
                    observer.onPause(obtainLifecycleClass());
                    break;
                case 4:
                    observer.onStop(obtainLifecycleClass());
                    break;
                case 5:
                    observer.onDestroy(obtainLifecycleClass());
                    break;
            }
        }
    }


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

    public void showDialog(String title) {
//        if (dialog != null) {
//            dialog.show();
//        } else {
//            MaterialDialog.Builder builder = MaterialDialogUtils.showIndeterminateProgressDialog(this, title, true);
//            dialog = builder.show();
//        }
    }

    public void dismissDialog() {
//        if (dialog != null && dialog.isShowing()) {
//            dialog.dismiss();
//        }
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
//        Intent intent = new Intent(this, ContainerActivity.class);
//        intent.putExtra(ContainerActivity.FRAGMENT, canonicalName);
//        if (bundle != null) {
//            intent.putExtra(ContainerActivity.BUNDLE, bundle);
//        }
//        startActivity(intent);
    }
}