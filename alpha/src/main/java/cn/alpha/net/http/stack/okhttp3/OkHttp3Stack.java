package cn.alpha.net.http.stack.okhttp3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import cn.alpha.BuildConfig;
import cn.alpha.net.http.Http;
import cn.alpha.net.http.Request;
import cn.alpha.net.http.URLHttpResponse;
import cn.alpha.net.http.stack.IHttpStack;
import cn.alpha.net.http.stack.okhttp3.loginterceptor.Level;
import cn.alpha.net.http.stack.okhttp3.loginterceptor.LoggingInterceptor;
import cn.alpha.net.http.tools.HttpParamsEntry;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.platform.Platform;

/**
 * Created by Law on 2017/4/5.
 */

public class OkHttp3Stack implements IHttpStack {
    private final OkHttpClient mClient;

    public OkHttp3Stack(OkHttpClient client) {
        this.mClient = client;
    }

    @SuppressWarnings("deprecation")
    private static void setConnectionParametersForRequest(okhttp3.Request.Builder builder, Request<?> request) throws IOException {
        switch (request.getMethod()) {
            case Http.Method.GET:
                builder.get();
                break;

            case Http.Method.DELETE:
                builder.delete();
                break;

            case Http.Method.POST:
                builder.post(createRequestBody(request));
                break;

            case Http.Method.PUT:
                builder.put(createRequestBody(request));
                break;

            case Http.Method.HEAD:
                builder.head();
                break;

            case Http.Method.OPTIONS:
                builder.method("OPTIONS", null);
                break;

            case Http.Method.TRACE:
                builder.method("TRACE", null);
                break;

            case Http.Method.PATCH:
                builder.patch(createRequestBody(request));
                break;

            default:
                throw new IllegalStateException("Unknown method type.");
        }
    }

    private static RequestBody createRequestBody(Request request) {
        final byte[] body = request.getBody();
        if (body == null)
            return null;

        return RequestBody.create(MediaType.parse(request.getBodyContentType()), body);
    }

    @Override
    public URLHttpResponse performRequest(Request<?> request, ArrayList<HttpParamsEntry> additionalHeaders) throws IOException {
        int timeoutMs = request.getTimeoutMs();
        OkHttpClient client = mClient.newBuilder()
                .addInterceptor(new LoggingInterceptor.Builder()
                                .loggable(true)
                                .setLevel(Level.BASIC)
                                .log(Platform.INFO)
                                .request("Request")
                                .response("Response")
                                .addHeader("version", BuildConfig.VERSION_NAME)
                                .build())
                .readTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                .connectTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                .writeTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                .build();

        okhttp3.Request.Builder okHttpRequestBuilder = new okhttp3.Request.Builder();
        okHttpRequestBuilder.url(request.getUrl());

        for (final HttpParamsEntry entry : request.getHeaders()) {
            okHttpRequestBuilder.addHeader(entry.k, entry.v);
        }
        for (final HttpParamsEntry entry : additionalHeaders) {
            okHttpRequestBuilder.addHeader(entry.k, entry.v);
        }

        setConnectionParametersForRequest(okHttpRequestBuilder, request);
        okhttp3.Request okHttpRequest = okHttpRequestBuilder.build();
        Call okHttpCall = client.newCall(okHttpRequest);
        Response okHttpResponse = okHttpCall.execute();

        return responseFromConnection(okHttpResponse);
    }

    private URLHttpResponse responseFromConnection(Response okHttpResponse) throws IOException {
        URLHttpResponse response = new URLHttpResponse();
        //contentStream
        int responseCode = okHttpResponse.code();
        if (responseCode == -1) {
            throw new IOException("Could not retrieve response code from HttpUrlConnection.");
        }
        response.setResponseCode(responseCode);
        response.setResponseMessage(okHttpResponse.message());

        response.setContentStream(okHttpResponse.body().byteStream());

        response.setContentLength(okHttpResponse.body().contentLength());
        response.setContentEncoding(okHttpResponse.header("Content-Encoding"));
        if (okHttpResponse.body().contentType() != null) {
            response.setContentType(okHttpResponse.body().contentType().type());
        }


        //header
        HashMap<String, String> headerMap = new HashMap<>();
        Headers responseHeaders = okHttpResponse.headers();
        for (int i = 0, len = responseHeaders.size(); i < len; i++) {
            final String name = responseHeaders.name(i), value = responseHeaders.value(i);
            if (name != null) {
                headerMap.put(name, value);
            }
        }
        response.setHeaders(headerMap);
        return response;
    }
}
