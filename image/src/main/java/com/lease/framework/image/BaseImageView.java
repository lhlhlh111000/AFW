package com.lease.framework.image;

import android.content.Context;
import android.util.AttributeSet;

import com.facebook.drawee.view.SimpleDraweeView;

/**
 *
 * Created by hxd on 16/9/22.
 */
public class BaseImageView extends SimpleDraweeView {
    public BaseImageView(Context context) {
        super(context);
    }
    public BaseImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
