package cn.alpha.imageloader.config;

/**
 * 加载图片配置类
 * Created by Jungle on 2017/6/6.
 */
public class SingleConfig {
    /**
     * 加载图片参数
     */
    private Params params;

    public Params getParams() {
        return params;
    }

    public SingleConfig(Params params) {
        this.params = params;
    }

    /**
     * 执行加载
     */
    public void excute() {
        GlobalConfig.getLoader().request(this);
    }
}
