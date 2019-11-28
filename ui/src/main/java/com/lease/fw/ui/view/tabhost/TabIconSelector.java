package com.lease.fw.ui.view.tabhost;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.widget.ImageView;

/**
 * Created by linchaoli on 2015/11/17.
 */
public class TabIconSelector {

    private StateListDrawable stateListDrawable = new StateListDrawable();

    private ImageView mHolder;

    public TabIconSelector(Drawable drawableNormal, Drawable drawableSelect) {
        stateListDrawable.addState(new int[]{-android.R.attr.state_selected}, drawableNormal);
        stateListDrawable.addState(new int[]{android.R.attr.state_selected}, drawableSelect);
    }

    public StateListDrawable getDrawable() {
        return stateListDrawable;
    }

    public void setHolder(ImageView imageView) {
        mHolder = imageView;
    }
}