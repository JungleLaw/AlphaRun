package cn.alpha.net.http;

import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import cn.alpha.net.http.client.DownloadRequest;
import cn.alpha.net.http.client.UploadRequest;
import cn.alpha.utils.Logger;

/**
 * 本类工作流程： 每当发起一次Request，会对这个Request标记一个唯一值。<br>
 * 并加入当前请求的Set中(保证唯一;方便控制)。<br>
 * 同时判断是否启用缓存，若启用则加入缓存队列，否则加入执行队列。<br>
 * Note:
 * 整个RequestQueue工作流程：采用责任链设计模式，由三部分组成，类似设计可以类比Handle...Looper...MessageQueue<br>
 * 1、RequestQueue负责不停向NetworkQueue(或CacheQueue实际还是NetworkQueue， 具体逻辑请查看
 * {@link CacheDispatcher})添加Request<br>
 * 2、另一边由TaskThread不停从NetworkQueue中取Request并交给Network执行器(逻辑请查看
 * {@link NetworkDispatcher} )，<br>
 * 3、Network执行器将执行成功的NetworkResponse返回给TaskThead，并通过Request的定制方法
 * Request.parseNetworkResponse()封装成Response，最终交给分发器 Delivery
 * 分发到主线程并调用HttpCallback相应的方法
 * Created by Law on 2017/4/5.
 */

public class RequestQueue {
    /**
     * Number of network request dispatcher threads to start.
     */
    private static final int DEFAULT_NETWORK_THREAD_POOL_SIZE = 4;
    /**
     * Staging area for requests that already have a duplicate request in flight.
     * <p/>
     * <ul>
     * <li>containsKey(cacheKey) indicates that there is a request in flight for the given cache
     * key.</li>
     * <li>get(cacheKey) returns waiting requests for the given cache key. The in flight request
     * is <em>not</em> contained in that list. Is null if no requests are staged.</li>
     * </ul>
     */
    private final Map<String, Queue<Request<?>>> mWaitingRequests = new HashMap<>();

    /**
     * The set of all requests currently being processed by this RequestQueue. A Request
     * will be in this set if it is waiting in any queue or currently being processed by
     * any dispatcher.
     */
    private final Set<Request<?>> mCurrentRequests = new HashSet<>();

    /**
     * The cache triage queue.
     */
    private final PriorityBlockingQueue<Request<?>> mCacheQueue = new PriorityBlockingQueue<>();

    /**
     * The queue of requests that are actually going out to the network.
     */
    private final PriorityBlockingQueue<Request<?>> mNetworkQueue = new PriorityBlockingQueue<>();
    /**
     * The queue of requests that are actually going out to the download.
     */
    private final PriorityBlockingQueue<Request<?>> mDownloadQueue = new PriorityBlockingQueue<>();
    /**
     * The queue of requests that are actually going out to the upload.
     */
    private final PriorityBlockingQueue<Request<?>> mUploadQueue = new PriorityBlockingQueue<>();

    /**
     * Cache interface for retrieving and storing responses.
     */
    private final Cache mCache;
    /**
     * Network interface for performing requests.
     */
    private final Network mNetwork;
    /**
     * Response delivery mechanism.
     */
    private final Delivery mDelivery;
    /**
     * Used for generating monotonically-increasing sequence numbers for requests.
     */
    private AtomicInteger mSequenceGenerator = new AtomicInteger();
    /**
     * The network dispatchers.
     */
    private NetworkDispatcher[] mNetworkDispatchers;

    /**
     * The cache dispatcher.
     */
    private CacheDispatcher mCacheDispatcher;

    private DownloadDispatch[] mDownloadDispatchs;

    private UploadDispatcher mUploadDispatch;

    /**
     * Creates the worker pool. Processing will not begin until {@link #start()} is called.
     *
     * @param cache          A Cache to use for persisting responses to disk
     * @param network        A Network interface for performing HTTP requests
     * @param threadPoolSize Number of network dispatcher threads to create
     * @param delivery       A ResponseDelivery interface for posting responses and errors
     */
    public RequestQueue(Cache cache, Network network, int threadPoolSize, Delivery delivery) {
        mCache = cache;
        mNetwork = network;
        mNetworkDispatchers = new NetworkDispatcher[threadPoolSize];
        mDownloadDispatchs = new DownloadDispatch[threadPoolSize];
        mDelivery = delivery;
    }

    /**
     * Creates the worker pool. Processing will not begin until {@link #start()} is called.
     *
     * @param cache          A Cache to use for persisting responses to disk
     * @param network        A Network interface for performing HTTP requests
     * @param threadPoolSize Number of network dispatcher threads to create
     */
    public RequestQueue(Cache cache, Network network, int threadPoolSize) {
        this(cache, network, threadPoolSize, new ExecutorDelivery(new Handler(Looper.getMainLooper())));
    }

    /**
     * Creates the worker pool. Processing will not begin until {@link #start()} is called.
     *
     * @param cache   A Cache to use for persisting responses to disk
     * @param network A Network interface for performing HTTP requests
     */
    public RequestQueue(Cache cache, Network network) {
        this(cache, network, DEFAULT_NETWORK_THREAD_POOL_SIZE);
    }

    /**
     * Starts the dispatchers in this queue.
     */
    public void start() {
        stop();  // Make sure any currently running dispatchers are stopped.
        // Create the cache dispatcher and start it.
        mCacheDispatcher = new CacheDispatcher(mCacheQueue, mNetworkQueue, mCache, mDelivery);
        mCacheDispatcher.start();

        // Create network dispatchers (and corresponding threads) up to the pool size.
        for (int i = 0; i < mNetworkDispatchers.length; i++) {
            NetworkDispatcher networkDispatcher = new NetworkDispatcher(mNetworkQueue, mNetwork, mCache, mDelivery);
            mNetworkDispatchers[i] = networkDispatcher;
            networkDispatcher.start();
        }

        for (int i = 0; i < mDownloadDispatchs.length; i++) {
            DownloadDispatch downloadDispatch = new DownloadDispatch(mDownloadQueue, mNetwork, mCache, mDelivery);
            mDownloadDispatchs[i] = downloadDispatch;
            downloadDispatch.start();
        }

        mUploadDispatch = new UploadDispatcher(mUploadQueue, mNetwork, mCache, mDelivery);
        mUploadDispatch.start();
    }

    /**
     * Stops the cache and network dispatchers.
     */
    public void stop() {
        if (mCacheDispatcher != null) {
            mCacheDispatcher.quit();
        }

        for (NetworkDispatcher dispatcher : mNetworkDispatchers) {
            if (dispatcher != null) {
                dispatcher.quit();
            }
        }

        for (DownloadDispatch dispatcher : mDownloadDispatchs) {
            if (dispatcher != null) {
                dispatcher.quit();
            }
        }

        if (mUploadDispatch != null) {
            mUploadDispatch.quit();
        }
    }

    /**
     * Gets a sequence number.
     */
    public int getSequenceNumber() {
        return mSequenceGenerator.incrementAndGet();
    }

    /**
     * Gets the {@link Cache} instance being used.
     */
    public Cache getCache() {
        return mCache;
    }

    public Delivery getDelivery() {
        return mDelivery;
    }

    /**
     * Cancels all requests in this queue for which the given filter applies.
     *
     * @param filter The filtering function to use
     */
    public void cancelAll(RequestFilter filter) {
        synchronized (mCurrentRequests) {
            for (Request<?> request : mCurrentRequests) {
                if (filter.apply(request)) {
                    request.cancel();
                }
            }
        }
    }

    public void cancelAll() {
        synchronized (mCurrentRequests) {
            for (Request<?> request : mCurrentRequests) {
                request.cancel();
            }
        }
    }

    /**
     * Cancels all requests in this queue with the given tag. Tag must be non-null
     * and equality is by identity.
     */
    public void cancelAll(final Object tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Cannot cancelAll with a null tag");
        }
        cancelAll(new RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return request.getTag() == tag;
            }
        });
    }

    /**
     * Adds a Request to the dispatch queue.
     *
     * @param request The request to service
     * @return The passed-in request
     */
    public <T> Request<T> add(Request<T> request) {
        // Tag the request as belonging to this queue and add it to the set of current requests.
        request.setRequestQueue(this);
        synchronized (mCurrentRequests) {
            mCurrentRequests.add(request);
        }

        // Process requests in the order they are added.
        request.setSequence(getSequenceNumber());
        if (!request.shouldCache()) {
            if (request instanceof DownloadRequest) {
                Logger.i("DownloadRequest start");
                mDownloadQueue.add(request);
                return request;
            } else if (request instanceof UploadRequest) {
                mUploadQueue.add(request);
                return request;
            }
            // If the request is uncacheable, skip the cache queue and go straight to the network.
            else {
                mNetworkQueue.add(request);
                return request;
            }
        }


        // Insert request into stage if there's already a request with the same cache key in flight.
        synchronized (mWaitingRequests) {
            String cacheKey = request.getCacheKey();
            if (mWaitingRequests.containsKey(cacheKey)) {
                // There is already a request in flight. Queue up.
                Queue<Request<?>> stagedRequests = mWaitingRequests.get(cacheKey);
                if (stagedRequests == null) {
                    stagedRequests = new LinkedList<>();
                }
                stagedRequests.add(request);
                mWaitingRequests.put(cacheKey, stagedRequests);
                Logger.d(String.format("Request for cacheKey=%s is in flight, putting on hold" + ".", cacheKey));
            } else {
                // Insert 'null' queue for this cacheKey, indicating there is now a request in
                // flight.
                mWaitingRequests.put(cacheKey, null);
                mCacheQueue.add(request);
            }
            return request;
        }
    }

    /**
     * Called from {@link Request#finish(String)}, indicating that processing of the given request
     * has finished.
     * <p/>
     * <p>Releases waiting requests for <code>request.getCacheKey()</code> if
     * <code>request.shouldCache()</code>.</p>
     */
    void finish(Request<?> request) {
        // Remove from the set of requests currently being processed.
        synchronized (mCurrentRequests) {
            mCurrentRequests.remove(request);
        }

        if (request.shouldCache()) {
            synchronized (mWaitingRequests) {
                String cacheKey = request.getCacheKey();
                Queue<Request<?>> waitingRequests = mWaitingRequests.remove(cacheKey);
                if (waitingRequests != null) {
                    Logger.d(String.format("Releasing %d waiting requests for cacheKey=%s.", waitingRequests.size(), cacheKey));
                    // Process all queued up requests. They won't be considered as in flight, but
                    // that's not a problem as the cache has been primed by 'request'.
                    mCacheQueue.addAll(waitingRequests);
                }
            }
        }
    }

    /**
     * A simple predicate or filter interface for Requests, for use by
     * {@link RequestQueue#cancelAll(RequestFilter)}.
     */
    public interface RequestFilter {
        boolean apply(Request<?> request);
    }
}
