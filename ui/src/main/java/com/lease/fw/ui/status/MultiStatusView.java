package com.lease.fw.ui.status;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.lease.fw.ui.R;
import com.lease.fw.ui.status.skeleton.SkeletonBuilder;
import com.lease.fw.ui.status.skeleton.SkeletonItem;

/**
 * created time: 2019-11-11
 * author: cqt
 * description: 多状态View
 */
public class MultiStatusView extends FrameLayout {

    private StatusDelegate delegate;

    private View contentView;

    private int loadLayoutId = R.layout.view_loading;

    private SkeletonBuilder skeletonBuilder;

    private int errorLayoutId = R.layout.view_error;

    private int emptyLayoutId = R.layout.view_empty;

    private int netLayoutId = R.layout.view_net_not_work;

    private View loadView;

    private View errorView;

    private View emptyView;

    private View netView;

    private OnReloadListener listener;

    MultiStatusView(View contentView, StatusDelegate delegate) {
        super(contentView.getContext());
        this.delegate = delegate;
        this.contentView = contentView;
    }

    public MultiStatusView load(int resId) {
        this.loadLayoutId = resId;
        return this;
    }

    public MultiStatusView skeleton(SkeletonItem ... items) {
        skeletonBuilder = SkeletonBuilder.with(items);
        return this;
    }

    public MultiStatusView skeleton(int bgColor, int skeletonColor, SkeletonItem ... items) {
        skeletonBuilder = SkeletonBuilder.with(items).skeletonColor(skeletonColor).backgroudColor(bgColor);
        return this;
    }

    public MultiStatusView error(int resId) {
        this.errorLayoutId = resId;
        return this;
    }

    public MultiStatusView empty(int resId) {
        this.emptyLayoutId = resId;
        return this;
    }

    public MultiStatusView netNotWork(int resId) {
        this.netLayoutId = resId;
        return this;
    }

    public MultiStatusView reloadListener(OnReloadListener listener) {
        this.listener = listener;
        return this;
    }

    public void show(Status status) {
        errorView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        netView.setVisibility(View.GONE);
        loadView.setVisibility(View.GONE);
        contentView.setVisibility(View.GONE);

        switch(status) {
            case EMPTY:
                emptyView.setVisibility(View.VISIBLE);
                break;
            case ERROR:
                errorView.setVisibility(View.VISIBLE);
                break;
            case NET_OFFILEN:
                netView.setVisibility(View.VISIBLE);
                break;
            case LOADING:
                loadView.setVisibility(View.VISIBLE);
                break;
            case DEFAULT:
            default:
                contentView.setVisibility(View.VISIBLE);
        }
    }

    public StatusDelegate build() {
        Context context = contentView.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        FrameLayout.LayoutParams params = new FrameLayout
                .LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        // loadView
        if(null != skeletonBuilder) {
            loadView = skeletonBuilder.build(context);
        }else {
            loadView = inflater.inflate(loadLayoutId, null);
        }
        addView(loadView, params);

        // errorView
        errorView = inflater.inflate(errorLayoutId, null);
        addView(errorView, params);
        setupReloadListener(errorView);

        // emptyView
        emptyView = inflater.inflate(emptyLayoutId, null);
        addView(emptyView, params);
        setupReloadListener(emptyView);

        // net
        netView = inflater.inflate(netLayoutId, null);
        addView(netView, params);
        setupReloadListener(netView);

        // contentView
        addView(contentView, params);

        // 默认显示内容View
        show(Status.DEFAULT);
        return delegate;
    }

    private void setupReloadListener(View view) {
        if(null == listener) {
            return;
        }

        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.reload();
            }
        });
    }
}