package cn.alpha.imageloader;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import cn.alpha.imageloader.config.GlobalConfig;
import cn.alpha.imageloader.config.builder.ConfigBuilder;
import cn.alpha.imageloader.config.builder.RequestBuilder;
import cn.alpha.imageloader.i.ILoader;

/**
 * 图片加载器
 * Created by Jungle on 2017/10/6.
 */

public class ImageLoader {
    /**
     * 上下文
     */
    public static Context context;

    /**
     * 主线程Handler
     * @return
     */
    static Handler getHandler() {
        if(handler==null){
            handler = new Handler(Looper.getMainLooper());
        }
        return handler;
    }
    /**
     * 主线程Handler
     * @return
     */
    static Handler handler;

    /**
     * 初始化加载器
     * @param context 上下文
     * @param cacheSizeInM 缓存大小
     * @param imageLoader ILoader实现类
     */
    public static void init(final Context context, int cacheSizeInM,ILoader imageLoader){
        ImageLoader.context = context;
        GlobalConfig.init(context,cacheSizeInM,imageLoader);
        handler = new Handler(Looper.getMainLooper());
        //imageLoader.init();
    }

    /**
     * @return ILoader实现的实例
     */
    public static ILoader getActualLoader(){
        return  GlobalConfig.getLoader();
    }

    /**
     * 加载普通图片
     * @param context
     * @return
     */
    public static RequestBuilder with(Context context){
        return new ConfigBuilder().with(context);
    }

    /**
     * 低内存调用
     */
    public static void onLowMemory() {
        GlobalConfig.getLoader().onLowMemory();
    }

    /**
     * 整理内存调用
     * @param level
     */
    public static void trimMemory(int level){
        GlobalConfig.getLoader().trimMemory(level);
    }

    /**
     *  清理缓存调用
     */
    public static void  clearAllMemoryCaches(){
        GlobalConfig.getLoader().onLowMemory();
    }
}
