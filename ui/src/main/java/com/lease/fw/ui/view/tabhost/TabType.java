/**
 *
 */
package com.lease.fw.ui.view.tabhost;


import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lease.fw.ui.R;
import com.lease.fw.ui.view.TipBubble;


/**
 * Tab类型
 */
public class TabType {
    private int index;
    private int labelResid;
    private int iconResid;
    private TabIconSelector tabIconSelector;
    private String name;
    private Bundle bundle;//可选

    private int textColor;
    private TabIndicator indicator;

    public TabType(String name, int index, int labelResid, int iconResid) {
        this.name = name;
        this.index = index;
        this.labelResid = labelResid;
        this.iconResid = iconResid;
    }

    public TabType(String name, int index, int labelResid, TabIconSelector iconSelector) {
        this(name, index, labelResid, 0);
        tabIconSelector = iconSelector;
    }
    public TabType(String name, int index, int labelResid, int iconResid, int textColor) {
        this.name = name;
        this.index = index;
        this.labelResid = labelResid;
        this.iconResid = iconResid;
        this.textColor = textColor;
    }
    public TabType(String name, int index, int labelResid, TabIconSelector iconSelector,int textColor) {
        this(name, index, labelResid, 0, textColor);
        tabIconSelector = iconSelector;
    }

    public void updateTabIconSelector(TabIconSelector iconSelector, boolean bApply) {
        this.iconResid = 0;
        tabIconSelector = iconSelector;
        if(bApply){
            TabIndicator tabIndicator = getIndicator();
            if (getTabIconSelector() != null && tabIndicator.getView() != null) {
                Drawable drawable = iconSelector.getDrawable();
                ImageView image = (ImageView)tabIndicator.getView().findViewById(R.id.tab_widget_icon);
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) image.getLayoutParams();
                layoutParams.setMargins(0, 10, 0, 0);
                image.setLayoutParams(layoutParams);
                image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                image.setImageDrawable(drawable);
                image.invalidate();
            }
        }
    }

    public void updateTabBgDrawable(Drawable drawable){
        TabIndicator tabIndicator = getIndicator();
        View view = tabIndicator.getView();
        if(view != null){
            view.setBackgroundDrawable(drawable);
        }
    }

    public void updateTabTextColor(ColorStateList colorStateList){
        TabIndicator tabIndicator = getIndicator();
        TextView textView = tabIndicator.getTextView();
        if(textView != null){
            textView.setTextColor(colorStateList);
        }
    }



    public static class TabIndicator {
        private View view;
        private TextView textView;
        private TipBubble bubble;

        public TabIndicator(View view, TextView textView, TipBubble tipBubble) {
            this.view = view;
            this.textView = textView;
            this.bubble = tipBubble;
        }

        public View getView() {
            return view;
        }

        public void setView(View view) {
            this.view = view;
        }

        public TextView getTextView() {
            return textView;
        }

        public void setTextView(TextView textView) {
            this.textView = textView;
        }

        public TipBubble getBubble() {
            return bubble;
        }

        public void setBubble(TipBubble bubble) {
            this.bubble = bubble;
        }
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @return the labelResid
     */
    public int getLabelResid() {
        return labelResid;
    }

    /**
     * @return the iconResid
     */
    public int getIconResid() {
        return iconResid;
    }

    public TabIconSelector getTabIconSelector() {
        return tabIconSelector;
    }

    public void setTabIconSelector(TabIconSelector tabIconSelector) {
        this.tabIconSelector = tabIconSelector;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public TabIndicator getIndicator() {
        return indicator;
    }

    public void setIndicator(TabIndicator indicator) {
        this.indicator = indicator;
    }
}
