package cn.alpha.net.http.client;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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

public class FormRequest extends Request<byte[]> {

    private final HttpParams mParams;

    public FormRequest(RequestConfig config, HttpParams params, HttpCallback callback) {
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

    @Override
    public String getBodyContentType() {
        if (mParams.getContentType() != null) {
            return mParams.getContentType();
        } else {
            return super.getBodyContentType();
        }
    }

    @Override
    public ArrayList<HttpParamsEntry> getHeaders() {
        return mParams.getHeaders();
    }

    @Override
    public byte[] getBody() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            if (mProgressListener != null) {
                mParams.writeTo(new CountingOutputStream(bos, mParams.getContentLength(), mProgressListener));
            } else {
                mParams.writeTo(bos);
            }
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
    protected void deliverResponse(ArrayList<HttpParamsEntry> headers, final byte[] response) {
        final HashMap<String, String> map = new HashMap<>(headers.size());
        for (HttpParamsEntry entry : headers) {
            map.put(entry.k, entry.v);
        }
        if (mCallback != null) {
            mCallback.onSuccess(map, response);
        }
        //        getConfig().mSubject.onNext(new Result(getUrl(), response, map));
    }

    @Override
    public Priority getPriority() {
        return Priority.IMMEDIATE;
    }

    public static class CountingOutputStream extends FilterOutputStream {
        private final ProgressListener progListener;
        private long transferred;
        private long fileLength;

        public CountingOutputStream(final OutputStream out, long fileLength, final ProgressListener listener) {
            super(out);
            this.fileLength = fileLength;
            this.progListener = listener;
            this.transferred = 0;
        }

        public void write(int b) throws IOException {
            out.write(b);
            if (progListener != null) {
                this.transferred++;
                if ((transferred % 20 == 0) && (transferred <= fileLength)) {
                    Http.getRequestQueue().getDelivery().postProgress(this.progListener, this.transferred, fileLength);
                }
            }
        }
    }
}
