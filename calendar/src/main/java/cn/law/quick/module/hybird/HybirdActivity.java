package cn.law.quick.module.hybird;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import butterknife.BindView;
import cn.alpha.hybird.BridgeWebView;
import cn.alpha.hybird.DefaultHandler;
import cn.alpha.utils.Logger;
import cn.law.quick.R;
import cn.law.quick.base.AppBaseCompatActivity;

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
        setting();
        mBridgeWebView.setInitialScale(100);
        mBridgeWebView.setDefaultHandler(new DefaultHandler());
        mBridgeWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
//                super.onProgressChanged(view, newProgress);
                Logger.e(newProgress);
            }
        });
        mBridgeWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.i("TAG", "URL：" + url);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                FileInputStream input;

//                String url = request.getUrl().toString();

                String key = "http://androidimg";
                String filekey = "file://";
                /*如果请求包含约定的字段 说明是要拿本地的图片*/
                if (url.contains(key)) {
                    String imgPath = url.replace(key, "");
                    Log.i("TAG", "本地图片路径：" + imgPath.trim());
                    try {
                        /*重新构造WebResourceResponse  将数据已流的方式传入*/
                        input = new FileInputStream(new File(imgPath.trim()));
                        WebResourceResponse response = new WebResourceResponse("image/jpg", "UTF-8", input);

                        /*返回WebResourceResponse*/
                        return response;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else if (url.startsWith(filekey)) {
                    String imgPath = url.replace(filekey, "");
                    try {
                        /*重新构造WebResourceResponse  将数据已流的方式传入*/
                        input = new FileInputStream(new File(imgPath.trim()));
                        WebResourceResponse response = new WebResourceResponse("image/jpg", "UTF-8", input);

                        /*返回WebResourceResponse*/
                        return response;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                return super.shouldInterceptRequest(view, url);
            }

            /*@Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                FileInputStream input;

                String url = request.getUrl().toString();

                String key = "http://androidimg";
                String filekey = "file://";
                *//*如果请求包含约定的字段 说明是要拿本地的图片*//*
                if (url.contains(key)) {
                    String imgPath = url.replace(key, "");
                    Log.i("TAG", "本地图片路径：" + imgPath.trim());
                    try {
                        *//*重新构造WebResourceResponse  将数据已流的方式传入*//*
                        input = new FileInputStream(new File(imgPath.trim()));
                        WebResourceResponse response = new WebResourceResponse("image/jpg", "UTF-8", input);

                        *//*返回WebResourceResponse*//*
                        return response;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else if (url.startsWith(filekey)) {
                    String imgPath = url.replace(filekey, "");
                    try {
                        *//*重新构造WebResourceResponse  将数据已流的方式传入*//*
                        input = new FileInputStream(new File(imgPath.trim()));
                        WebResourceResponse response = new WebResourceResponse("image/jpg", "UTF-8", input);

                        *//*返回WebResourceResponse*//*
                        return response;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                return super.shouldInterceptRequest(view, request);
            }*/
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
                if (TextUtils.isEmpty(url)) {
                    return;
                }
                mBridgeWebView.loadUrl(url);
                break;
            case R.id.btn_back:
                if (mBridgeWebView.canGoBack()) {
                    mBridgeWebView.goBack();
                }
                break;
        }
    }

    private void setting() {
        WebSettings settings = mBridgeWebView.getSettings();
        settings.setSupportZoom(false);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        Log.i("TAG", settings.getAllowFileAccess() + "");
        Log.i("TAG", settings.getAllowContentAccess() + "");
        Log.i("TAG", settings.getAllowFileAccessFromFileURLs() + "");
        Log.i("TAG", settings.getAllowUniversalAccessFromFileURLs() + "");
    }
}
