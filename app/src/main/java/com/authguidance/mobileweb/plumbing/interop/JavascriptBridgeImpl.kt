package com.authguidance.mobileweb.plumbing.interop

import android.app.Activity
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.authguidance.mobileweb.plumbing.errors.ErrorHandler
import com.authguidance.mobileweb.plumbing.oauth.OIDCManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*
 * Mobile entry points called from Javascript in our SPA
 */
class JavascriptBridgeImpl(
    private val webView: WebView,
    private val oidcManager: OIDCManager,
    private val activity: Activity
) : JavascriptBridge {

    /*
     * Handle SPA requests for the logged in state
     */
    @JavascriptInterface
    override fun isLoggedIn(callbackName: String) {

        val that = this@JavascriptBridgeImpl
        CoroutineScope(Dispatchers.Main).launch {

            try {
                // Do the mobile work
                that.successResult(callbackName, that.oidcManager.authenticator!!.isLoggedIn().toString())

            } catch (ex: Throwable) {

                // Call back the SPA with an error response
                that.errorResult(callbackName, ex)
            }
        }
    }

    /*
     * Handle SPA requests to get an access token
     */
    @JavascriptInterface
    override fun getAccessToken(callbackName: String) {

        val that = this@JavascriptBridgeImpl
        CoroutineScope(Dispatchers.IO).launch {

            try {
                // Do the mobile work
                val accessToken = that.oidcManager.authenticator!!.getAccessToken()

                // Call back the SPA with an access token
                withContext(Dispatchers.Main) {
                    that.successResult(callbackName, accessToken)
                }

            } catch (ex: Throwable) {

                // Call back the SPA with an error response
                withContext(Dispatchers.Main) {
                    that.errorResult(callbackName, ex)
                }
            }
        }
    }

    /*
     * Handle SPA requests to refresh an access token
     */
    @JavascriptInterface
    override fun refreshAccessToken(callbackName: String) {

        val that = this@JavascriptBridgeImpl
        CoroutineScope(Dispatchers.IO).launch {

            try {
                // Do the mobile work
                val accessToken = that.oidcManager.authenticator!!.refreshAccessToken()

                // Call back the SPA with an access token
                withContext(Dispatchers.Main) {
                    that.successResult(callbackName, accessToken)
                }

            } catch (ex: Throwable) {

                // Call back the SPA with an error response
                withContext(Dispatchers.Main) {
                    that.errorResult(callbackName, ex)
                }
            }
        }
    }

    /*
     * Handle SPA requests to trigger a login redirect
     */
    @JavascriptInterface
    override fun login(callbackName: String) {

        val onSuccess = {
            this.successResult(callbackName, "")
        }

        val onError = { ex: Throwable ->
            this.errorResult(callbackName, ex)
        }

        // Start the redirect
        this.oidcManager.startLogin(this.activity, onSuccess, onError)
    }

    /*
     * Handle SPA requests to trigger a logout redirect
     */
    @JavascriptInterface
    override fun logout(callbackName: String) {

        val onSuccess = {
            this.successResult(callbackName, "")
        }

        val onError = { ex: Throwable ->
            this.errorResult(callbackName, ex)
        }

        // Start the redirect
        this.oidcManager.startLogout(this.activity, onSuccess, onError)
    }

    /*
     * Handle SPA requests to expire the access token
     */
    @JavascriptInterface
    override fun expireAccessToken(callbackName: String) {

        val that = this@JavascriptBridgeImpl
        CoroutineScope(Dispatchers.Main).launch {

            try {
                // Do the mobile work
                that.oidcManager.authenticator!!.expireAccessToken()
                that.successResult(callbackName, "")

            } catch (ex: Throwable) {

                // Call back the SPA with an error response
                withContext(Dispatchers.Main) {
                    that.errorResult(callbackName, ex)
                }
            }
        }
    }

    /*
     * Handle SPA requests to expire the refresh token
     */
    @JavascriptInterface
    override fun expireRefreshToken(callbackName: String) {

        val that = this@JavascriptBridgeImpl
        CoroutineScope(Dispatchers.Main).launch {

            try {
                // Do the mobile work
                that.oidcManager.authenticator!!.expireRefreshToken()
                that.successResult(callbackName, "")

            } catch (ex: Throwable) {

                // Call back the SPA with an error response
                withContext(Dispatchers.Main) {
                    that.errorResult(callbackName, ex)
                }
            }
        }
    }

    /*
     * Return a success result back to the SPA
     */
    private fun successResult(callbackName: String, data: String) {
        webView.loadUrl("javascript: window['$callbackName']('$data', null)")
    }

    /*
     * Pass an error result back to the SPA as a JSON object
     */
    private fun errorResult(callbackName: String, ex: Throwable) {

        val errorJson = ErrorHandler().fromException(ex).toJson()
        webView.loadUrl("javascript: window['$callbackName'](null, '$errorJson')")
    }
}
