package cn.alpha.net.http.client;

/**
 * Created by Law on 2017/4/5.
 */

public interface ProgressListener {
    /**
     * Callback method thats called on each byte transfer.
     */
    void onProgress(long transferredBytes, long totalSize);
}
