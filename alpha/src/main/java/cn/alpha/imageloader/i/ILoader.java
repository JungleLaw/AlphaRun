package cn.alpha.imageloader.i;

import android.content.Context;
import android.view.View;

import java.io.File;

import cn.alpha.imageloader.config.SingleConfig;

/**
 * 图片加载及其他功能接口
 * Created by Jungle on 2017/6/6.
 */
public interface ILoader {
    /**
     * 初始化
     * @param context 上下文
     * @param cacheSizeInM 缓存大小（M）
     */
    void init(Context context, int cacheSizeInM);

    /**
     * 请求加载图片
     * @param config 配置
     */
    void request(SingleConfig config);

    /**
     * 暂停加载
     */
    void pause();

    /**
     * 恢复加载
     */
    void resume();

    /**
     * 清理硬盘缓存
     */
    void clearDiskCache();

    /**
     * 清理内存缓存
     */
    void clearMomoryCache();

    /**
     * 获取缓存大小
     * @return 硬盘缓存大小
     */
    long getCacheSize();

    /**
     * 清理目标url生成的缓存
     * @param url
     */
    void clearCacheByUrl(String url);

    /**
     * 清理目标view的内存缓存
     * @param config
     * @param view
     */
    void clearMomoryCache(SingleConfig config, View view);

    /**
     * 清除目标URL生成的缓存
     * @param url
     */
    void clearMomoryCache(String url);

    /**
     * 获取目标url硬盘缓存
     * @param url
     * @return
     */
    File getFileFromDiskCache(String url);

    /**
     * 异步获取目标url硬盘缓存
     * @param url
     * @param getter
     */
    void getFileFromDiskCache(String url, FileGetter getter);

    /**
     * 判断目标URL是否被缓存
     * @param url
     * @return
     */
    boolean isCached(String url);

    /**
     * 放在Application 中的trimMemory中调用
     * @param level
     */
    void trimMemory(int level);

    /**
     * 低内存时调用，放在Application 中的onlowmemory中调用
     */
    void onLowMemory();
}
