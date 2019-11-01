package com.lease.framework.biz.test;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lease.fw.ui.base.BaseViewModel;
import com.lease.fw.ui.frg.TitleFragment;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

public class ListFragment extends TitleFragment<BaseViewModel> {

    private int count = 20;

    private ListAdapter adapter;

    @Override
    protected int obtainContentLayout() {
        return R.layout.frg_list;
    }

    @Override
    protected void setupView() {
        super.setupView();

        setTitle("下拉列表");

        final SmartRefreshLayout srl = getView().findViewById(R.id.srl_list);
        srl.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                refreshLayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        count = 20;
                        adapter.notifyDataSetChanged();
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
                        count += 20;
                        adapter.notifyDataSetChanged();
                        refreshLayout.finishLoadMore();
                    }
                }, 2000);
            }
        });

        RecyclerView rcv = getView().findViewById(R.id.rcv_list);
        rcv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ListAdapter();
        rcv.setAdapter(adapter);
    }


    class ListAdapter extends RecyclerView.Adapter<ListViewHolder> {

        @NonNull
        @Override
        public ListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            TextView tvText = new TextView(getActivity());
            int height = (int) (56*getResources().getDisplayMetrics().density);
            tvText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
            tvText.setGravity(Gravity.CENTER_VERTICAL);
            tvText.setPadding(10, 10, 10, 10);
            return new ListViewHolder(tvText);
        }

        @Override
        public void onBindViewHolder(@NonNull ListViewHolder listViewHolder, int i) {
            ((TextView) listViewHolder.itemView).setText("Item " + i);
        }

        @Override
        public int getItemCount() {
            return count;
        }
    }

    class ListViewHolder extends RecyclerView.ViewHolder {

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
