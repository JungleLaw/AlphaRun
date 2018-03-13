package cn.alpha.net.http.client;

import cn.alpha.net.http.DefaultRetryPolicy;
import cn.alpha.net.http.RetryPolicy;

/**
 * Created by Law on 2017/4/5.
 */

public class RequestConfig {
    public int mTimeout = 0; // 请求超时时间

    public int mDelayTime = 0; // 为了更真实的模拟网络,缓存延迟响应

    public int mCacheTime = 5; //缓存时间(分钟)

    public boolean mUseServerControl; //服务器控制缓存时间,为true时mCacheTime无效

    public int mMethod; // 请求方式

    public Boolean mShouldCache = null; // 是否缓存本次请求,默认为智能模式,get缓存post不缓存

    public String mUrl; //请求接口地址

    public RetryPolicy mRetryPolicy = new DefaultRetryPolicy(); //重试策略

    public String mEncoding = "UTF-8"; //编码

    public Object mTag; //每个request可以设置一个标志

//    public final Subject<Result, Result> mSubject = new SerializedSubject<>(PublishSubject.<Result>create());

}
