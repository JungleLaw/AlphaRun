package cn.alpha.net.http.client;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import cn.alpha.net.http.Http;
import cn.alpha.net.http.HttpCallback;
import cn.alpha.net.http.NetworkResponse;
import cn.alpha.net.http.Request;
import cn.alpha.net.http.Response;
import cn.alpha.net.http.tools.HttpHeaderParser;
import cn.alpha.net.http.tools.HttpParamsEntry;
import cn.alpha.utils.Logger;

/**
 * 用来发起application/json格式的请求的，我们平时所使用的是form表单提交的参数，而使用JsonRequest提交的是json参数。
 * Created by Law on 2017/4/5.
 */

public class JSONRequest extends Request<byte[]> {
    private final String mRequestBody;
    private final HttpParams mParams;

    public JSONRequest(RequestConfig config, HttpParams params, HttpCallback callback) {
        super(config, callback);
        mRequestBody = params.getJsonParams();
        mParams = params;
    }

    @Override
    public ArrayList<HttpParamsEntry> getHeaders() {
        return mParams.getHeaders();
    }

    @Override
    protected void deliverResponse(ArrayList<HttpParamsEntry> headers, byte[] response) {
        HashMap<String, String> map = new HashMap<>(headers.size());
        for (HttpParamsEntry entry : headers) {
            map.put(entry.k, entry.v);
        }
        if (mCallback != null) {
            mCallback.onSuccess(map, response);
        }
        //        getConfig().mSubject.onNext(new Result(getUrl(), response, map));
    }

    @Override
    public Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response.data, response.headers, HttpHeaderParser.parseCacheHeaders(getUseServerControl(), getCacheTime(), response));
    }

    @Override
    public String getBodyContentType() {
        return String.format("application/json; charset=%s", getConfig().mEncoding);
    }

    @Override
    public String getCacheKey() {
        if (getMethod() == Http.Method.POST) {
            return getUrl() + mParams.getJsonParams();
        } else {
            return getUrl();
        }
    }

    @Override
    public byte[] getBody() {
        try {
            return mRequestBody == null ? null : mRequestBody.getBytes(getConfig().mEncoding);
        } catch (UnsupportedEncodingException uee) {
            Logger.d(String.format("Unsupported Encoding while trying to get the bytes of %s" + " using %s", mRequestBody, getConfig().mEncoding));
            return null;
        }
    }

    @Override
    public Priority getPriority() {
        return Priority.IMMEDIATE;
    }
}
