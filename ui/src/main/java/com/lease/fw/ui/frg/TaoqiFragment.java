package com.lease.fw.ui.frg;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lease.fw.ui.R;
import com.lease.fw.ui.UICentre;
import com.lease.fw.ui.base.BaseViewModel;
import com.lease.fw.ui.config.MenuAction;
import com.lease.fw.ui.config.TitleBarConfig;
import com.lease.fw.ui.title.TitleBarView;
import com.trello.rxlifecycle2.components.support.RxFragment;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public abstract class TaoqiFragment<VM extends BaseViewModel> extends RxFragment {

    protected VM viewModel;

    protected TitleBarView titleBarView;

    protected MutableLiveData<TitleBarConfig> titleBarConfig = new MutableLiveData<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return this.initContentView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.initTitleBar(view);
        this.initViewModel();
        this.registerUIChangeLiveDataCallBack();
        this.initParams();
        this.setupView();
        this.initViewObservable();
        this.initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getLifecycle().removeObserver(viewModel);
    }

    private View initContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        if(!needTitle()) {
            view = inflater.inflate(R.layout.layout_base_no_title, container, false);
        }else {
            if(isOverlayTitle()) {
                view = inflater.inflate(R.layout.layout_overlay_base, container, false);
            }else {
                view = inflater.inflate(R.layout.layout_base, container, false);
            }
        }

        View contentView = LayoutInflater.from(getActivity()).inflate(obtainContentLayout(), null);
        if(null != contentView) {
            ((ViewGroup) view.findViewById(R.id.layout_base_container)).addView(contentView,
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        return view;
    }

    private void initTitleBar(View view) {
        if(!needTitle()) {
            return;
        }
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        // 获取titleBar view
        if(-1 == obtainTitleBarLayout()) {
            titleBarView = (TitleBarView) toolbar.getChildAt(0);
        }else {
            toolbar.removeAllViews();
            titleBarView = (TitleBarView) LayoutInflater.from(getActivity()).inflate(obtainTitleBarLayout(), null);
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

            this.viewModel = (VM) this.createViewModel(getActivity(), modelClass);
        }

        if(null == this.viewModel) {
            throw new IllegalStateException("viewModel can not be null!");
        }

        getLifecycle().addObserver(viewModel);
        viewModel.injectLifecycleProvider(this);
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
     * 初始化数据模型
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
                getActivity().finish();
            }
        });
        //关闭上一层
        viewModel.getUC().getOnBackPressedEvent().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                getActivity().onBackPressed();
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
        startActivity(new Intent(getActivity(), clz));
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent(getActivity(), clz);
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