package com.mmnn.zoo.activity.web;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.mmnn.zoo.R;
import com.mmnn.zoo.utils.NetworkUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;

@SuppressLint({ "SetJavaScriptEnabled", "InflateParams" })
public class BrowserFragment extends Fragment {

    private String mUrl;
    private String mTitle;
    private HashMap<String, String> mUrlTitleMaps = new HashMap<>();
    private ViewGroup mContainer;
    private WebView mWebView;
    private ProgressBar mProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = savedInstanceState == null ? getArguments() : savedInstanceState;
        if (b != null) {
            mUrl = b.getString("url");
            mTitle = b.getString("title");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_browser, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View v) {
        mContainer = (ViewGroup) v.findViewById(R.id.container);
        mWebView = (WebView) v.findViewById(R.id.web_view);
        mProgressBar = (ProgressBar) v.findViewById(R.id.pgb);

        if (!TextUtils.isEmpty(mTitle)) {
            getActivity().setTitle(mTitle);
        }

        mWebView.requestFocus();
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setVerticalScrollBarEnabled(false);

        WebSettings ws = mWebView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setUseWideViewPort(true);
        ws.setLoadWithOverviewMode(true);

        mWebView.clearCache(true);
        mWebView.clearHistory();
        if (NetworkUtils.isConnected(getActivity().getApplicationContext())) {
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

        mWebView.setWebViewClient(new BrowserWebViewClient(this));
        mWebView.setWebChromeClient(new BrowserWebChromeClient(this));
        mWebView.loadUrl(mUrl);
    }

    public boolean canGoBack() {
        return mWebView == null || mWebView.canGoBack();
    }

    public void goBack() {
        if (mWebView != null) {
            mWebView.goBack();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mContainer.removeAllViews();
        mWebView.removeAllViews();
        mWebView.destroy();
        mWebView = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("url", mUrl);
        outState.putString("title", mTitle);
        super.onSaveInstanceState(outState);
    }

    private static class BrowserWebViewClient extends WebViewClient {

        protected WeakReference<BrowserFragment> mReference;

        public BrowserWebViewClient(BrowserFragment frag) {
            mReference = new WeakReference<>(frag);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            BrowserFragment frag = mReference.get();
            if (frag != null) {
                String title = frag.mUrlTitleMaps.get(url);
                frag.getActivity().setTitle(title);
            }
        }
    }

    private static class BrowserWebChromeClient extends WebChromeClient {
        protected WeakReference<BrowserFragment> mReference;

        public BrowserWebChromeClient(BrowserFragment frag) {
            mReference = new WeakReference<>(frag);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            BrowserFragment frag = mReference.get();
            if (frag == null) {
                return;
            }

            frag.mProgressBar.setProgress(newProgress);
            frag.mProgressBar.postInvalidate();
            if (newProgress == 100) {
                frag.mProgressBar.setVisibility(View.GONE);
            } else {
                frag.mProgressBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            BrowserFragment frag = mReference.get();
            if (frag != null) {
                frag.mUrlTitleMaps.put(view.getUrl(), title);
                frag.getActivity().setTitle(title);
            }
        }
    }

}
