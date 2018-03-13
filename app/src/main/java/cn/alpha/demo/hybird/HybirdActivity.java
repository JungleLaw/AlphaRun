package cn.alpha.demo.hybird;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import butterknife.BindView;
import cn.alpha.demo.R;
import cn.alpha.demo.base.AppBaseCompatActivity;
import cn.alpha.demo.jni.Host;
import cn.alpha.hybird.BridgeWebView;
import cn.alpha.hybird.DefaultHandler;
import cn.alpha.utils.Logger;

/**
 * Created by Jungle on 2017/10/7.
 */

public class HybirdActivity extends AppBaseCompatActivity {
    public static final String URL = "http://localhost:3000/";
    @BindView(R.id.webview)
    BridgeWebView mBridgeWebView;

    @Override
    public int setContentViewLayout() {
        return R.layout.activity_hybird;
    }

    @Override
    public void loadData(@Nullable Bundle savedInstanceState) {
        Logger.e(Host.URL_SERVER_PREFIX);
        WebSettings settings = mBridgeWebView.getSettings();
        settings.setSupportZoom(false);
        mBridgeWebView.setInitialScale(100);
        mBridgeWebView.setDefaultHandler(new DefaultHandler());
        mBridgeWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
//                super.onProgressChanged(view, newProgress);
                Logger.e(newProgress);
            }
        });
        mBridgeWebView.loadUrl(Host.URL_SERVER_PREFIX);
//        mBridgeWebView.loadUrl(Host.URL_IMG_PREFIX + "imgs/gifasd.gif");
    }
}
