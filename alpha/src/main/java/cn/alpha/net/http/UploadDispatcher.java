package cn.alpha.net.http;

import android.net.TrafficStats;
import android.os.Process;

import java.util.concurrent.BlockingQueue;

import cn.alpha.utils.Logger;

/**
 * Created by Law on 2017/4/9.
 */

public class UploadDispatcher extends Thread {
    private final BlockingQueue<Request<?>> mUploadQueue; // 用于执行网络请求的工作队列
    private final Network mNetwork; // 网络请求执行器
    private final Cache mCache; // 缓存器
    private final Delivery mDelivery; // 分发器
    private volatile boolean mQuit = false;

    /**
     * 创建分发器(必须手动调用star()方法启动分发任务)
     *
     * @param uploadQueue 正在执行的队列
     */
    public UploadDispatcher(BlockingQueue<Request<?>> uploadQueue, Network network, Cache cache, Delivery delivery) {
        mUploadQueue = uploadQueue;
        mNetwork = network;
        mCache = cache;
        mDelivery = delivery;
        //        mPoster = RxBus.getDefault();
    }

    /**
     * 强制退出
     */
    public void quit() {
        mQuit = true;
        interrupt();
    }

    /**
     * 工作在阻塞态
     */
    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        int stateCode = -1;
        while (true) {
            Request<?> request;
            try {
                request = mUploadQueue.take();
            } catch (InterruptedException e) {
                if (mQuit) {
                    return;
                } else {
                    continue;
                }
            }
            try {
                if (request.isCanceled()) {
                    request.finish("任务已经取消");
                    continue;
                }
                mDelivery.postStartHttp(request);
                addTrafficStatsTag(request);
                NetworkResponse networkResponse = mNetwork.performRequest(request);
                stateCode = networkResponse.statusCode;
                // 如果这个响应已经被分发，则不会再次分发
                if (networkResponse.notModified && request.hasHadResponseDelivered()) {
                    request.finish("已经分发过本响应");
                    continue;
                }
                Response<?> response = request.parseNetworkResponse(networkResponse);

                if (request.shouldCache() && response.cacheEntry != null) {
                    mCache.put(request.getCacheKey(), response.cacheEntry);
                }
                request.markDelivered();
                //执行异步响应
                if (networkResponse.data != null) {
                    if (request.getCallback() != null) {
                        request.getCallback().onSuccessInAsync(networkResponse.data);
                    }
                }
                mDelivery.postResponse(request, response);
            } catch (HttpError error) {
                parseAndDeliverNetworkError(request, error, stateCode);
            } catch (Exception e) {
                Logger.d(String.format("Unhandled exception %s", e.getMessage()));
                parseAndDeliverNetworkError(request, new HttpError(e), stateCode);
            }
        }
    }

    private void addTrafficStatsTag(Request<?> request) {
        TrafficStats.setThreadStatsTag(request.getTrafficStatsTag());
    }

    private void parseAndDeliverNetworkError(Request<?> request, HttpError error, int stateCode) {
        error = request.parseNetworkError(error);
        mDelivery.postError(request, error);
    }
}
