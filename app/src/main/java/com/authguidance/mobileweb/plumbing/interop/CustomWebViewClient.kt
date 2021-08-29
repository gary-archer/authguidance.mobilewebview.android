package com.authguidance.mobileweb.plumbing.interop

import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.authguidance.mobileweb.plumbing.errors.ErrorHandler
import com.authguidance.mobileweb.plumbing.errors.UIError

/*
 * A custom web view client to load our SPA
 */
class CustomWebViewClient(private val onError: (ex: UIError) -> Unit) : WebViewClient() {

    /*
     * Ensure that our SPA content loads within the web view and not in an external browser
     */
    @Override
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        return false
    }

    /*
     * Report error responses from the web view
     */
    @Override
    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {

        super.onReceivedError(view, request, error)
        if (error != null) {

            val details = "Code: ${error.errorCode}, Description ${error.description}"
            val uiError = ErrorHandler().fromWebViewLoadError(request?.url, details)
            this.onError(uiError)
        }
    }
}
