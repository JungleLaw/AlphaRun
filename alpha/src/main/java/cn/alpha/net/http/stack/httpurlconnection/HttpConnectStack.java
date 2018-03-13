package cn.alpha.net.http.stack.httpurlconnection;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import cn.alpha.net.http.Http;
import cn.alpha.net.http.Request;
import cn.alpha.net.http.URLHttpResponse;
import cn.alpha.net.http.stack.IHttpStack;
import cn.alpha.net.http.tools.HTTPSTrustManager;
import cn.alpha.net.http.tools.HttpParamsEntry;

/**
  * HttpUrlConnection方式实现,添加的默认认可全部https证书
 * Created by Law on 2017/4/5.
 */

public class HttpConnectStack implements IHttpStack {
    private static final String HEADER_CONTENT_TYPE = "Content-Type";

    private final UrlRewriter mUrlRewriter;
    private final SSLSocketFactory mSslSocketFactory;

    public interface UrlRewriter {
        /**
         * 重写用于请求的URL
         */
        String rewriteUrl(String originalUrl);
    }

    public HttpConnectStack() {
        this(null);
    }

    public HttpConnectStack(UrlRewriter urlRewriter) {
        this(urlRewriter, null);
    }

    public HttpConnectStack(UrlRewriter urlRewriter, SSLSocketFactory sslSocketFactory) {
        mUrlRewriter = urlRewriter;
        mSslSocketFactory = sslSocketFactory;
    }

    @Override
    public URLHttpResponse performRequest(Request<?> request,
                                          ArrayList<HttpParamsEntry> additionalHeaders)
            throws IOException {
        String url = request.getUrl();
        ArrayList<HttpParamsEntry> header = new ArrayList<>();
        header.addAll(request.getHeaders());
        header.addAll(additionalHeaders);

        if (mUrlRewriter != null) {
            String rewritten = mUrlRewriter.rewriteUrl(url);
            if (rewritten == null) {
                throw new IOException("URL blocked by rewriter: " + url);
            }
            url = rewritten;
        }
        URL parsedUrl = new URL(url);
        HttpURLConnection connection = openConnection(parsedUrl, request);
        for (HttpParamsEntry entry : header) {
            connection.addRequestProperty(entry.k, entry.v);
        }
        setConnectionParametersForRequest(connection, request);
        return responseFromConnection(connection);
    }

    private URLHttpResponse responseFromConnection(HttpURLConnection connection)
            throws IOException {
        URLHttpResponse response = new URLHttpResponse();
        //contentStream
        InputStream inputStream;
        try {
            inputStream = connection.getInputStream();
        } catch (IOException ioe) {
            inputStream = connection.getErrorStream();
        }
        int responseCode = connection.getResponseCode();
        if (responseCode == -1) {
            throw new IOException(
                    "Could not retrieve response code from HttpUrlConnection.");
        }
        response.setResponseCode(responseCode);
        response.setResponseMessage(connection.getResponseMessage());

        response.setContentStream(inputStream);

        response.setContentLength(connection.getContentLength());
        response.setContentEncoding(connection.getContentEncoding());
        response.setContentType(connection.getContentType());
        //header
        HashMap<String, String> headerMap = new HashMap<>();
        for (Map.Entry<String, List<String>> header : connection.getHeaderFields()
                .entrySet()) {
            if (header.getKey() != null) {
                StringBuilder value = new StringBuilder();
                for (String v : header.getValue()) {
                    value.append(v).append(";");
                }
                headerMap.put(header.getKey(), value.toString());
            }
        }
        response.setHeaders(headerMap);
        return response;
    }

    private HttpURLConnection openConnection(URL url, Request<?> request)
            throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        int timeoutMs = request.getTimeoutMs();
        connection.setConnectTimeout(timeoutMs);
        connection.setReadTimeout(timeoutMs);
        connection.setUseCaches(false);
        connection.setDoInput(true);

        // use caller-provided custom SslSocketFactory, if any, for HTTPS
        if ("https".equals(url.getProtocol())) {
            if (mSslSocketFactory != null) {
                ((HttpsURLConnection) connection)
                        .setSSLSocketFactory(mSslSocketFactory);
            } else {
                //信任所有证书
                HTTPSTrustManager.allowAllSSL();
            }
        }
        return connection;
    }

    /* package */
    static void setConnectionParametersForRequest(HttpURLConnection connection, Request<?> request)
            throws IOException {
        switch (request.getMethod()) {
            case Http.Method.GET:
                connection.setRequestMethod("GET");
                break;
            case Http.Method.DELETE:
                connection.setRequestMethod("DELETE");
                break;
            case Http.Method.POST:
                connection.setRequestMethod("POST");
                addBodyIfExists(connection, request);
                break;
            case Http.Method.PUT:
                connection.setRequestMethod("PUT");
                addBodyIfExists(connection, request);
                break;
            case Http.Method.HEAD:
                connection.setRequestMethod("HEAD");
                break;
            case Http.Method.OPTIONS:
                connection.setRequestMethod("OPTIONS");
                break;
            case Http.Method.TRACE:
                connection.setRequestMethod("TRACE");
                break;
            case Http.Method.PATCH:
                connection.setRequestMethod("PATCH");
                addBodyIfExists(connection, request);
                break;
            default:
                throw new IllegalStateException("Unknown method type.");
        }
    }

    /**
     * 如果有body则添加
     */
    private static void addBodyIfExists(HttpURLConnection connection, Request<?> request)
            throws IOException {
        byte[] body = request.getBody();
        if (body != null) {
            connection.setDoOutput(true);
            connection.addRequestProperty(HEADER_CONTENT_TYPE,
                    request.getBodyContentType());
            DataOutputStream out = new DataOutputStream(
                    connection.getOutputStream());
            out.write(body);
            out.close();
        }
    }
}
