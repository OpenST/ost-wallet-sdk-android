/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.ostsdkui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.CookieHandler;
import java.net.HttpCookie;
import java.util.List;
import java.util.Locale;

import ost.com.ostsdkui.uicomponents.AppBar;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class WebViewFragment extends BaseFragment {

    private WebView mWebView;
    private WebChromeClient webChromeClient;
    private WebViewClient webViewClient;
    private String mUrl;
    private String mAppBarTitle = null;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WebViewFragment() {
    }


    public static WebViewFragment newInstance(String url) {
        WebViewFragment fragment = new WebViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.mUrl = url;
        return fragment;
    }

    public static WebViewFragment newInstance(String url, String title) {
        WebViewFragment fragment = new WebViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.mUrl = url;
        fragment.mAppBarTitle = title;
        return fragment;
    }

    @Override
    protected void onCreateViewDelegate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateViewDelegate(inflater, container, savedInstanceState);
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_web_view, container, true);

        AppBar appBar = AppBar.newInstance(getContext(), false);
        setUpAppBar(view, appBar);

        mWebView = (WebView) view.findViewById(R.id.activity_main_webview);

        webChromeClient = new WebChromeClient();
        webViewClient = new WebViewClient();

        loadWebView();
    }

    private void loadWebView() {
        mWebView.setWebViewClient(webViewClient);
        mWebView.setWebChromeClient(webChromeClient);
        mWebView.getSettings().setJavaScriptEnabled(true);
        java.net.CookieManager cookiemanager = (java.net.CookieManager) CookieHandler.getDefault();
        List<HttpCookie> cookies = cookiemanager.getCookieStore().getCookies();
        CookieManager cookieManager = CookieManager.getInstance();
        for (HttpCookie eachCookie : cookies) {
            cookieManager.setCookie(eachCookie.getDomain(), String.format(Locale.US,"%s=%s", eachCookie.getName(), eachCookie.getValue()));
        }
        mWebView.loadUrl(mUrl);
    }
}