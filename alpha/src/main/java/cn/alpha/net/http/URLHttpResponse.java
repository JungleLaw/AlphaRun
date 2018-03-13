package cn.alpha.net.http;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.InputStream;
import java.util.HashMap;

/**
 * 兼容6.0，用于替换掉org.apache.http.HttpResponse
 * <p>
 * Created by Law on 2017/4/5.
 */

public class URLHttpResponse implements Parcelable {
    public static final Creator<URLHttpResponse> CREATOR = new Creator<URLHttpResponse>() {
        public URLHttpResponse createFromParcel(Parcel source) {
            return new URLHttpResponse(source);
        }

        public URLHttpResponse[] newArray(int size) {
            return new URLHttpResponse[size];
        }
    };
    private static final long serialVersionUID = 1L;
    private HashMap<String, String> headers;
    private int responseCode;
    private String responseMessage;
    private InputStream contentStream;
    private String contentEncoding;
    private String contentType;
    private long contentLength;

    public URLHttpResponse() {
    }

    protected URLHttpResponse(Parcel in) {
        this.headers = (HashMap<String, String>) in.readSerializable();
        this.responseCode = in.readInt();
        this.responseMessage = in.readString();
        this.contentEncoding = in.readString();
        this.contentType = in.readString();
        this.contentLength = in.readLong();
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public InputStream getContentStream() {
        return contentStream;
    }

    public void setContentStream(InputStream contentStream) {
        this.contentStream = contentStream;
    }

    public String getContentEncoding() {
        return contentEncoding;
    }

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.headers);
        dest.writeInt(this.responseCode);
        dest.writeString(this.responseMessage);
        dest.writeString(this.contentEncoding);
        dest.writeString(this.contentType);
        dest.writeLong(this.contentLength);
    }
}
