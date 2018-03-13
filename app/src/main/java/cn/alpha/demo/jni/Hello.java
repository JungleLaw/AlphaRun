package cn.alpha.demo.jni;

/**
 * Created by Jungle on 2017/9/30.
 */

public class Hello {
    static {
        System.loadLibrary("hello");
    }

    public static native String getServerUrl();

    public static native String getIMGUrl();
}
