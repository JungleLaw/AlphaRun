package cn.alpha.net.http;

import android.net.TrafficStats;
import android.os.Process;

import java.util.concurrent.BlockingQueue;

import cn.alpha.utils.Logger;

/**
 * 网络请求任务的调度器，负责不停的从RequestQueue中取Request并交给NetWork执行，
 * 执行完成后分发执行结果到UI线程的回调并缓存结果到缓存器
 * Created by Law on 2017/4/5.
 */

public class NetworkDispatcher extends Thread {
    private final BlockingQueue<Request<?>> mQueue; // 正在发生请求的队列
    private final Network mNetwork; // 网络请求执行器
    private final Cache mCache; // 缓存器
    private final Delivery mDelivery;
    private volatile boolean mQuit = false; // 标记是否退出本线程

    public NetworkDispatcher(BlockingQueue<Request<?>> queue, Network network, Cache cache, Delivery delivery) {
        mQueue = queue;
        mNetwork = network;
        mCache = cache;
        mDelivery = delivery;
    }

    /**
     * 强制退出本线程
     */
    public void quit() {
        mQuit = true;
        interrupt();
    }

    private void addTrafficStatsTag(Request<?> request) {
        TrafficStats.setThreadStatsTag(request.getTrafficStatsTag());
    }

    /**
     * 阻塞态工作，不停的从队列中获取任务，直到退出。并把取出的request使用Network执行请求，然后NetWork返回一个NetWork响应
     */
    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        int stateCode = -1;
        while (true) {
            Request<?> request;
            try {
                request = mQueue.take();
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

    private void parseAndDeliverNetworkError(Request<?> request, HttpError error, int stateCode) {
        error = request.parseNetworkError(error);
        mDelivery.postError(request, error);
    }
}
