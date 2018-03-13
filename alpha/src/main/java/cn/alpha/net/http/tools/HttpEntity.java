package cn.alpha.net.http.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Law on 2017/4/12.
 */

public interface HttpEntity {
    boolean isRepeatable();

    boolean isChunked();

    long getContentLength();

    String getContentType();

    String getContentEncoding();

    InputStream getContent() throws IOException, IllegalStateException;

    void writeTo(OutputStream var1) throws IOException;

    boolean isStreaming();

    void consumeContent() throws IOException;
}
