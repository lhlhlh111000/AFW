package com.lease.fw.ui.view.tabhost;

/**
 * Created by Administrator on 2015/8/26.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;

import com.lease.framework.core.LogUtils;
import com.lease.fw.ui.R;

import java.util.HashMap;
import java.util.List;

/**
 * This is a helper class that implements a generic mechanism for
 * associating fragments with the tabs in a tab host. It relies on a trick.
 * Normally a tab host has a simple API for supplying a View or Intent that
 * each tab will show. This is not sufficient for switching between
 * fragments. So instead we make the content part of the tab host 0dp high
 * (it is not shown) and the TabManager supplies its own dummy view to show
 * as the tab content. It listens to changes in tabs, and takes care of
 * switch to the correct fragment shown in a separate content area whenever
 * the selected tab changes.
 */
public class TabManager implements TabHost.OnTabChangeListener, View.OnTouchListener {
    private static final String TAG = "UI->TabManager";
    private final Context mActivity;
    private final TabHost mTabHost;
    private final int mContainerId;
    private final HashMap<String, TabInfo> mTabs = new HashMap<String, TabInfo>();
    private FragmentManager mFragmentManager;
    private TabClickListener tabClickListener;
    /**
     * 当前Tab的上一个Tab
     */
    TabInfo mPreviousTab;
    /**
     * 当前（最后一个）Tab
     */
    TabInfo mLastTab;


    final class TabInfo {
        private final String tag;
        private final Class<?> clss;
        private final Bundle args;
        private Fragment fragment;

        TabInfo(String tag, Class<?> clazz, Bundle args) {
            this.tag = tag;
            this.clss = clazz;
            this.args = args;
        }
    }

    public TabManager(Context activity, TabHost tabHost,
                      int containerId, FragmentManager fragmentManager) {
        mActivity = activity;
        mTabHost = tabHost;
        mContainerId = containerId;
        this.mFragmentManager = fragmentManager;
        mTabHost.setOnTabChangedListener(this);
        mTabHost.setup();
    }

    public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
        tabSpec.setContent(new DummyTabFactory(mActivity));
        String tag = tabSpec.getTag();

        TabInfo info = new TabInfo(tag, clss, args);

        if (clss != null) {

            info.fragment = mFragmentManager.findFragmentByTag(tag);
            if (info.fragment != null && !info.fragment.isDetached()) {
                FragmentTransaction ft = mFragmentManager.beginTransaction();
                ft.hide(info.fragment);
                ft.commitAllowingStateLoss();
                //LogUtils.d(TAG, "hide fragment " + info.tag);
            }
        }

        mTabs.put(tag, info);
        mTabHost.addTab(tabSpec);

    }

    public void requestTabHeight(List<Integer> tabs, final int height) {
        TabWidget tabWidget = mTabHost.getTabWidget();
        for (int i = 0; i < tabWidget.getChildCount(); i++) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabWidget.getChildAt(i).getLayoutParams();
            params.gravity = Gravity.BOTTOM;
            for (Integer index : tabs) {
                if (i == index) {
                    final View widget = tabWidget.getChildAt(index);
                    final int HIGHER = 0;//对tab高度做特殊定制,默认0表示等高
                    final int widget_height = widget.getLayoutParams().height = height + HIGHER;
                    final int widget_width = widget.getWidth();
                    widget.invalidate();
                    if (widget.findViewById(R.id.tab_widget_text).getVisibility() == View.GONE) {
                        ImageView imageView = (ImageView) widget.findViewById(R.id.tab_widget_icon);
                        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                        layoutParams.height = widget_height;
                        imageView.setLayoutParams(layoutParams);
                        imageView.requestLayout();
                        imageView.invalidate();
                    }
                    //若需要将tab icon高出其他tab显示，则需要对这个tab的背景色做特殊处理
                    if (widget_height > height) {
                        //TODO: need more test
                        Drawable drawable = new Drawable() {
                            @Override
                            public void draw(Canvas canvas) {
                                //tab高出区域背景透明
                                Paint transparent = new Paint();
                                transparent.setColor(0x00000000);
                                canvas.drawRect(0, 0, widget_width, widget_height - height, transparent);
                                //灰色分隔线
                                Paint gray = new Paint();
                                gray.setColor(0xffdddddd);
                                canvas.drawLine(0, widget_height - height, widget_width, widget_height - height, gray);
                                //tab白色背景
                                Paint white = new Paint();
                                white.setColor(0xffffffff);
                                canvas.drawRect(0, widget_height - height + 1, widget_width, widget_height, white);
                            }

                            @Override
                            public void setAlpha(int alpha) {

                            }

                            @Override
                            public void setColorFilter(ColorFilter cf) {

                            }

                            @Override
                            public int getOpacity() {
                                return 0;
                            }
                        };
                        drawable.setBounds(0, 0, widget_width, widget_height);
                        widget.setBackgroundDrawable(drawable);
                    }
                }
            }
        }
    }

    /**
     * 切换到上一个Tab，如果没有上一个，则切换到第一个Tab
     */
    public void switchToPreviousTab() {
        if (mPreviousTab == null) {
            mTabHost.setCurrentTab(0);
        } else {
            mTabHost.setCurrentTabByTag(mPreviousTab.tag);
        }
    }

    @Override
    public void onTabChanged(String tabId) {
        LogUtils.d(TAG, "onTabChanged");

        mTabHost.setEnabled(false);
        try {
            synchronized (mTabHost) {
                TabInfo newTab = mTabs.get(tabId);
                if (mLastTab == newTab) {
                    return;
                }
                // 拦截tab切换
                if(null != tabClickListener && tabClickListener.interceptTabChange(mLastTab.tag, tabId)) {
                    mTabHost.setCurrentTabByTag(mLastTab.tag);
                    return;
                }
                FragmentTransaction ft = mFragmentManager.beginTransaction();
                if (mLastTab != null && mLastTab.fragment != null) {
                    ft.hide(mLastTab.fragment);
                }
                if (tabClickListener != null) {
                    tabClickListener.tabChangeBefore(mLastTab.tag, tabId);
                }
                if (newTab != null) {
                    if (newTab.fragment == null) {
                        newTab.fragment = Fragment.instantiate(
                                mActivity, newTab.clss.getName(),
                                newTab.args);
                        ft.add(mContainerId, newTab.fragment, newTab.tag);
                        LogUtils.d(TAG, "add fragment " + newTab.tag);
                    } else {  // has added
                        if (newTab.fragment.isDetached()) {
                            ft.attach(newTab.fragment);
                            LogUtils.d(TAG, "attach fragment " + newTab.tag);
                        }
                        ft.show(newTab.fragment);
                        LogUtils.d(TAG, "show fragment " + newTab.tag);

                    }
                }

                mPreviousTab = mLastTab;
                mLastTab = newTab;
                ft.commitAllowingStateLoss();
                mFragmentManager.executePendingTransactions();

                if (tabClickListener != null) {
                    tabClickListener.tabChange(mPreviousTab.tag, tabId);
                }
            }
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        }
        mTabHost.setEnabled(true);

    }

    public TabClickListener getTabClickListener() {
        return tabClickListener;
    }

    public void setTabClickListener(TabClickListener tabClickListener) {
        this.tabClickListener = tabClickListener;

    }

    /**
     * 为了解决 再次点击当前tab 的问题
     *
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN
                && v.equals(mTabHost.getCurrentTabView())) {
            if (tabClickListener != null) {
                tabClickListener.tabChange(mTabHost.getCurrentTabTag(), mTabHost.getCurrentTabTag());
            }
        }
        return false;
    }

    public void initSpecialListener() {
        for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
            View v = mTabHost.getTabWidget().getChildAt(i);
            v.setOnTouchListener(this);
        }
    }
}
