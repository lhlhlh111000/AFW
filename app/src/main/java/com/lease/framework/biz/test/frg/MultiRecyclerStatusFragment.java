package com.lease.framework.biz.test.frg;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lease.framework.biz.test.R;
import com.lease.fw.ui.base.BaseViewModel;
import com.lease.fw.ui.frg.TaoqiFragment;
import com.lease.fw.ui.status.StatusDelegate;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

/**
 * created time: 2019-11-11
 * author: cqt
 * description:
 */
public class MultiRecyclerStatusFragment extends TaoqiFragment<BaseViewModel> {

    private StatusDelegate statusDelegate;

    @Override
    protected int obtainContentLayout() {
        return R.layout.view_sm;
    }

    @Override
    protected void setupView() {
        super.setupView();

        RecyclerView rc = getView().findViewById(R.id.rcv_list);

        statusDelegate = new StatusDelegate(rc).build();

        final BaseQuickAdapter adapter = new AAdapter(R.layout.item_layout);
        adapter.setNewData(getData());
        rc.setLayoutManager(new LinearLayoutManager(getContext()));
        rc.setAdapter(adapter);

        final SmartRefreshLayout srl = getView().findViewById(R.id.srl_list);
        srl.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                refreshLayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setNewData(getData());
                        refreshLayout.finishRefresh();
                    }
                }, 2000);
            }
        });
        srl.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                refreshLayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.addData(getData());
                        refreshLayout.finishLoadMore();
                    }
                }, 2000);
            }
        });

        statusDelegate.showLoadingView();
        rc.postDelayed(new Runnable() {
            @Override
            public void run() {
                statusDelegate.showContentView();
            }
        }, 3000);
    }

    private List<String> getData() {
        List<String> data = new ArrayList<>();
        for (int i=0; i<20; i++) {
            data.add("hello " + i);
        }
        return data;
    }

    class AAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public AAdapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder helper, String item) {
            helper.setText(R.id.tv_item, item);
        }
    }
}