package cn.alpha.imageloader.impl.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.MemoryCategory;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;

import cn.alpha.imageloader.ImageLoader;
import cn.alpha.imageloader.MyUtil;
import cn.alpha.imageloader.config.GlobalConfig;
import cn.alpha.imageloader.config.ScaleMode;
import cn.alpha.imageloader.config.SingleConfig;
import cn.alpha.imageloader.config.builder.transform.CircleTransform;
import cn.alpha.imageloader.config.builder.transform.RoundRecTransform;
import cn.alpha.imageloader.i.FileGetter;
import cn.alpha.imageloader.i.ILoader;
import cn.alpha.imageloader.impl.transform.RoundedCornersTransformation;


/**
 * Created by Jungle on 2017/6/6.
 */

public class GlideImageLoaderImpl implements ILoader {
    /**
     * 初始化
     *
     * @param context      上下文
     * @param cacheSizeInM 缓存大小（M）
     */
    @Override
    public void init(Context context, int cacheSizeInM) {
        GlideApp.get(context).setMemoryCategory(MemoryCategory.NORMAL);
    }

    /**
     * 请求加载图片
     *
     * @param config 配置
     */
    @Override
    public void request(final SingleConfig config) {
        if (config.getParams().getBitmapListener() != null) {
            RequestManager requestManager = GlideApp.with(config.getParams().context);
            RequestBuilder requestBuilder = getRequest(config, requestManager.asBitmap());
            if (config.getParams().getWidth() > 0 && config.getParams().getHeight() > 0) {
                requestBuilder.apply(RequestOptions.overrideOf(config.getParams().getWidth(), config.getParams().getHeight()));
            }

            SimpleTarget target = new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                    // do something with the bitmap
                    // for demonstration purposes, let's just set it to an ImageView
                    // BitmapPool mBitmapPool = Glide.get(BigLoader.context).getBitmapPool();
                    //bitmap = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight())

                    config.getParams().getBitmapListener().onSuccess(bitmap);
                }

                @Override
                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                    super.onLoadFailed(errorDrawable);
                    config.getParams().getBitmapListener().onFail();
                }
            };
            // setShapeModeAndBlur(config, request);
            requestBuilder.into(target);

        } else {
            RequestManager requestManager = GlideApp.with(config.getParams().context);
//            RequestManager requestManager = null;
            RequestBuilder requestBuilder = null;
            if (config.getParams().asBitmap) {
//                requestBuilder = Glide.with(config.getParams().context).asBitmap();
                requestBuilder = getRequest(config, requestManager.asBitmap());
            } else if (config.getParams().asGif) {
//                request.asGif().into((ImageView) config.getParams().getTarget());
//                requestBuilder = Glide.with(requestManager.asGif();
                requestBuilder = getRequest(config, requestManager.asGif());
            } else {
                requestBuilder = getDrawableTypeRequest(config, requestManager);
            }

            if (requestBuilder == null) {
                return;
            }

            int scaleMode = config.getParams().getMode();
            switch (scaleMode) {
                case ScaleMode.FIT_CENTER:
                    requestBuilder.apply(RequestOptions.fitCenterTransform());
                    break;
                case ScaleMode.CENTER_INSIDE:
                    requestBuilder.apply(RequestOptions.centerInsideTransform());
                    break;
                case ScaleMode.CENTER_CROP:
                    requestBuilder.apply(RequestOptions.centerCropTransform());
                    break;
                case ScaleMode.FIT_XY:
                case ScaleMode.FIT_END:
                case ScaleMode.FOCUS_CROP:
                case ScaleMode.CENTER:
                case ScaleMode.FIT_START:
                    requestBuilder.apply(RequestOptions.centerCropTransform());
                    break;
                default:
                    requestBuilder.apply(RequestOptions.noTransformation());
                    break;
            }

            if (config.getParams().transform != null) {
                if (config.getParams().transform instanceof CircleTransform) {
                    requestBuilder.apply(RequestOptions.circleCropTransform());
                } else if (config.getParams().transform instanceof RoundRecTransform) {
                    requestBuilder.apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(config.getParams().context, ((RoundRecTransform) config.getParams().transform).getRadius(), 0)));
                }
            }

            if (config.getParams().getWidth() > 0 && config.getParams().getHeight() > 0) {
//                request.override(config.getParams().getWidth(), config.getParams().getHeight());
                requestBuilder.apply(RequestOptions.overrideOf(config.getParams().getWidth(), config.getParams().getHeight()));
            }

            if (config.getParams().getPlaceHolderResId() > 0) {
//                request.placeholder(config.getParams().getPlaceHolderResId());
                requestBuilder.apply(RequestOptions.placeholderOf(config.getParams().getPlaceHolderResId()));
            }

            if (config.getParams().getThumbnail() > 0) {
                requestBuilder.thumbnail(config.getParams().getThumbnail());
            }

            if (config.getParams().getErrorResId() > 0) {
//                request.error(config.getParams().getErrorResId());
                requestBuilder.apply(RequestOptions.errorOf(config.getParams().getErrorResId()));
            }

            if (config.getParams().getTarget() instanceof ImageView) {
                requestBuilder.into((ImageView) config.getParams().getTarget());
            }
        }
    }

    @Nullable
    private RequestBuilder getDrawableTypeRequest(SingleConfig config, RequestManager requestManager) {
        RequestBuilder<Drawable> request = null;
        if (!TextUtils.isEmpty(config.getParams().getUrl())) {
            request = requestManager.load(config.getParams().getUrl());
            //request.diskCacheStrategy(DiskCacheStrategy.SOURCE);//只缓存原图
        } else if (config.getParams().getFile() != null) {
            request = requestManager.load(config.getParams().getFile());
        } else if (config.getParams().getUri() != null && !TextUtils.isEmpty(config.getParams().getUri().getPath())) {
            request = requestManager.load(config.getParams().getUri());
        } else if (config.getParams().getResId() > 0) {
            request = requestManager.load(config.getParams().getResId());
        }
        return request;
    }

    private RequestBuilder getRequest(SingleConfig config, RequestBuilder builder) {
        RequestBuilder request = null;
        if (!TextUtils.isEmpty(config.getParams().getUrl())) {
            request = builder.load(config.getParams().getUrl());
            //request.diskCacheStrategy(DiskCacheStrategy.SOURCE);//只缓存原图
        } else if (config.getParams().getFile() != null) {
            request = builder.load(config.getParams().getFile());
        } else if (config.getParams().getUri() != null && !TextUtils.isEmpty(config.getParams().getUri().getPath())) {
            request = builder.load(config.getParams().getUri());
        } else if (config.getParams().getResId() > 0) {
            request = builder.load(config.getParams().getResId());
        }
        return request;
    }

    /**
     * 暂停加载
     */
    @Override
    public void pause() {
        GlideApp.with(GlobalConfig.mContext).pauseRequestsRecursive();
    }

    /**
     * 恢复加载
     */
    @Override
    public void resume() {
        GlideApp.with(GlobalConfig.mContext).resumeRequestsRecursive();
    }

    /**
     * 清理硬盘缓存
     */
    @Override
    public void clearDiskCache() {
        GlideApp.get(ImageLoader.context).clearDiskCache();
    }

    /**
     * 清理内存缓存
     */
    @Override
    public void clearMomoryCache() {
        GlideApp.get(ImageLoader.context).clearMemory();
    }

    /**
     * 获取缓存大小
     *
     * @return 硬盘缓存大小
     */
    @Override
    public long getCacheSize() {
        return MyUtil.getCacheSize();
    }

    /**
     * 清理目标url生成的缓存
     *
     * @param url
     */
    @Override
    public void clearCacheByUrl(String url) {

    }

    /**
     * 清理目标view的内存缓存
     *
     * @param config
     * @param view
     */
    @Override
    public void clearMomoryCache(SingleConfig config, View view) {
        GlideApp.with(config.getParams().context).clear(view);
    }

    /**
     * 清除目标URL生成的缓存
     *
     * @param url
     */
    @Override
    public void clearMomoryCache(String url) {

    }

    /**
     * 获取目标url硬盘缓存
     *
     * @param url
     * @return
     */
    @Override
    public File getFileFromDiskCache(String url) {
        return null;
    }

    /**
     * 异步获取目标url硬盘缓存
     *
     * @param url
     * @param getter
     */
    @Override
    public void getFileFromDiskCache(String url, final FileGetter getter) {
        GlideApp.with(ImageLoader.context)
                .load(url)
                .downloadOnly(new SimpleTarget<File>() {
                    @Override
                    public void onResourceReady(File resource, Transition<? super File> transition) {
                        if (resource.exists() && resource.isFile()) {//&& resource.length() > 70
                            getter.onSuccess(resource);
                        } else {
                            getter.onFail();
                        }
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        getter.onFail();
                    }
                });
    }

    /**
     * 判断目标URL是否被缓存
     *
     * @param url
     * @return
     */
    @Override
    public boolean isCached(String url) {
        return false;
    }

    /**
     * 放在Application 中的trimMemory中调用
     *
     * @param level
     */
    @Override
    public void trimMemory(int level) {
        GlideApp.with(GlobalConfig.mContext).onTrimMemory(level);
    }

    /**
     * 低内存时调用，放在Application 中的onlowmemory中调用
     */
    @Override
    public void onLowMemory() {
        GlideApp.with(GlobalConfig.mContext).onLowMemory();
    }
}
