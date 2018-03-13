package cn.law.calendar.module.hybird;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;

import butterknife.BindView;
import cn.alpha.hybird.BridgeWebView;
import cn.alpha.hybird.DefaultHandler;
import cn.alpha.utils.Logger;
import cn.law.calendar.R;
import cn.law.calendar.base.AppBaseCompatActivity;

/**
 * Created by Jungle on 2017/10/7.
 */

public class HybirdActivity extends AppBaseCompatActivity implements View.OnClickListener {
    public static final String APP = "http://192.168.1.117:3000/";
    public static final String WEB = "http://192.168.1.117:3001/";
    public static final String MGR = "http://192.168.1.117:3002/";
    public static final String WX = "http://192.168.1.117:3003/";

    @BindView(R.id.webview)
    BridgeWebView mBridgeWebView;
    @BindView(R.id.et_url)
    EditText mEtUrl;

    @Override
    public int setContentViewLayout() {
        return R.layout.activity_hybird;
    }

    @Override
    public void loadData(@Nullable Bundle savedInstanceState) {
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_app:
                mBridgeWebView.loadUrl(APP);
                break;
            case R.id.btn_web:
                mBridgeWebView.loadUrl(WEB);
                break;
            case R.id.btn_mgr:
                mBridgeWebView.loadUrl(MGR);
                break;
            case R.id.btn_wx:
                mBridgeWebView.loadUrl(WX);
                break;
            case R.id.btn_go:
                String url = mEtUrl.getText().toString();
                if(TextUtils.isEmpty(url)){
                    return;
                }
                mBridgeWebView.loadUrl(url);
                break;
        }
    }

}
