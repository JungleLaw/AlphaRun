package cn.alpha.net.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import cn.alpha.net.http.client.DownloadRequest;
import cn.alpha.net.http.client.FileRequest;
import cn.alpha.net.http.client.UploadRequest;
import cn.alpha.net.http.stack.IHttpStack;
import cn.alpha.net.http.tools.ByteArrayPool;
import cn.alpha.net.http.tools.HttpParamsEntry;
import cn.alpha.net.http.tools.HttpStatus;
import cn.alpha.net.http.tools.PoolingByteArrayOutputStream;
import cn.alpha.utils.FileUtils;
import cn.alpha.utils.Logger;

/**
 * Created by Law on 2017/4/5.
 */

public class NetworkImpl implements Network {
    protected final IHttpStack mHttpStack;

    public NetworkImpl(IHttpStack httpStack) {
        mHttpStack = httpStack;
    }

    /**
     * 实际执行一个请求的方法
     *
     * @param request 一个请求任务
     * @return 一个不会为null的响应
     * @throws HttpError
     */
    @Override
    public NetworkResponse performRequest(Request<?> request) throws HttpError {
        while (true) {
            URLHttpResponse httpResponse = null;
            byte[] responseContents = null;
            HashMap<String, String> responseHeaders = new HashMap<>();
            try {
                // 标记Http响应头在Cache中的tag
                ArrayList<HttpParamsEntry> headers = new ArrayList<>();

                addCacheHeaders(headers, request.getCacheEntry());
                httpResponse = mHttpStack.performRequest(request, headers);

                int statusCode = httpResponse.getResponseCode();
                responseHeaders = httpResponse.getHeaders();

                if (statusCode == HttpStatus.SC_NOT_MODIFIED) { // 304
                    return new NetworkResponse(HttpStatus.SC_NOT_MODIFIED, request.getCacheEntry() == null ? null : request.getCacheEntry().data, responseHeaders, true);
                }

                if (httpResponse.getContentStream() != null) {
                    if (request instanceof FileRequest) {
                        responseContents = ((FileRequest) request).handleResponse(httpResponse);
                    } else if (request instanceof DownloadRequest) {
                        responseContents = ((DownloadRequest) request).handleResponse(httpResponse);
                    } else if (request instanceof UploadRequest) {
//                        responseContents = ((UploadRequest) request).handleResponse(httpResponse);
                        Logger.i(statusCode);
                        responseContents = entityToBytes(httpResponse);
                    } else {
                        responseContents = entityToBytes(httpResponse);
                    }
                } else {
                    responseContents = new byte[0];
                }

                if (statusCode < 200 || statusCode > 299) {
                    throw new IOException();
                }
                return new NetworkResponse(statusCode, responseContents, responseHeaders, false);
            } catch (SocketTimeoutException e) {
                attemptRetryOnException("socket", request, new HttpError(new SocketTimeoutException("socket timeout")));
            } catch (MalformedURLException e) {
                attemptRetryOnException("connection", request, new HttpError("Bad URL " + request.getUrl(), e));
            } catch (IOException e) {
                int statusCode;
                NetworkResponse networkResponse;
                if (httpResponse != null) {
                    statusCode = httpResponse.getResponseCode();
                } else {
                    throw new HttpError("NoConnection error", e);
                }
                Logger.d(String.format("Unexpected response code %d for %s", statusCode, request.getUrl()));
                if (responseContents != null) {
                    networkResponse = new NetworkResponse(statusCode, responseContents, responseHeaders, false);
                    if (statusCode == HttpStatus.SC_UNAUTHORIZED || statusCode == HttpStatus.SC_FORBIDDEN) {
                        attemptRetryOnException("auth", request, new HttpError(networkResponse));
                    } else {
                        throw new HttpError(networkResponse);
                    }
                } else {
                    throw new HttpError(String.format("Unexpected response code %d for %s", statusCode, request.getUrl()));
                }
            }
        }
    }

    /**
     * Attempts to prepare the request for a retry. If there are no more
     * attempts remaining in the request's retry policy, a timeout exception is
     * thrown.
     *
     * @param request The request to use.
     */
    private static void attemptRetryOnException(String logPrefix, Request<?> request, HttpError exception) throws HttpError {
        RetryPolicy retryPolicy = request.getRetryPolicy();
        int oldTimeout = request.getTimeoutMs();

        try {
            if (retryPolicy != null) {
                retryPolicy.retry(exception);
            } else {
                Logger.d("not retry policy");
            }
        } catch (HttpError e) {
            Logger.d(String.format("%s-timeout-giveup [timeout=%s]", logPrefix, oldTimeout));
            throw e;
        }
        Logger.d(String.format("%s-retry [timeout=%s]", logPrefix, oldTimeout));
    }

    /**
     * 标记Respondeader响应头在Cache中的tag
     */
    private void addCacheHeaders(ArrayList<HttpParamsEntry> headers, Cache.Entry entry) {
        if (entry == null) {
            return;
        }
        if (entry.etag != null) {
            headers.add(new HttpParamsEntry("If-None-Match", entry.etag));
        }
        if (entry.serverDate > 0) {
            Date refTime = new Date(entry.serverDate);
            DateFormat sdf = SimpleDateFormat.getDateTimeInstance();
            headers.add(new HttpParamsEntry("If-Modified-Since", sdf.format(refTime)));

        }
    }

    /**
     * 把HttpEntry转换为byte[]
     *
     * @throws IOException
     * @throws HttpError
     */
    private byte[] entityToBytes(URLHttpResponse httpResponse) throws IOException, HttpError {
        PoolingByteArrayOutputStream bytes = new PoolingByteArrayOutputStream(ByteArrayPool.get(), (int) httpResponse.getContentLength());
        byte[] buffer = null;
        try {
            InputStream in = httpResponse.getContentStream();
            if (in == null) {
                throw new HttpError("server error");
            }
            buffer = ByteArrayPool.get().getBuf(1024);
            int count;
            while ((count = in.read(buffer)) != -1) {
                bytes.write(buffer, 0, count);
            }
            return bytes.toByteArray();
        } finally {
            FileUtils.closeIO(httpResponse.getContentStream());
            ByteArrayPool.get().returnBuf(buffer);
            FileUtils.closeIO(bytes);
        }
    }
}
