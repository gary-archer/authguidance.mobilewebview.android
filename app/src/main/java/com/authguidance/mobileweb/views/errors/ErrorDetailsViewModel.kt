package com.authguidance.mobileweb.views.errors

import com.authguidance.mobileweb.plumbing.errors.UIError

/*
 * A simple view model class for the error details dialog
 */
class ErrorDetailsViewModel(
    val title: String,
    val error: UIError,
    val onDismissCallback: () -> Unit
) {

    /*
     * Android binding requires a member function and does not bind to lambdas
     */
    fun onDismiss() {
        this.onDismissCallback()
    }
}
