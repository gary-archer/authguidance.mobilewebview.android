package com.authguidance.mobileweb.views.webview

/*
 * A simple view model class for the web view dialog
 */
class WebViewDialogViewModel(
    val onDismissCallback: () -> Unit
) {

    /*
     * Android binding requires a member function and does not bind to lambdas
     */
    fun onDismiss() {
        this.onDismissCallback()
    }
}
