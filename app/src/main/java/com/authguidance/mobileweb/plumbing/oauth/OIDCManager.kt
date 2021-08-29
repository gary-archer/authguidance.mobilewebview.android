package com.authguidance.mobileweb.plumbing.oauth

import android.app.Activity
import android.content.Intent
import com.authguidance.mobileweb.views.utilities.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*
 * An entry point class for OAuth processing, shared between native mobile and webview processing
 */
class OIDCManager {

    var isTopMost: Boolean = true
    var authenticator: Authenticator? = null
    private var onSuccess: () -> Unit = {}
    private var onError: (Throwable) -> Unit = {}

    /*
     * Start a login operation
     */
    fun startLogin(activity: Activity, onSuccess: () -> Unit, onError: (Throwable) -> Unit) {

        // Prevent re-entrancy
        if (!this.isTopMost) {
            return
        }
        this.isTopMost = false

        // Update state
        this.onSuccess = onSuccess
        this.onError = onError

        // Run on the UI thread since we present UI elements
        CoroutineScope(Dispatchers.Main).launch {

            val that = this@OIDCManager
            try {

                // Start the redirect
                that.authenticator!!.startLogin(activity, Constants.LOGIN_REDIRECT_REQUEST_CODE)

            } catch (ex: Throwable) {

                // Report errors such as those looking up endpoints
                that.onError(ex)

                // Reset state
                that.onSuccess = {}
                that.onError = {}
            }
        }
    }

    /*
     * After the post login page executes, we receive the login response here
     */
    fun finishLogin(responseIntent: Intent?) {

        if (responseIntent == null) {
            this.onSuccess = {}
            this.onError = {}
            this.isTopMost = true
            return
        }

        // Switch to a background thread to perform the code exchange
        CoroutineScope(Dispatchers.IO).launch {

            val that = this@OIDCManager
            try {
                // Handle completion after login success, which will exchange the authorization code for tokens
                that.authenticator!!.finishLogin(responseIntent)

                // Indicate success on the main thread
                withContext(Dispatchers.Main) {
                    that.onSuccess()
                }

            } catch (ex: Throwable) {

                // Report errors on the main thread
                withContext(Dispatchers.Main) {
                    that.onError(ex)
                }

            } finally {

                // Always reset state to allow retries
                that.isTopMost = true
                withContext(Dispatchers.Main) {
                    that.onSuccess = {}
                    that.onError = {}
                }
            }
        }
    }

    /*
     * Start a logout redirect
     */
    fun startLogout(activity: Activity, onSuccess: () -> Unit, onError: (Throwable) -> Unit) {

        // Prevent re-entrancy
        if (!this.isTopMost) {
            return
        }
        this.isTopMost = false

        // Update state
        this.onSuccess = onSuccess
        this.onError = onError

        // Run on the UI thread since we present UI elements
        CoroutineScope(Dispatchers.Main).launch {

            val that = this@OIDCManager
            try {

                // Trigger the logout process, which will remove tokens and redirect to clear the OAuth session cookie
                that.authenticator!!.startLogout(activity, Constants.LOGOUT_REDIRECT_REQUEST_CODE)

            } catch (ex: Throwable) {

                // Report errors such as those looking up endpoints
                that.onError(ex)

                // Reset state
                that.isTopMost = true
                that.onSuccess = {}
                that.onError = {}
            }
        }
    }

    /*
     * Update state when a logout completes
     */
    fun finishLogout() {

        // Finalise
        this.authenticator!!.finishLogout()
        this.onSuccess()

        // Reset our own state
        this.isTopMost = true
        this.onSuccess = {}
        this.onError = {}
    }
}
