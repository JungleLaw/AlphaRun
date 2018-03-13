package cn.alpha.net.http.client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import cn.alpha.net.http.HttpCallback;
import cn.alpha.net.http.tools.MultipartEntity;
import cn.alpha.utils.Logger;

/**
 * Created by Law on 2017/4/11.
 */

public class UploadRequest extends FormRequest {
    private MultipartEntity mUploadMultipartEntity = new MultipartEntity();
    private final String mFileName;
    private final File mUploadFile;
    //    private final HttpParams mParams;
    //    //
    //    private ArrayList<HttpParamsEntry> mHeaders = new ArrayList<>();
    //
    //    public UploadRequest(String fileName, File file, RequestConfig config, HttpParams params, HttpCallback callback) {
    //        super(config, callback);
    //        mFileName = fileName;
    //        mUploadFile = file;
    //        if (params == null) {
    //            params = new HttpParams();
    //        }
    //        mParams = params;
    //    }
    //
    //    public MultipartEntity getMutltipartEntity() {
    //        return mUploadMultipartEntity;
    //    }
    //
    //    @Override
    //    public String getBodyContentType() {
    //        return mUploadMultipartEntity.getContentType();
    //    }
    //
    //
    //    public static String getHeader(URLHttpResponse response, String key) {
    //        return response.getHeaders().get(key);
    //    }
    //
    //    public static boolean isGzipContent(URLHttpResponse response) {
    //        return TextUtils.equals(getHeader(response, "Content-Encoding"), "gzip");
    //    }
    //
    //    public String getFileName() {
    //        return mFileName;
    //    }
    //
    //    public File getUploadFile() {
    //        return mUploadFile;
    //    }
    //
    //    @Override
    //    public String getCacheKey() {
    //        return "";
    //    }
    //
    //    @Override
    //    public boolean shouldCache() {
    //        return false;
    //    }
    //
    //    @Override
    //    public Response<byte[]> parseNetworkResponse(NetworkResponse response) {
    //        String errorMessage = null;
    //        if (!isCanceled()) {
    //            return Response.success(response.data, response.headers, HttpHeaderParser.parseCacheHeaders(getConfig().mUseServerControl, getConfig().mCacheTime, response));
    //        }
    //        if (errorMessage == null) {
    //            errorMessage = "Request was Canceled!";
    //        }
    //        return Response.error(new HttpError(errorMessage));
    //    }
    //
    //    @Override
    //    public ArrayList<HttpParamsEntry> getHeaders() {
    //        return mHeaders;
    //    }
    //
    //    public ArrayList<HttpParamsEntry> putHeader(String k, String v) {
    //        mHeaders.add(new HttpParamsEntry(k, v));
    //        return mHeaders;
    //    }
    //
    //    public byte[] handleResponse(URLHttpResponse response) throws IOException {
    //        Logger.i(response.getContentEncoding());
    //        return null;
    //    }
    //
    //    @Override
    //    public Priority getPriority() {
    //        return Priority.LOW;
    //    }
    //
    //    @Override
    //    protected void deliverResponse(ArrayList<HttpParamsEntry> headers, byte[] response) {
    //        HashMap<String, String> map = new HashMap<>(headers.size());
    //        for (HttpParamsEntry entry : headers) {
    //            map.put(entry.k, entry.v);
    //        }
    //        if (response == null)
    //            response = new byte[0];
    //        if (mCallback != null) {
    //            mCallback.onSuccess(map, response);
    //        }
    //    }
    //
    //    @Override
    //    public byte[] getBody() {
    //        ByteArrayOutputStream bos = new ByteArrayOutputStream();
    //        try {
    //            // multipart body
    ////            mUploadMultipartEntity.writeTo(bos);
    //            mParams.writeTo(bos);
    //        } catch (IOException e) {
    //            Logger.e("IOException writing to ByteArrayOutputStream");
    //        }
    //        return bos.toByteArray();
    //    }

    private HttpParams mHttpParams;
    public UploadRequest(String fileName, File file, RequestConfig config, HttpParams params, HttpCallback callback) {
        super(config, params, callback);
        this.mFileName = fileName;
        this.mUploadFile = file;
        params.put(fileName, file);
        this.mHttpParams = params;
        Logger.i(mHttpParams.getContentType());
    }

//    public byte[] handleResponse(URLHttpResponse response) throws IOException {
//        Logger.i("handleResponse");
//        return response.getResponseMessage();
//    }


//    @Override
//    public byte[] getBody() {
////        FileOutputStream fos;
////        try {
////            fos = new FileOutputStream(mUploadFile);
////            mHttpParams.writeTo(fos);
////        } catch (FileNotFoundException e) {
////            e.printStackTrace();
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//
//        return getBytes(mUploadFile.getAbsolutePath());
//    }
    /**
     * 将文件转为byte[]
     * @param filePath 文件路径
     * @return
     */
    public static byte[] getBytes(String filePath){
        File file = new File(filePath);
        ByteArrayOutputStream out = null;
        try {
            FileInputStream in = new FileInputStream(file);
            out = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int i = 0;
            while ((i = in.read(b)) != -1) {
                out.write(b, 0, b.length);
            }
            out.close();
            in.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        byte[] s = out.toByteArray();
        return s;

    }

}
