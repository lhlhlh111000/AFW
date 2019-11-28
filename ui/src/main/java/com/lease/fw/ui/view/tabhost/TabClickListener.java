package com.lease.fw.ui.view.tabhost;

/**
 * Created by Administrator on 2015/8/26.
 */
public interface TabClickListener {
    public void tabChange(String fromTag, String toTag);

    /**
     * 切换Fragemnt前
     *
     * @param fromTag
     * @param toTag
     */
    public void tabChangeBefore(String fromTag, String toTag);

    /**
     * 拦截切换事件
     * @param fromTag
     * @param toTag
     * @return
     */
    public boolean interceptTabChange(String fromTag, String toTag);
}
