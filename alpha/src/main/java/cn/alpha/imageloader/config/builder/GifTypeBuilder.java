package cn.alpha.imageloader.config.builder;

import cn.alpha.imageloader.config.Params;

/**
 * BITMAP或者GIF类型
 * Created by Jungle on 2017/10/6.
 */

public class GifTypeBuilder extends DrawableBuilder {
    public GifTypeBuilder(Params params) {
        super(params);
    }

    /**
     * BITMAP
     *
     * @return
     */
    public DrawableBuilder asBitmap() {
        params.asBitmap = true;
        params.asGif = false;
        return this;
    }

    /**
     * GIF
     *
     * @return
     */
    public DrawableBuilder asGif() {
        params.asGif = true;
        params.asBitmap = false;
        return this;
    }
}
