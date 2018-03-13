package cn.alpha.demo.net.http;

import android.os.Bundle;
import android.support.annotation.Nullable;

import cn.alpha.demo.R;
import cn.alpha.demo.base.AppBaseCompatActivity;
import cn.alpha.net.http.Http;
import cn.alpha.net.http.HttpCallback;
import cn.alpha.net.http.client.HttpParams;
import cn.alpha.utils.Logger;

/**
 * Created by Law on 2017/4/16.
 */

public class HttpActivity extends AppBaseCompatActivity {
    public static final String BAIDU = "https://www.baidu.com";
    public static final String HAO123 = "http://www.chsi.com.cn/";
    public static final String MINE = "http://192.168.1.117:3000/weather";

    @Override
    public int setContentViewLayout() {
        return R.layout.activity_http;
    }

    @Override
    public void loadData(@Nullable Bundle savedInstanceState) {
//        Http.get("BAIDU", BAIDU, new HttpCallback() {
//            @Override
//            public void onSuccess(String t) {
//                super.onSuccess(t);
//                Logger.i("alpha-http", t);
//            }
//
//            @Override
//            public void onFailure(int errorNo, String strMsg) {
//                super.onFailure(errorNo, strMsg);
//                Logger.i("alpha-http", strMsg);
//            }
//        });
        HttpParams params = new HttpParams();
        Logger.i("alpha-http", "loadData");
        Http.get("mine", MINE, params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                Logger.i("alpha-http", t);
            }
        });
    }
}
