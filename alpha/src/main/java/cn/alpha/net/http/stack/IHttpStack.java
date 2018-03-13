package cn.alpha.net.http.stack;

import java.io.IOException;
import java.util.ArrayList;

import cn.alpha.net.http.Request;
import cn.alpha.net.http.URLHttpResponse;
import cn.alpha.net.http.tools.HttpParamsEntry;

/**
 * Http请求端定义
 *
 * Created by Law on 2017/4/5.
 */

public interface IHttpStack {
    /**
     * 让Http请求端去发起一个Request
     *
     * @param request           一次实际请求集合
     * @param additionalHeaders Http请求头
     * @return 一个Http响应
     */
    URLHttpResponse performRequest(Request<?> request, ArrayList<HttpParamsEntry> additionalHeaders)
            throws IOException;
}
