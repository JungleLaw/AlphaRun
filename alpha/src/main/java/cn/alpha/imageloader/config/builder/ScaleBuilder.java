package cn.alpha.imageloader.config.builder;

import cn.alpha.imageloader.config.Params;
import cn.alpha.imageloader.config.ScaleMode;

/**
 * SCALETYPE建造类
 * Created by Jungle on 2017/10/6.
 */

public class ScaleBuilder extends GifTypeBuilder {
    public ScaleBuilder(Params params) {
        super(params);
    }

    /**
     * 适中
     * @return
     */
    public GifTypeBuilder fitCenter() {
        params.mode = ScaleMode.FIT_CENTER;
        return this;
    }

    /**
     * 居中裁剪
     * @return
     */
    public GifTypeBuilder centerCrop() {
        params.mode = ScaleMode.CENTER_CROP;
        return this;
    }
}
