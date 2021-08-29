package com.authguidance.mobileweb.views.menu

import androidx.databinding.BaseObservable

/*
 * A simple view model class for the menu fragment
 */
class MenuViewModel(
    private val onLogin: () -> Unit,
    private val onOpenSystemBrowser: () -> Unit,
    private val onOpenInAppBrowser: () -> Unit,
    private val onShowWebViewDialog: () -> Unit,
    private val onLogout: () -> Unit

) : BaseObservable() {

    // The layout file binds the enabled state to this property
    var isLoggedIn: Boolean = false

    /*
     * First ensure that we are logged in to the mobile app
     */
    fun onInvokeLogin() {
        this.onLogin()
    }

    /*
     * Alternatively invoke the SPA in the system browser
     */
    fun onInvokeSystemBrowser() {
        this.onOpenSystemBrowser()
    }

    /*
     * Alternatively invoke the SPA in a Custom Tab
     */
    fun onInvokeInAppBrowser() {
        this.onOpenInAppBrowser()
    }

    /*
     * Invoke the SPA in a web view running in a modal dialog
     */
    fun onInvokeWebView() {
        this.onShowWebViewDialog()
    }

    /*
     * Log out of the mobile app when required
     */
    fun onInvokeLogout() {
        this.onLogout()
    }

    /*
     * Force the UI to re-render menu buttons if the logged in state has changed
     */
    fun updateLoggedInState(loggedIn: Boolean) {
        this.isLoggedIn = loggedIn
        this.notifyChange()
    }
}
