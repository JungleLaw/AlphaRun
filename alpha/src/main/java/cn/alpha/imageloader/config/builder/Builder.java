package cn.alpha.imageloader.config.builder;

import android.view.View;

import cn.alpha.imageloader.MyUtil;
import cn.alpha.imageloader.config.Params;
import cn.alpha.imageloader.config.SingleConfig;


/**
 * 执行加载图片Builder
 * Created by Jungle on 2017/6/6.
 */
public class Builder {
    /**
     * 图片加载参数
     */
    public Params params;

    private Builder() {
    }

    public Builder(Params params) {
        this.params = params;
    }

    /**
     * 加载的View(ImageView类)
     * @param target
     */
    public void into(View target) {
        params.target = target;
        new SingleConfig(params).excute();
    }

    /**
     * 异步加载Bitmap
     * @param listener
     */
    public void setBitmapListener(Params.BitmapListener listener) {
        params.bitmapListener = MyUtil.getBitmapListenerProxy(listener);
        new SingleConfig(params).excute();
    }
}
