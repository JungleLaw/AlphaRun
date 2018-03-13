package cn.alpha.app;

import android.app.Application;
import android.content.Context;

import java.lang.reflect.Field;

import cn.alpha.instrumentation.InstrumentationIoc;
import cn.alpha.reflect.KernelObject;
import cn.alpha.reflect.KernelReflect;
import cn.alpha.utils.Logger;

/**
 * Created by Law on 2016/12/25.
 */

class Core {
    private static Core mCore;
    /**
     * aop切入点
     **/
    private static InstrumentationIoc instrumentationIoc;
    private static Application mApplication;

    private Core() {
    }

    private Core(Application application) {
        this.mApplication = application;
    }

    private static Core getInstance(Application application) {
        if (mCore == null) {
            synchronized (Core.class) {
                if (mCore == null) {
                    mCore = new Core(application);
                }
            }
        }
        return mCore;
    }

    public static void initCore(Application application) {
        getInstance(application);
        long time = System.currentTimeMillis();
        // --------------------------------------------------------------------------------------------------
        // 反射获取mMainThread
        // getBaseContext()返回的是ContextImpl对象 ContextImpl中包含ActivityThread
        // mMainThread这个对象
        Context object = application.getBaseContext();
        Object mainThread = KernelObject.declaredGet(object, "mMainThread");
        // 反射获取mInstrumentation的对象
        Field instrumentationField = KernelReflect.declaredField(mainThread.getClass(), "mInstrumentation");
        instrumentationIoc = new InstrumentationIoc();
        // 自定义一个Instrumentation的子类 并把所有的值给copy进去
        // --------------------------------------------------------------------------------------------------
        // 是否打开兼容模式
        // KernelObject.copy(KernelReflect.get(mainThread,
        // instrumentationField), instrumentationIoc);
        // 再把替换过的Instrumentation重新放进去
        KernelReflect.set(mainThread, instrumentationField, instrumentationIoc);
        // --------------------------------------------------------------------------------------------------
        Logger.d("appliaction 加载时间为:" + (System.currentTimeMillis() - time));
        // initDatabase(application);
    }

}
