package com.example.vaziliybober.learnersreader.helpers;

import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyWebViewClient extends WebViewClient {


    @Override
    public void onPageFinished(WebView view, String url) {
        if (view.getUrl().contains("about:blank")) {
            view.setVisibility(View.GONE);
        }

        super.onPageFinished(view, url);
    }
}
