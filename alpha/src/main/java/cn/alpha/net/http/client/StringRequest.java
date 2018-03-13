package cn.alpha.net.http.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
 * Created by Law on 2017/4/5.
 */

public class StringRequest extends Request<byte[]> {
    private final HttpParams mParams;

    public StringRequest(RequestConfig config, HttpParams params, HttpCallback callback) {
        super(config, callback);
        if (params == null) {
            params = new HttpParams();
        }
        this.mParams = params;
    }

    @Override
    public String getCacheKey() {
        if (getMethod() == Http.Method.POST) {
            return getUrl() + mParams.getUrlParams();
        } else {
            return getUrl();
        }
    }

    public ArrayList<HttpParamsEntry> getHeaders() {
        return mParams.getHeaders();
    }

    @Override
    public String getBodyContentType() {
        if (mParams.getContentType() != null) {
            return mParams.getContentType();
        } else {
            return super.getBodyContentType();
        }
    }

    @Override
    public byte[] getBody() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            mParams.writeTo(bos);
        } catch (IOException e) {
            Logger.d("FormRequest#getBody()--->IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    public Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response.data, response.headers, HttpHeaderParser.parseCacheHeaders(getUseServerControl(), getCacheTime(), response));
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
    }

    @Override
    public Priority getPriority() {
        return Priority.IMMEDIATE;
    }
}
