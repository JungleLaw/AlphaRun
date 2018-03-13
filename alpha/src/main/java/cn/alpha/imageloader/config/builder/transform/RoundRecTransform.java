package cn.alpha.imageloader.config.builder.transform;

import cn.alpha.imageloader.config.Transform;

/**
 * Created by Jungle on 2017/8/10.
 */

public class RoundRecTransform extends Transform {
    public static final int DEFAULT_RADIUS = 8;
    private int radius = DEFAULT_RADIUS;

    public RoundRecTransform() {
    }

    public RoundRecTransform(int radius) {
        this.radius = radius;
    }

    public int getRadius() {
        return radius;
    }
}
