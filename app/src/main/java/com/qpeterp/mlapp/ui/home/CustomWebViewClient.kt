package com.qpeterp.mlapp.ui.home

import android.webkit.WebView
import android.webkit.WebViewClient

class CustomWebViewClient : WebViewClient() {
    @Deprecated("Deprecated in Java")
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        if (url != null && url.startsWith("https")) {
            return true
        }
        return false
    }
}