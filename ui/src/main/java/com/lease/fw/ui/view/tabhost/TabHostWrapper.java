package com.lease.fw.ui.view.tabhost;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.lease.framework.core.DeviceUtils;
import com.lease.framework.core.LogUtils;
import com.lease.framework.core.StringUtils;
import com.lease.fw.ui.R;
import com.lease.fw.ui.view.TipBubble;

import java.util.ArrayList;
import java.util.List;

/**
 * tabHost
 * Created by hxd on 15/7/3.
 */
public class TabHostWrapper {
    private static String sTAG = "TabHostWrapper";
    private TabManager mTabManager;
    private TabHost mTabHost;
    private Context context;
    private TabType[] tabTypeArray;
    private boolean tabWidgetMeasured = false;


    public TabHostWrapper(Context context, TabHost tabHost,
                          FragmentManager fragmentManager) {
        this.context = context;
        mTabHost = tabHost;
        mTabManager = new TabManager(context, mTabHost, android.R.id.tabcontent, fragmentManager);
        mTabHost.setup();

    }

    public void setTabClickListener(TabClickListener tabChangeListener) {
        mTabManager.setTabClickListener(tabChangeListener);
    }

    public void init(TabType[] tabTypeArray, Class[] fragmentArray) {
        final List<Integer> customTabs = new ArrayList<>();
        this.tabTypeArray = tabTypeArray;
        boolean bUseFixedHeight = useFixedHeight(tabTypeArray);
        if (tabTypeArray != null && fragmentArray != null) {
            for (int i = 0; i < tabTypeArray.length; ++i) {
                TabType tabType = tabTypeArray[i];
                Class clazz = fragmentArray[i];
                TabType.TabIndicator indicator = createTabIndicatorView(tabType);
                final View view = indicator.getView();
                view.setLayoutParams(initLayoutParam(bUseFixedHeight));
                mTabManager.addTab(mTabHost.newTabSpec(tabType.getName())
                                .setIndicator(view),
                        clazz, tabType.getBundle());
                tabType.setIndicator(indicator);
                if(!bUseFixedHeight){
                    if(tabType.getIconResid() > 0){//标签使用本地图标，作为其他标签高度的依据
                        ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
                        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                            @Override
                            public boolean onPreDraw() {
                                if (!tabWidgetMeasured) {
                                    int h = view.getMeasuredHeight();
                                    if (customTabs.size() != 0) {
                                        mTabManager.requestTabHeight(customTabs, h);
                                    }
                                    tabWidgetMeasured = true;
                                }
                                return true;
                            }
                        });
                    }
                    if(tabType.getTabIconSelector() != null){//当使用网络图片作为tab标签时，需要强制定义该tab高度
                        customTabs.add(i);
                    }
                }
            }
        }
        mTabManager.initSpecialListener();
    }

    private boolean useFixedHeight(TabType[] tabTypeArray){
        boolean useFixHeight = true;
        for(TabType tabType:tabTypeArray){
            if(tabType.getIconResid() > 0){
                useFixHeight = false;
                break;
            }
        }
        return useFixHeight;
    }

    private LinearLayout.LayoutParams initLayoutParam(boolean bUseFixHeight){
        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                0, bUseFixHeight? DeviceUtils.dip2px(context, 46):ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        lp.setMargins(0, 0, 0, 0);
        return lp;
    }

    private TabType findTabType(String tagName) {
        for (int i = 0; i < tabTypeArray.length; i++) {
            if (StringUtils.equals(tagName, tabTypeArray[i].getName())) {
                return tabTypeArray[i];
            }
        }
        return null;
    }

    public boolean containTab(String tagName) {
        return findTabType(tagName) != null;
    }

    public void setTabString(String tagName, String tabName) {
        TabType tab = findTabType(tagName);
        if (tab != null) {
            tab.getIndicator().getTextView().setText(tabName);
        } else {
//            throw new RuntimeException("tag name illegal");
            LogUtils.e("TabHostWrapper", "tag name illegal!");
        }
    }

    public void setBubbleUnread(String tagName, int unread) {
        TabType tab = findTabType(tagName);
        if (tab != null) {
            tab.getIndicator().getBubble().setUnread((unread));
        } else {
//            throw new RuntimeException("tag name illegal!");
            LogUtils.e("TabHostWrapper", "tag name illegal!");
        }
    }

    public void setBubbleWithoutUnread(String tagName, int unread) {
        TabType tab = findTabType(tagName);
        if (tab != null) {
            tab.getIndicator().getBubble().showWithoutCount(unread);
        } else {
//            throw new RuntimeException("tag name illegal!");
            LogUtils.e("TabHostWrapper", "tag name illegal!");
        }
    }

    public void setBubbleWithoutUnreadNarrow(String tagName, int unread) {
        TabType tab = findTabType(tagName);
        if (tab != null) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tab.getIndicator().getBubble().getLayoutParams();
            lp.setMargins(DeviceUtils.dip2px(context, 3), DeviceUtils.dip2px(context, 2), 0, 0);
            tab.getIndicator().getBubble().setLayoutParams(lp);
            tab.getIndicator().getBubble().showWithoutCount(unread);
        } else {
            LogUtils.e("TabHostWrapper", "tag name illegal!");
        }
    }

    public void setNewUnread(String tagName,int unread){
        TabType tab = findTabType(tagName);
        if (tab != null) {
            tab.getIndicator().getBubble().showNew(unread);
        } else {
            LogUtils.e("TabHostWrapper", "tag name illegal!");
        }
    }

    /**
     * 创建TAB指示器视图
     *
     * @return
     */
    private TabType.TabIndicator createTabIndicatorView(TabType tabtype) {
        View view = LayoutInflater.from(context).inflate(R.layout.widget_tab, null);
        TextView text = (TextView) view.findViewById(R.id.tab_widget_text);
        ImageView image = (ImageView) view.findViewById(R.id.tab_widget_icon);
        TipBubble tipBubble = (TipBubble) view.findViewById(R.id.msg_count);
        if(tabtype.getLabelResid() > 0){
            text.setText(tabtype.getLabelResid());
        }else{
            text.setVisibility(View.GONE);
        }
        if (tabtype.getIconResid() > 0) {
            image.setImageResource(tabtype.getIconResid());
        } else {//通过服务端配置构建icon图标
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) image.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, 0);
            image.setLayoutParams(layoutParams);
            image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            TabIconSelector tabIconSelector = tabtype.getTabIconSelector();
            image.setImageDrawable(tabIconSelector.getDrawable());
            tabIconSelector.setHolder(image);
            image.invalidate();
        }
        //给TAB底部标签设置颜色
        if (tabtype.getTextColor() > 0) {
            ColorStateList csl = context.getResources().getColorStateList(tabtype.getTextColor());
            text.setTextColor(csl);
        }
        return new TabType.TabIndicator(view, text, tipBubble);
    }

    public void setCurrentTabByTag(String name) {
        this.mTabHost.setCurrentTabByTag(name);
    }

    public String getCurrentTabTag() {
        return mTabHost.getCurrentTabTag();
    }

    /**
     * 切换到上一个Tab，如果没有上一个，则切换到第一个Tab
     * {@see TabManager.switchToPreviousTab()}
     */
    public void switchToPreviousTab() {
        if (mTabManager != null) {
            mTabManager.switchToPreviousTab();
        }
    }

    public TabHost getTabHost(){
        return mTabHost;
    }

}
