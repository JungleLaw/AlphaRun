package cn.alpha.net.http.client;

import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

import cn.alpha.net.http.HttpCallback;
import cn.alpha.net.http.HttpError;
import cn.alpha.net.http.NetworkResponse;
import cn.alpha.net.http.Request;
import cn.alpha.net.http.Response;
import cn.alpha.net.http.URLHttpResponse;
import cn.alpha.net.http.tools.HttpHeaderParser;
import cn.alpha.net.http.tools.HttpParamsEntry;
import cn.alpha.utils.Logger;

/**
 * Created by Law on 2017/4/5.
 */

public class FileRequest extends Request<byte[]> {
    private final File mStoreFile;
    private final File mTemporaryFile; // 临时文件

    private ArrayList<HttpParamsEntry> mHeaders = new ArrayList<>();

    public FileRequest(String storeFilePath, RequestConfig config, HttpCallback callback) {
        super(config, callback);
        mStoreFile = new File(storeFilePath);
        File folder = mStoreFile.getParentFile();

        if (folder != null && folder.mkdirs()) {
            if (!mStoreFile.exists()) {
                try {
                    mStoreFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        mTemporaryFile = new File(storeFilePath + ".tmp");
    }

    public static boolean isSupportRange(URLHttpResponse response) {
        if (TextUtils.equals(getHeader(response, "Accept-Ranges"), "bytes")) {
            return true;
        }
        String value = getHeader(response, "Content-Range");
        return value != null && value.startsWith("bytes");
    }

    public static String getHeader(URLHttpResponse response, String key) {
        return response.getHeaders().get(key);
    }

    public static boolean isGzipContent(URLHttpResponse response) {
        return TextUtils.equals(getHeader(response, "Content-Encoding"), "gzip");
    }

    public File getStoreFile() {
        return mStoreFile;
    }

    public File getTemporaryFile() {
        return mTemporaryFile;
    }

    @Override
    public String getCacheKey() {
        return "";
    }

    @Override
    public boolean shouldCache() {
        return false;
    }

    @Override
    public Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        String errorMessage = null;
        if (!isCanceled()) {
            if (mTemporaryFile.canRead() && mTemporaryFile.length() > 0) {
                if (mTemporaryFile.renameTo(mStoreFile)) {
                    return Response.success(response.data, response.headers, HttpHeaderParser.parseCacheHeaders(getConfig().mUseServerControl, getConfig().mCacheTime, response));
                } else {
                    errorMessage = "Can't rename the download temporary file!";
                }
            } else {
                errorMessage = "Download temporary file was invalid!";
            }
        }
        if (errorMessage == null) {
            errorMessage = "Request was Canceled!";
        }
        return Response.error(new HttpError(errorMessage));
    }

    @Override
    public ArrayList<HttpParamsEntry> getHeaders() {
        mHeaders.add(new HttpParamsEntry("Range", "bytes=" + mTemporaryFile.length() + "-"));
        mHeaders.add(new HttpParamsEntry("Accept-Encoding", "identity"));
        return mHeaders;
    }

    public ArrayList<HttpParamsEntry> putHeader(String k, String v) {
        mHeaders.add(new HttpParamsEntry(k, v));
        return mHeaders;
    }

    public byte[] handleResponse(URLHttpResponse response) throws IOException {
        long fileSize = response.getContentLength();
        if (fileSize <= 0) {
            Logger.d("Response doesn't present Content-Length!");
        }

        long downloadedSize = mTemporaryFile.length();
        boolean isSupportRange = isSupportRange(response);
        if (isSupportRange) {
            fileSize += downloadedSize;

            String realRangeValue = response.getHeaders().get("Content-Range");
            if (!TextUtils.isEmpty(realRangeValue)) {
                String assumeRangeValue = "bytes " + downloadedSize + "-" + (fileSize - 1);
                if (TextUtils.indexOf(realRangeValue, assumeRangeValue) == -1) {
                    Logger.d("The Content-Range Header is invalid Assume[" + assumeRangeValue + "] vs Real[" + realRangeValue + "], " + "please remove the temporary file [" + mTemporaryFile + "].");
                }
            }
        }

        if (fileSize > 0 && mStoreFile.length() == fileSize) {
            mStoreFile.renameTo(mTemporaryFile);
            if (mProgressListener != null)
                mRequestQueue.getDelivery().postProgress(mProgressListener, fileSize, fileSize);
            return null;
        }

        RandomAccessFile tmpFileRaf = new RandomAccessFile(mTemporaryFile, "rw");
        if (isSupportRange) {
            tmpFileRaf.seek(downloadedSize);
        } else {
            tmpFileRaf.setLength(0);
            downloadedSize = 0;
        }

        InputStream in = response.getContentStream();
        try {
            if (isGzipContent(response) && !(in instanceof GZIPInputStream)) {
                in = new GZIPInputStream(in);
            }
            byte[] buffer = new byte[6 * 1024]; // 6K buffer
            int offset;

            while ((offset = in.read(buffer)) != -1) {
                tmpFileRaf.write(buffer, 0, offset);
                downloadedSize += offset;
                //下载进度回调
                if (mProgressListener != null)
                    mRequestQueue.getDelivery().postProgress(mProgressListener, downloadedSize, fileSize);
                if (isCanceled()) {
                    break;
                }
            }
        } finally {
            if (in != null) {
                in.close();
            }
            try {
                response.getContentStream().close();
            } catch (Exception e) {
                Logger.d("Error occured when calling consumingContent");
            }
            tmpFileRaf.close();
        }
        return null;
    }

    @Override
    public Priority getPriority() {
        return Priority.LOW;
    }

    @Override
    protected void deliverResponse(ArrayList<HttpParamsEntry> headers, byte[] response) {
        HashMap<String, String> map = new HashMap<>(headers.size());
        for (HttpParamsEntry entry : headers) {
            map.put(entry.k, entry.v);
        }
        if (response == null)
            response = new byte[0];
        if (mCallback != null) {
            mCallback.onSuccess(map, response);
        }
        //        getConfig().mSubject.onNext(new Result(getUrl(), response, map));
    }
}

