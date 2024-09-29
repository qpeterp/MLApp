package com.qpeterp.mlapp.ui.home

import android.webkit.WebView
import android.webkit.WebViewClient

class CustomWebViewClient : WebViewClient() {
    @Deprecated("Deprecated in Java")
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        if (url != null && url.startsWith("https://learn.microsoft.com/en-us/dotnet/architecture/maui/mvvm#the-mvvm-pattern")) {
            return true
        }
        return false
    }
}