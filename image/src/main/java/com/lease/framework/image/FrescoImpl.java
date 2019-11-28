package com.lease.framework.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.facebook.common.executors.UiThreadImmediateExecutorService;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

/**
 *
 * Created by hxd on 16/9/22.
 */
public class FrescoImpl extends LoaderProxy {

    @Override
    public void init(Context context) {
        ImagePipelineConfig.Builder builder = ImagePipelineConfig.newBuilder(context);
        Fresco.initialize(context, builder.build());
    }

    @Override
    public void loadImage(LoadImageParams params) {
        if(null == params.baseImageView) {
            return;
        }
        // 防止图片错位
        params.baseImageView.setImageBitmap(null);

        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(params.baseImageView.getResources());
        builder.reset();
        RoundingParams roundingParams = null;
        Context context = params.baseImageView.getContext();
        if (params.roundType == LoadImageParams.ROUND_TYPE_CIRCLES) {
            roundingParams = new RoundingParams();
            roundingParams.setRoundAsCircle(true);
            builder.setRoundingParams(roundingParams);
        } else if (params.roundType == LoadImageParams.ROUND_TYPE_ROUND) {
            roundingParams = new RoundingParams();
            int[] tmp = params.radiusArray;
            if (tmp != null
                    && tmp.length == 4
                    && !(tmp[0] == tmp[1] && tmp[0] == tmp[2] && tmp[0] == tmp[3])) {

                roundingParams.setCornersRadii(
                        dip2px(context, tmp[0]),
                        dip2px(context, tmp[1]),
                        dip2px(context, tmp[2]),
                        dip2px(context, tmp[3])
                );
            }
        }
        if (roundingParams!=null){
            if (params.roundBgColor > 0) {
                roundingParams.setOverlayColor(context.getResources().getColor(params.roundBgColor));
            }
            builder.setRoundingParams(roundingParams);
        }
        if(0 != params.defaultholder) {
            builder.setPlaceholderImage(params.defaultholder);
        }
        GenericDraweeHierarchy hierarchy = builder.setFadeDuration(300)
                .setActualImageScaleType(convertScaleType(params.scaleType))
                .build();
        params.baseImageView.setHierarchy(hierarchy);
        if(TextUtils.isEmpty(params.uri)) {
            return;
        }
        ImageRequestBuilder builderImage = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(params.uri));
        if (params.width > 0 && params.height > 0) {
            builderImage.setResizeOptions(new ResizeOptions(params.width, params.height));
        }
        ImageRequest request = builderImage.setAutoRotateEnabled(true).build();
        DraweeController controller = com.facebook.drawee.backends.pipeline.Fresco
                .newDraweeControllerBuilder()
                .setTapToRetryEnabled(params.enableRetry).setImageRequest(request)
                .setOldController(params.baseImageView.getController())
                .setAutoPlayAnimations(true)
                .setControllerListener(new BaseControllerListener<ImageInfo>(){
                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        super.onFinalImageSet(id, imageInfo, animatable);
                    }

                    @Override
                    public void onFailure(String id, Throwable throwable) {
                        super.onFailure(id, throwable);
                    }
                }).build();
        params.baseImageView.setController(controller);
    }

    private ScalingUtils.ScaleType convertScaleType(ImageView.ScaleType scaleType) {
        if (scaleType == ImageView.ScaleType.CENTER) {
            return ScalingUtils.ScaleType.CENTER;
        }
        if (scaleType == ImageView.ScaleType.FIT_XY) {
            return ScalingUtils.ScaleType.FIT_XY;
        }
        if (scaleType == ImageView.ScaleType.FIT_START) {
            return ScalingUtils.ScaleType.FIT_START;
        }
        if (scaleType == ImageView.ScaleType.FIT_CENTER) {
            return ScalingUtils.ScaleType.FIT_CENTER;
        }
        if (scaleType == ImageView.ScaleType.FIT_END) {
            return ScalingUtils.ScaleType.FIT_END;
        }
        if (scaleType == ImageView.ScaleType.CENTER_INSIDE) {
            return ScalingUtils.ScaleType.CENTER_INSIDE;
        }
        if (scaleType == ImageView.ScaleType.CENTER_CROP) {
            return ScalingUtils.ScaleType.CENTER_CROP;
        }
        return ScalingUtils.ScaleType.FIT_XY;

    }

    @Override
    public void loadImage(final String url, final LoaderCallBack callBack) {
        if (TextUtils.isEmpty(url)) {
            if(null != callBack) {
                callBack.onFailure(Uri.parse(url), new Throwable("url is Empty"));
            }
            return;
        }
        try {
            ImageRequestBuilder requestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url));
            ImageRequest imageRequest = requestBuilder.build();
            DataSource<CloseableReference<CloseableImage>> dataSource = ImagePipelineFactory.getInstance()
                    .getImagePipeline().fetchDecodedImage(imageRequest, null);
            dataSource.subscribe(new BaseBitmapDataSubscriber() {
                                     @Override
                                     public void onNewResultImpl(@Nullable final Bitmap bitmap) {
                                         if (callBack == null)
                                             return;
                                         if (bitmap != null && !bitmap.isRecycled()) {
                                             if (bitmap != null && !bitmap.isRecycled()) {
                                                 new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                     @Override
                                                     public void run() {
                                                         callBack.onSuccess(Uri.parse(url), bitmap);
                                                     }
                                                 });
                                             }
                                         }
                                     }

                                     @Override
                                     public void onCancellation(DataSource<CloseableReference<CloseableImage>> dataSource) {
                                         super.onCancellation(dataSource);
                                         if (callBack == null)
                                             return;
                                         callBack.onCancel(Uri.parse(url));
                                     }

                                     @Override
                                     public void onFailureImpl(DataSource dataSource) {
                                         if (callBack == null)
                                             return;
                                         Throwable throwable = null;
                                         if (dataSource != null) {
                                             throwable = dataSource.getFailureCause();
                                         }
                                         callBack.onFailure(Uri.parse(url), throwable);
                                     }
                                 },
                    UiThreadImmediateExecutorService.getInstance());
        } catch (Exception e) {
            e.printStackTrace();
            if(null != callBack) {
                callBack.onFailure(Uri.parse(url), e);
            }
        }
    }

    private int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
