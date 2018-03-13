package cn.alpha.imageloader.config.builder;

import android.support.annotation.DrawableRes;
import android.support.annotation.FloatRange;

import cn.alpha.imageloader.config.Params;
import cn.alpha.imageloader.config.Transform;

/**
 * Drawable建造者
 * Created by Jungle on 2017/10/6.
 */

public class DrawableBuilder extends Builder {
    public DrawableBuilder(Params params) {
        super(params);
    }

    /**
     * 占位图资源ID
     *
     * @param placeHolderResId
     * @return
     */
    public DrawableBuilder placeHolder(@DrawableRes int placeHolderResId) {
        params.placeHolderResId = placeHolderResId;
        return this;
    }

    /**
     * 加载失败占位图资源ID
     *
     * @param errorResId
     * @return
     */
    public DrawableBuilder error(@DrawableRes int errorResId) {
        params.errorResId = errorResId;
        return this;
    }

    public DrawableBuilder override(int width, int height) {
        params.width = width;
        params.height = height;
        return this;
    }

    /**
     * 缩略图质量0.0~1.0
     *
     * @param thumbnail
     * @return
     */
    public DrawableBuilder thumb(@FloatRange(from = 0.0, to = 1.0) float thumbnail) {
        params.thumbnail = thumbnail;
        return this;
    }

    public DrawableBuilder transform(Transform transform) {
        params.transform = transform;
        return this;
    }
}
