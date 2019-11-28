package com.lease.framework.image;

import android.widget.ImageView;

/**
 * Created by hxd on 16/9/22.
 */

public class LoadImageParams {

    String uri;
    BaseImageView baseImageView;
    /**
     * 默认holder
     */
    int defaultholder;
    /**
     * 失败holder
     */
    int failholder;
    int retryholder;
    int bgholder;
    int roundBgColor;
    int width;
    int height;

    /**
     * Sets the corner radii of all the corners.
     *
     * @param topLeft top left corner radius.
     * @param topRight top right corner radius
     * @param bottomRight bototm right corner radius.
     * @param bottomLeft bottom left corner radius.
     */
    int[] radiusArray;
    ImageView.ScaleType scaleType;

    public static final int ROUND_TYPE_NONE = 0;//无
    public static final int ROUND_TYPE_CIRCLES = 1;//圆形图片
    public static final int ROUND_TYPE_ROUND = 2;//圆角图片

    int roundType = ROUND_TYPE_NONE;
    Object tag;
    public boolean enableRetry;

    private LoadImageParams(Builder builder) {
        uri = builder.uri;
        baseImageView = builder.baseImageView;
        defaultholder = builder.defaultholder;
        failholder = builder.failholder;
        retryholder = builder.retryholder;
        bgholder = builder.bgholder;
        roundBgColor = builder.roundBgColor;
        width = builder.width;
        height = builder.height;
        radiusArray = builder.radiusArray;
        scaleType = builder.scaleType;
        roundType = builder.roundType;
        tag = builder.tag;
        enableRetry = builder.enableRetry;

    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public static final class Builder {
        private int defaultholder;
        private int failholder;
        private int retryholder;
        private int bgholder;
        private int roundBgColor;
        private int width;
        private int height;
        private int[] radiusArray;
        private ImageView.ScaleType scaleType;
        private boolean round;
        private int loadMode;
        private Object tag;
        private String uri;
        private BaseImageView baseImageView;
        private int roundType;
        private boolean enableRetry;

        private Builder() {
        }

        public Builder defaultholder(int val) {
            defaultholder = val;
            return this;
        }

        public Builder failholder(int val) {
            failholder = val;
            return this;
        }

        public Builder retryholder(int val) {
            retryholder = val;
            return this;
        }

        public Builder bgholder(int val) {
            bgholder = val;
            return this;
        }

        public Builder roundBgColor(int val) {
            roundBgColor = val;
            return this;
        }

        public Builder width(int val) {
            width = val;
            return this;
        }

        public Builder height(int val) {
            height = val;
            return this;
        }

        public Builder radiusArray(int[] val) {
            radiusArray = val;
            return this;
        }

        public Builder scaleType(ImageView.ScaleType val) {
            scaleType = val;
            return this;
        }

        public Builder round(boolean val) {
            round = val;
            return this;
        }

        public Builder loadMode(int val) {
            loadMode = val;
            return this;
        }

        public Builder tag(Object val) {
            tag = val;
            return this;
        }

        public LoadImageParams build() {
            return new LoadImageParams(this);
        }

        public Builder uri(String val) {
            uri = val;
            return this;
        }

        public Builder baseImageView(BaseImageView val) {
            baseImageView = val;
            return this;
        }

        public Builder roundType(int val) {
            roundType = val;
            return this;
        }

        public Builder enableRetry(boolean val) {
            enableRetry = val;
            return this;
        }
    }
}
