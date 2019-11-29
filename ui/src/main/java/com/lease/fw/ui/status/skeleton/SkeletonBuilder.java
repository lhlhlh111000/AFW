package com.lease.fw.ui.status.skeleton;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * created time: 2019-11-28
 * author: cqt
 * description: 骨架屏构建者
 */
public class SkeletonBuilder {

    private SkeletonItem[] skeletonItems;

    private int bgColor = Color.WHITE;

    private int skeletonColor = Color.parseColor("#f5f5f9");

    private SkeletonBuilder() {}

    public static SkeletonBuilder with(SkeletonItem ... items) {
        SkeletonBuilder builder = new SkeletonBuilder();
        builder.skeletonItems = items;
        return builder;
    }

    public SkeletonBuilder backgroudColor(int bgColor) {
        this.bgColor = bgColor;
        return this;
    }

    public SkeletonBuilder skeletonColor(int skeletonColor) {
        this.skeletonColor = skeletonColor;
        return this;
    }

    public View build(Context context) {
        ScrollView scrollView = new ScrollView(context);
        LinearLayout linContainer = new LinearLayout(context);
        linContainer.setOrientation(LinearLayout.VERTICAL);
        if(null != skeletonItems) {
            for(SkeletonItem item : skeletonItems) {
                for(int i=0; i<item.getNum(); i++) {
                    linContainer.addView(
                            buildSkeletonItem(context, item.getResId()),
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                }
            }
        }
        scrollView.addView(linContainer, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        scrollView.setBackgroundColor(bgColor);
        return scrollView;
    }

    private View buildSkeletonItem(Context context, int resId) {
        View view = LayoutInflater.from(context).inflate(resId, null);
        setupSkeletonStyle(view);
        view.setBackgroundColor(bgColor);
        return view;
    }

    private void setupSkeletonStyle(View view) {
        if(view instanceof ViewGroup) {
            if(((ViewGroup) view).getChildCount() <= 0) {
                view.setBackgroundColor(skeletonColor);
            }else {
                view.setBackgroundColor(bgColor);
                for(int i=0; i<((ViewGroup) view).getChildCount(); i++) {
                    setupSkeletonStyle(((ViewGroup)view).getChildAt(i));
                }
            }
        }else {
            view.setBackgroundColor(skeletonColor);
        }
    }
}