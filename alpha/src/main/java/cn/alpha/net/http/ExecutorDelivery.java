package cn.alpha.net.http;

import android.os.Handler;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import cn.alpha.net.http.client.ProgressListener;
import cn.alpha.net.http.tools.HttpParamsEntry;

/**
 * Created by Law on 2017/4/5.
 */

public class ExecutorDelivery implements Delivery {
    /**
     * Used for posting responses, typically to the main thread.
     */
    private final Executor mResponsePoster;

    /**
     * Creates a new response delivery interface.
     *
     * @param handler {@link Handler} to post responses on
     */
    public ExecutorDelivery(final Handler handler) {
        // Make an Executor that just wraps the handler.
        mResponsePoster = new Executor() {
            @Override
            public void execute(Runnable command) {
                handler.post(command);
            }
        };
    }

    /**
     * Creates a new response delivery interface, mockable version
     * for testing.
     *
     * @param executor For running delivery tasks
     */
    public ExecutorDelivery(Executor executor) {
        mResponsePoster = executor;
    }

    @Override
    public void postResponse(Request<?> request, Response<?> response) {
        postResponse(request, response, null);
    }

    @Override
    public void postResponse(Request<?> request, Response<?> response, Runnable runnable) {
        request.markDelivered();
        mResponsePoster.execute(new ResponseDeliveryRunnable(request, response, runnable));
    }

    @Override
    public void postError(Request<?> request, HttpError error) {
        Response<?> response = Response.error(error);
        mResponsePoster.execute(new ResponseDeliveryRunnable(request, response, null));
    }

    @Override
    public void postStartHttp(final Request<?> request) {
        mResponsePoster.execute(new Runnable() {
            @Override
            public void run() {
                request.deliverStartHttp();
            }
        });
    }

    @Override
    public void postProgress(final ProgressListener listener, final long transferredBytes, final long totalSize) {
        mResponsePoster.execute(new Runnable() {
            @Override
            public void run() {
                listener.onProgress(transferredBytes, totalSize);
            }
        });
    }

    /**
     * A Runnable used for delivering network responses to a listener on the
     * main thread.
     */
    @SuppressWarnings("rawtypes")
    private static class ResponseDeliveryRunnable implements Runnable {
        private final Request mRequest;
        private final Response mResponse;
        private final Runnable mRunnable;

        public ResponseDeliveryRunnable(Request request, Response response, Runnable runnable) {
            mRequest = request;
            mResponse = response;
            mRunnable = runnable;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            // If this request has canceled, finish it and don't deliver.
            if (mRequest.isCanceled()) {
                mRequest.finish("canceled-at-delivery");
                return;
            }

            // Deliver a normal response or error, depending.
            if (mResponse.isSuccess()) {
                ArrayList<HttpParamsEntry> headers = new ArrayList<>();
                if (mResponse.headers != null) {
                    Set<Map.Entry<String, String>> entrySet = mResponse.headers.entrySet();
                    for (Map.Entry<String, String> entry : entrySet) {
                        headers.add(new HttpParamsEntry(entry.getKey(), entry.getValue()));
                    }
                }
                mRequest.deliverResponse(headers, mResponse.result);
            } else {
                mRequest.deliverError(mResponse.error);
            }
            mRequest.finish("done");
            mRequest.requestFinish();
            // If we have been provided a post-delivery runnable, run it.
            if (mRunnable != null) {
                mRunnable.run();
            }
        }
    }
}
