package cn.alpha.net.http;

import cn.alpha.net.http.client.ProgressListener;

/**
 * 分发器，将异步线程中的结果响应到UI线程中
 * Created by Law on 2017/4/5.
 */

public interface Delivery {

    /**
     * 分发响应结果
     *
     * @param request
     * @param response
     */
    void postResponse(Request<?> request, Response<?> response);

    /**
     * 分发Failure事件
     *
     * @param request 请求
     * @param error   异常原因
     */
    void postError(Request<?> request, HttpError error);

    void postResponse(Request<?> request, Response<?> response, Runnable runnable);

    /**
     * 分发当Http请求开始时的事件
     */
    void postStartHttp(Request<?> request);

    /**
     * 进度
     *
     * @param transferredBytes 进度
     * @param totalSize        总量
     */
    void postProgress(ProgressListener listener, long transferredBytes, long totalSize);

}
