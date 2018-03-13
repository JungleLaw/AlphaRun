package cn.alpha.net.http;

import android.content.Context;

import java.io.File;

import cn.alpha.net.http.client.HttpParams;
import cn.alpha.net.http.client.ProgressListener;
import cn.alpha.net.http.stack.IHttpStack;
import cn.alpha.net.http.stack.httpurlconnection.HttpConnectStack;
import cn.alpha.net.http.tools.DiskBasedCache;

/**
 * Created by Law on 2017/4/5.
 */

public class Http {
    /**
     * Default on-disk cache directory.
     */
    private static final String DEFAULT_CACHE_DIR = "alpha_http_cache";

    private static RequestQueue mQueue;

    /**
     * 实例化一个RequestQueue，其中start()主要完成相关工作线程的开启，
     * 比如开启缓存线程CacheDispatcher先完成缓存文件的扫描， 还包括开启多个NetworkDispatcher访问网络线程，
     * 该多个网络线程将从 同一个 网络阻塞队列中读取消息
     * <p>
     * 此处可见，start()已经开启，所有我们不用手动的去调用该方法，在start()方法中如果存在工作线程应该首先终止，并重新实例化工作线程并开启
     * 在访问网络很频繁，而又重复调用start()，势必会导致性能的消耗；但是如果在访问网络很少时，调用stop()方法，停止多个线程，然后调用start(),反而又可以提高性能，具体可折中选择
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @param stack   An {@link IHttpStack} to use for the network, or null for default.
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context, IHttpStack stack) {
        File cacheDir = new File(context.getApplicationContext().getCacheDir(), DEFAULT_CACHE_DIR);
        if (stack == null) {
            stack = new HttpConnectStack();
        }
        Network network = new NetworkImpl(stack);
        mQueue = new RequestQueue(new DiskBasedCache(cacheDir), network);
        mQueue.start();
        return mQueue;
    }

    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context) {
        return newRequestQueue(context, null);
    }

    public synchronized static RequestQueue getRequestQueue() {
        if (mQueue == null)
            throw new IllegalStateException("should newRequestQueue");
        return mQueue;
    }

    public static void cancel(Object tag) {
        mQueue.cancelAll(tag);
    }

    public static void cancelAll() {
        mQueue.cancelAll();
    }

    public interface Method {
        int GET = 0;
        int POST = 1;
        int PUT = 2;
        int DELETE = 3;
        int HEAD = 4;
        int OPTIONS = 5;
        int TRACE = 6;
        int PATCH = 7;
    }

    /**
     * 请求方式:FORM表单,或 JSON内容传递
     */
    public interface ContentType {
        int FORM = 0;
        int JSON = 1;
        int STRING = 2;
        int UPLOAD = 3;
        int DOWNLOAD = 4;
    }

    public static void get(Object tag, String url, HttpCallback callback) {
        new Builder().setTag(tag).url(url).httpMethod(Method.GET).callback(callback).doTask();
    }

    public static void get(Object tag, String url, HttpParams params, HttpCallback callback) {
        new Builder().setTag(tag).url(url).httpMethod(Method.GET).params(params).callback(callback).doTask();
    }

    public static void post(Object tag, String url, HttpParams params, HttpCallback callback) {
        new Builder().setTag(tag).url(url).params(params).httpMethod(Method.POST).callback(callback).doTask();
    }

    public static void jsonGet(Object tag, String url, HttpParams params, HttpCallback callback) {
        new Builder().setTag(tag).url(url).params(params).contentType(ContentType.JSON).httpMethod(Method.GET).callback(callback).doTask();
    }

    public static void jsonPost(Object tag, String url, HttpParams params, HttpCallback callback) {
        new Builder().setTag(tag).url(url).params(params).contentType(ContentType.JSON).httpMethod(Method.POST).callback(callback).doTask();
    }

    /**
     * 下载
     *
     * @param storeFilePath    本地存储绝对路径
     * @param url              要下载的文件的url
     * @param progressListener 下载进度回调
     * @param callback         回调
     */
    public static void download(Object tag, String storeFilePath, String url, HttpParams params, ProgressListener progressListener, HttpCallback callback) {
        //        RequestConfig config = new RequestConfig();
        //        config.mUrl = url;
        //        config.mRetryPolicy = new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 20, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        //        DownloadRequest request = new DownloadRequest(storeFilePath, config, callback);
        //        request.setTag(url);
        //        request.setOnProgressListener(progressListener);
        //        new Builder().setRequest(request).doTask();
        new Builder().setTag(tag).url(url).params(params).download(storeFilePath).progressListener(progressListener).callback(callback).doTask();
    }

    /**
     * @param fileName
     * @param file
     * @param url
     * @param progressListener
     * @param callback
     * @return
     */
    public static void upload(Object tag, String fileName, File file, String url, HttpParams params, ProgressListener progressListener, HttpCallback callback) {
        //http请求的回调，内置了很多方法，详细请查看源码
        //包括在异步响应的onSuccessInAsync():注不能做UI操作
        //网络请求成功时的回调onSuccess()
        //网络请求失败时的回调onFailure():例如无网络，服务器异常等
        new Builder().setTag(tag).url(url) //接口地址
                //请求类型，如果不加，默认为 GET 可选项：
                //POST/PUT/DELETE/HEAD/OPTIONS/TRACE/PATCH
                .httpMethod(Method.POST)
                //设置缓存时间: 默认是 get 请求 5 分钟, post 请求不缓存
                .cacheTime(6).upload(fileName, file)
                //内容参数传递形式，如果不加，默认为 FORM 表单提交，可选项 JSON 内容
                //.contentType(ContentType.UPLOAD)
                .params(params) //上文创建的HttpParams请求参数集
                //是否缓存，默认是 get 请求 5 缓存分钟, post 请求不缓存
                .shouldCache(false).progressListener(progressListener) //上传进度
                .callback(callback) //响应回调
                .encoding("UTF-8") //编码格式，默认为utf-8
                .doTask();  //执行请求操作
    }

}
