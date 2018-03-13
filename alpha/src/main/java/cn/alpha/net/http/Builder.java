package cn.alpha.net.http;

import android.text.TextUtils;

import java.io.File;

import cn.alpha.net.http.client.DownloadRequest;
import cn.alpha.net.http.client.FormRequest;
import cn.alpha.net.http.client.HttpParams;
import cn.alpha.net.http.client.JSONRequest;
import cn.alpha.net.http.client.ProgressListener;
import cn.alpha.net.http.client.RequestConfig;
import cn.alpha.net.http.client.StringRequest;
import cn.alpha.net.http.client.UploadRequest;
import cn.alpha.utils.Logger;

/**
 * Created by Law on 2017/4/5.
 */

class Builder {
    private HttpParams params;
    private int contentType;
    private HttpCallback callback;
    private Request<?> request;
    private ProgressListener progressListener;
    private RequestConfig httpConfig = new RequestConfig();
    private String mFileName;
    private File mUploadFile;
    private String mStoreFilePath;

    /**
     * Http请求参数
     */
    public Builder params(HttpParams params) {
        this.params = params;
        return this;
    }

    /**
     * 参数的类型:FORM表单,或 JSON内容传递
     */
    public Builder contentType(int contentType) {
        this.contentType = contentType;
        return this;
    }

    /**
     * 请求回调,不需要可以为空
     */
    public Builder callback(HttpCallback callback) {
        this.callback = callback;
        return this;
    }

    /**
     * HttpRequest
     */
    public Builder setRequest(Request<?> request) {
        this.request = request;
        return this;
    }

    /**
     * 每个request可以设置一个标志,用于在cancel()时找到
     */
    public Builder setTag(Object tag) {
        this.httpConfig.mTag = tag;
        return this;
    }


    /**
     * HttpRequest的配置器
     */
    public Builder httpConfig(RequestConfig httpConfig) {
        this.httpConfig = httpConfig;
        return this;
    }

    /**
     * 请求超时时间,如果不设置则使用重连策略的超时时间,默认3000ms
     */
    public Builder timeout(int timeout) {
        this.httpConfig.mTimeout = timeout;
        return this;
    }

    /**
     * 上传进度回调
     *
     * @param listener 进度监听器
     */
    public Builder progressListener(ProgressListener listener) {
        this.progressListener = listener;
        return this;
    }

    /**
     * 为了更真实的模拟网络,如果读取缓存,延迟一段时间再返回缓存内容
     */
    public Builder delayTime(int delayTime) {
        this.httpConfig.mDelayTime = delayTime;
        return this;
    }

    /**
     * 缓存有效时间,单位分钟
     */
    public Builder cacheTime(int cacheTime) {
        this.httpConfig.mCacheTime = cacheTime;
        return this;
    }

    /**
     * 是否使用服务器控制的缓存有效期(如果使用服务器端的,则无视#cacheTime())
     */
    public Builder useServerControl(boolean useServerControl) {
        this.httpConfig.mUseServerControl = useServerControl;
        return this;
    }

    /**
     * 查看RequestConfig$Method
     * GET/POST/PUT/DELETE/HEAD/OPTIONS/TRACE/PATCH
     */
    public Builder httpMethod(int httpMethod) {
        this.httpConfig.mMethod = httpMethod;
        if (httpMethod == Http.Method.POST) {
            this.httpConfig.mShouldCache = false;
        }
        return this;
    }

    /**
     * 是否启用缓存
     */
    public Builder shouldCache(boolean shouldCache) {
        this.httpConfig.mShouldCache = shouldCache;
        return this;
    }

    /**
     * 网络请求接口url
     */
    public Builder url(String url) {
        this.httpConfig.mUrl = url;
        return this;
    }

    /**
     * 重连策略,不传则使用默认重连策略
     */
    public Builder retryPolicy(RetryPolicy retryPolicy) {
        this.httpConfig.mRetryPolicy = retryPolicy;
        return this;
    }

    /**
     * 编码,默认UTF-8
     */
    public Builder encoding(String encoding) {
        this.httpConfig.mEncoding = encoding;
        return this;
    }


    public Builder download(String storeFilePath) {
        this.mStoreFilePath = storeFilePath;
        this.contentType = Http.ContentType.DOWNLOAD;
        return this;
    }

    public Builder upload(String fileName, File file) {
        this.mFileName = fileName;
        this.mUploadFile = file;
        this.contentType = Http.ContentType.UPLOAD;
        return this;
    }

    private Builder build() {
        if (request == null) {
            if (params == null) {
                params = new HttpParams();
            } else {
                if (httpConfig.mMethod == Http.Method.GET)
                    httpConfig.mUrl += params.getUrlParams();
            }

            if (httpConfig.mShouldCache == null) {
                //默认情况只对get请求做缓存
                if (httpConfig.mMethod == Http.Method.GET) {
                    httpConfig.mShouldCache = Boolean.TRUE;
                } else {
                    httpConfig.mShouldCache = Boolean.FALSE;
                }
            }

            if (contentType == Http.ContentType.JSON) {
                request = new JSONRequest(httpConfig, params, callback);
            } else if (contentType == Http.ContentType.STRING) {
                request = new StringRequest(httpConfig, params, callback);
            } else if (contentType == Http.ContentType.FORM) {
                request = new FormRequest(httpConfig, params, callback);
            } else if (contentType == Http.ContentType.DOWNLOAD) {
                Logger.i("new DownloadRequest");
                request = new DownloadRequest(mStoreFilePath, httpConfig, callback);
            } else if (contentType == Http.ContentType.UPLOAD) {
                Logger.i("new UploadRequest");
                contentType = Http.ContentType.FORM;
                request = new UploadRequest(mFileName, mUploadFile, httpConfig, params, callback);
            }

            request.setTag(httpConfig.mTag);
            request.setOnProgressListener(progressListener);

            if (TextUtils.isEmpty(httpConfig.mUrl)) {
                throw new RuntimeException("Request url is empty");
            }
        }
        if (callback != null) {
            callback.onPreStart();
        }
        return this;
    }

    //    /**
    //     * 执行请求任务,并返回一个RxJava的Observable类型
    //     */
    //    public Observable<Result> getResult() {
    //        doTask();
    //        return httpConfig.mSubject;
    //    }

    /**
     * 执行请求任务
     */
    public void doTask() {
        build();
        Http.getRequestQueue().add(request);
    }
}
