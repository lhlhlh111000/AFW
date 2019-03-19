package com.lease.fw.ui.title;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.lease.fw.ui.base.BaseViewModel;
import com.lease.fw.ui.config.TitleBarConfig;

public abstract class TitleBarView<T extends BaseViewModel> extends RelativeLayout {

    protected TitleBarConfig titleBarConfig;

    protected T vm;

    public TitleBarView(Context context) {
        this(context, null);
    }

    public TitleBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public abstract void setupTitleBarConfig(TitleBarConfig config, T vm);

}