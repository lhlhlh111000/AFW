package com.lease.fw.ui.title;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lease.fw.ui.R;
import com.lease.fw.ui.base.BaseViewModel;
import com.lease.fw.ui.config.MenuAction;
import com.lease.fw.ui.config.TitleBarConfig;

import java.util.List;

public class DefaultTitleBarView extends TitleBarView {

    public DefaultTitleBarView(Context context) {
        this(context, null);
    }

    public DefaultTitleBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultTitleBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setupStyle();
    }

    @Override
    public void setTitle(int titleRes) {
        setTitle(getContext().getText(titleRes).toString());
    }

    @Override
    public void setTitle(String titleStr) {
        if (TextUtils.isEmpty(titleStr)) {
            return;
        }
        TextView tvTitle = findViewById(R.id.tv_title);
        if(null == tvTitle) {
            return;
        }

        tvTitle.setText(titleStr);
    }

    @Override
    public void setupTitleBarConfig(TitleBarConfig config, BaseViewModel vm) {
        this.titleBarConfig = config;
        this.vm = vm;

        setupStyle();
    }

    protected void setupStyle() {
        if(null == titleBarConfig) {
            return;
        }

        setVisibility(titleBarConfig.getShow());
        final ImageView ivBack = findViewById(R.id.iv_back);
        if(null == ivBack) {
            return;
        }
        TextView tvTitle = findViewById(R.id.tv_title);
        LinearLayout linActions = findViewById(R.id.lin_actions);

        // 返回按钮
        ivBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != vm) {
                    vm.onBackPressed();
                }
            }
        });
        ivBack.setImageResource(this.titleBarConfig.getBackIcon());
        ivBack.setVisibility(this.titleBarConfig.getBackShow());

        // 标题文字
        tvTitle.setTextColor(this.titleBarConfig.getTitleTextColor());
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, this.titleBarConfig.getTitleTextSize());
        if(!TextUtils.isEmpty(titleBarConfig.getTitleText())) {
            tvTitle.setText(titleBarConfig.getTitleText());
        }
        LayoutParams params = (LayoutParams) tvTitle.getLayoutParams();
        if(this.titleBarConfig.getTitleTextGravity() == Gravity.CENTER) {
            params.addRule(CENTER_IN_PARENT);
        }else if(this.titleBarConfig.getTitleTextGravity() == Gravity.LEFT) {
            params.addRule(LEFT_OF, R.id.iv_back);
        }
        tvTitle.setLayoutParams(params);

        // 背景
        setBackgroundColor(this.titleBarConfig.getTitleBarBackgroundColor());

        // 右侧菜单
        if(null != titleBarConfig.getActions()) {
            TypedValue typedValue = new TypedValue();
            getContext().getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true);
            int actionBarSize = (int) typedValue.getDimension(getContext().getResources().getDisplayMetrics());
            linActions.removeAllViews();
            for(MenuAction action : titleBarConfig.getActions()) {
                MenuActionView actionView = (MenuActionView) LayoutInflater.from(getContext()).inflate(R.layout.menu_action, null);
                actionView.setupConfig(titleBarConfig, action);
                linActions.addView(actionView, actionBarSize, LinearLayout.LayoutParams.MATCH_PARENT);
            }
        }
    }

    /**
     * 显示隐藏返回按钮
     * @param visible
     */
    public void showOrHideBack(int visible) {
        ImageView ivBack = findViewById(R.id.iv_back);
        if(null == ivBack) {
            return;
        }

        ivBack.setVisibility(visible);
    }

    /**
     * 设置右侧菜单
     * @param menuActions
     */
    public void setupMenuActions(List<MenuAction> menuActions) {
        LinearLayout linActions = findViewById(R.id.lin_actions);
        linActions.removeAllViews();
        if(null == menuActions || menuActions.size() <= 0) {
            return;
        }
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true);
        int actionBarSize = (int) typedValue.getDimension(getContext().getResources().getDisplayMetrics());
        for(MenuAction action : menuActions) {
            MenuActionView actionView = (MenuActionView) LayoutInflater.from(getContext()).inflate(R.layout.menu_action, null);
            actionView.setupConfig(titleBarConfig, action);
            linActions.addView(actionView, actionBarSize, LinearLayout.LayoutParams.MATCH_PARENT);
        }
    }
}