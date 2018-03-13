package cn.alpha.app;

import android.app.Application;

import cn.alpha.imageloader.ImageLoader;
import cn.alpha.imageloader.impl.glide.GlideImageLoaderImpl;
import cn.alpha.net.http.Http;
import cn.alpha.net.http.stack.okhttp3.OkHttp3Stack;
//import cn.alpha.permission.PermissionChecker;
import cn.alpha.utils.Logger;
import okhttp3.OkHttpClient;

//import com.alpha.db.litepal.LitePal;
//import cn.alpha.imageloader.ImageLoader;

/**
 * Created by Law on 2017/2/22.
 */
public class Alpha {
    private static AppConf conf = new AppConf();

    private Alpha() {
    }

    public static void init(Application application) {
        conf.mApp = application;
        Core.initCore(application);
    }

    //    public static void initPref(Application appContext) {
    //        AnyPref.init(appContext);
    //    }
    public static void initLitePal(Application application) {
//        LitePal.initialize(application);
    }

//    public static void initPermissionChecker(Application application) {
//        PermissionChecker.init(application);
//    }

    public static void initHttp(Application appContext) {
        Http.newRequestQueue(appContext, new OkHttp3Stack(new OkHttpClient()));
        //        Http.newRequestQueue(appContext);
    }

    public static void initImageLoader(Application appContext) {
        //初始化ImageLoader
        ImageLoader.init(appContext, 10, new GlideImageLoaderImpl());
    }

    public static void onTrimMemory(int level) {
        ImageLoader.trimMemory(level);
    }

    public static void onLowMemory() {
        ImageLoader.onLowMemory();
    }

    public static void setDebug(boolean debug) {
        conf.debug = debug;
        Logger.init(conf.debug);
    }

    public static Application getApplication() {
        if (conf.mApp == null) {
            throw new RuntimeException("Firstly,U should init AlphaX at Application.");
        }
        return conf.mApp;
    }

    static class AppConf {
        public boolean debug = false;
        public Application mApp = null;
    }
}
