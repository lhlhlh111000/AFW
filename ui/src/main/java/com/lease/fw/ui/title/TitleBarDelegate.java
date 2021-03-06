package com.lease.fw.ui.title;

import android.app.Activity;
import android.arch.lifecycle.LifecycleOwner;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lease.fw.ui.R;
import com.lease.fw.ui.UICentre;
import com.lease.fw.ui.base.BaseViewModel;
import com.lease.fw.ui.config.TitleBarConfig;

public class TitleBarDelegate<VM extends BaseViewModel, TB extends TitleBarView> {

    private ITitleBarAttach titleBarAttach;

    private View rootView;

    private TB titleBarView;

    public TitleBarDelegate(ITitleBarAttach iTitleBarAttach) {
        if(null == iTitleBarAttach) {
            throw new IllegalStateException("title bar attach is null");
        }
        this.titleBarAttach = iTitleBarAttach;
    }

    /**
     * 创建Title Bar容器View
     * @param inflater inflater
     * @param contentLayoutId 布局资源id
     * @return
     */
    public View buildContentView(LayoutInflater inflater, int contentLayoutId) {
        if(null == inflater || contentLayoutId <= -1) {
            throw new IllegalStateException("build content view error, inflater or layout id empty");
        }

        View view;
        if(titleBarAttach.isOverlayTitle()) {
            view = inflater.inflate(R.layout.layout_overlay_base, null);
        }else {
            view = inflater.inflate(R.layout.layout_base, null);
        }

        View contentView = LayoutInflater.from(inflater.getContext()).inflate(contentLayoutId, null);
        if(null != contentView) {
            ((ViewGroup) view.findViewById(R.id.layout_base_container)).addView(contentView,
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        rootView = view;
        return view;
    }

    /**
     * 初始化Title Bar
     * @param owner 生命周期持有者
     * @param viewModel vm
     */
    public void initTitleBar(Activity activity, LifecycleOwner owner, final VM viewModel) {
        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        if(activity instanceof AppCompatActivity) {
            ((AppCompatActivity) activity).setSupportActionBar(toolbar);
        }

        // 获取titleBar view
        if(titleBarAttach.obtainTitleBarLayout() <= 0) {
            titleBarView = (TB) toolbar.getChildAt(0);
        }else {
            // 设置自定义TitleBarView
            toolbar.removeAllViews();
            titleBarView = (TB) LayoutInflater.from(rootView.getContext()).inflate(titleBarAttach.obtainTitleBarLayout(), null);
            toolbar.addView(titleBarView, Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.MATCH_PARENT);
        }

        // 构建菜单栏
        titleBarView.setupTitleBarConfig(new TitleBarConfig(UICentre.getInstance().getUiConfig().getTitleBarConfig()), viewModel);
        toolbar.setBackgroundColor(UICentre.getInstance().getUiConfig().getTitleBarConfig().getTitleBarBackgroundColor());
    }

    /**
     * 菜单View获取
     * @return
     */
    public TB getTitleBarView() {
        return titleBarView;
    }

    /**
     * 销毁
     */
    public void destroy() {
        rootView = null;
        titleBarView = null;
        titleBarAttach = null;
    }
}