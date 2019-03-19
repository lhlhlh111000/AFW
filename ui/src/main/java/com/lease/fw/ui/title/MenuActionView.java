package com.lease.fw.ui.title;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lease.framework.core.ToastUtils;
import com.lease.fw.ui.R;
import com.lease.fw.ui.config.MenuAction;
import com.lease.fw.ui.config.TitleBarConfig;

public class MenuActionView extends LinearLayout {

    private TitleBarConfig titleBarConfig;

    private MenuAction action;

    public MenuActionView(Context context) {
        this(context, null);
    }

    public MenuActionView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuActionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        setupStyle();
    }

    public void setupConfig(TitleBarConfig titleBarConfig, MenuAction action) {
        this.titleBarConfig = titleBarConfig;
        this.action = action;

        setupStyle();
    }

    private void setupStyle() {
        if(null == action) {
            return;
        }

        ImageView ivAction = findViewById(R.id.iv_action);
        TextView tvAction = findViewById(R.id.tv_action);
        if(null == ivAction) {
            return;
        }

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                action.onAction();
            }
        });

        if(action.menuText >= 0) {
            setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ToastUtils.showToast(getContext(), action.menuText);
                    return false;
                }
            });
        }

        if(action.menuIcon <= 0) {
            ivAction.setVisibility(View.GONE);
        }else {
            ivAction.setVisibility(View.VISIBLE);
            tvAction.setVisibility(View.GONE);
            ivAction.setImageResource(action.menuIcon);
            return;
        }

        if(action.menuText <= 0) {
            tvAction.setVisibility(View.GONE);
        }else {
            tvAction.setText(action.menuText);
            tvAction.setVisibility(View.VISIBLE);
            tvAction.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleBarConfig.getActionTextSize());
            tvAction.setTextColor(titleBarConfig.getActionTextColor());
        }
    }
}