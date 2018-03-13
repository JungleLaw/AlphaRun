package cn.law.calendar.app;

import android.app.Application;

import cn.alpha.app.Alpha;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by Jungle on 2018/3/1.
 */

public class App extends Application {
    private static Application instance;

    public static Application getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Alpha.init(this);
        Alpha.setDebug(true);
//        File file = new File(getCacheDir() + File.separator + "android.support.v4.app");
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//        RxVolley.setRequestQueue(RequestQueue.newRequestQueue(file));
        Alpha.initHttp(this);
        Alpha.initImageLoader(this);
//        Alpha.initLitePal(this);
//        Alpha.initPermissionChecker(this);

        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Alpha.onTrimMemory(level);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Alpha.onLowMemory();
    }
}
