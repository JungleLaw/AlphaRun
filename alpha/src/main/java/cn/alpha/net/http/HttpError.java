package cn.alpha.net.http;


/**
 * Exception style class encapsulating Volley errors
 *
 * Created by Law on 2017/4/5.
 */

public class HttpError extends Exception  {
    public final NetworkResponse networkResponse;

    public HttpError() {
        networkResponse = null;
    }

    public HttpError(NetworkResponse response) {
        networkResponse = response;
    }

    public HttpError(String exceptionMessage) {
        super(exceptionMessage);
        networkResponse = null;
    }

    public HttpError(String exceptionMessage, Throwable reason) {
        super(exceptionMessage, reason);
        networkResponse = null;
    }

    public HttpError(Throwable cause) {
        super(cause);
        networkResponse = null;
    }
}
