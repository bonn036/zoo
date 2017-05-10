package com.mmnn.zoo.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.mmnn.zoo.R;

import java.net.URL;

public class WebViewActivity extends Activity {

    private static final String TAG = "WebViewActivity";
    private WebView mWebView;
    private ProgressBar mWebLoading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String webUrl = "http://jbossews-mmnn.rhcloud.com";
        try {
            URL url = new URL(webUrl);
            final StringBuilder sb = new StringBuilder();
            sb.append(webUrl);
            if (url.getQuery() == null) {
                sb.append("?");
            }
//            sb.append("&source=").append(URLEncoder.encode(SystemUtils.getInstance().getEncryptImei(), "UTF-8"));
//            sb.append("&deviceid=").append(URLEncoder.encode(new DeviceUuidFactory(getApplicationContext()).getUuid(), "UTF-8"));
            webUrl = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
            return;
        }

        setContentView(R.layout.activity_webview);


        mWebView = (WebView) findViewById(R.id.web_view);
        mWebLoading = (ProgressBar) findViewById(R.id.web_loading);

        WebSettings settings = mWebView.getSettings();
        settings.setDatabaseEnabled(true);
        if (Build.VERSION.SDK_INT < 19) {
            settings.setDatabasePath("/data/data/" + mWebView.getContext().getPackageName() + "/databases/");
        }
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setDomStorageEnabled(true);
        settings.setBlockNetworkLoads(false);
        settings.setBlockNetworkImage(false);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                mWebLoading.setProgress(progress);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });

        mWebView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
        mWebView.loadUrl(webUrl);
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        }
        super.onBackPressed();
    }
}
