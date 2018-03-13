package cn.alpha.net.http;

/**
 * Created by Law on 2017/4/5.
 */

public interface Network {
    NetworkResponse performRequest(Request<?> request) throws HttpError;
}
