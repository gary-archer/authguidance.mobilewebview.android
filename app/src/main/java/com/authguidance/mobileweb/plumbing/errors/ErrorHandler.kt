package com.authguidance.mobileweb.plumbing.errors

import android.net.Uri
import net.openid.appauth.AuthorizationException

/*
 * A class to manage error translation
 */
@Suppress("TooManyFunctions")
class ErrorHandler {

    /*
     * Return an error object starting the mobile app
     */
    fun fromStartupError(ex: Throwable): UIError {

        val error = UIError(
            "Startup",
            ErrorCodes.generalUIError,
            "A problem was encountered starting the mobile app"
        )

        this.updateFromException(ex, error)
        return error
    }

    /*
     * Return an error object from an exception
     */
    fun fromException(ex: Throwable): UIError {

        // Already handled
        if (ex is UIError) {
            return ex
        }

        val error = UIError(
            "Mobile UI",
            ErrorCodes.generalUIError,
            "A technical problem was encountered in the Mobile UI"
        )

        this.updateFromException(ex, error)
        return error
    }

    /*
     * Used when we cannot open the system browser at a URL
     */
    fun fromSystemBrowserLoadError(ex: Throwable, url: String): UIError {

        val error = UIError(
            "Mobile UI",
            ErrorCodes.loadSystemBrowserFailed,
            "A problem was encountered loading the system browser at $url"
        )
        this.updateFromException(ex, error)
        return error
    }

    /*
     * Used when we cannot load a URL into a custom tab
     */
    fun fromCustomTabLoadError(ex: Throwable, url: String): UIError {

        val error = UIError(
            "Mobile UI",
            ErrorCodes.loadCustomTabFailed,
            "A problem was encountered loading a Chrome Custom Tab for URL: $url"
        )
        this.updateFromException(ex, error)
        return error
    }

    /*
     * Used when we cannot load a URL into a web view
     */
    fun fromWebViewLoadError(url: Uri?, details: String): UIError {

        val error = UIError(
            "Mobile UI",
            ErrorCodes.loadWebViewFailed,
            "A problem was encountered loading a Web View for URL: $url"
        )
        error.details = details
        return error
    }

    /*
     * Return an error to short circuit execution
     */
    fun fromLoginRequired(): UIError {

        return UIError(
            "Login",
            ErrorCodes.loginRequired,
            "A login is required so the API call was aborted"
        )
    }

    /*
     * Return an error to indicate that the Chrome custom tab window was closed
     */
    fun fromRedirectCancelled(): UIError {

        return UIError(
            "Redirect",
            ErrorCodes.redirectCancelled,
            "The login request was cancelled"
        )
    }

    /*
     * Handle errors signing in
     */
    fun fromLoginOperationError(ex: Throwable, errorCode: String): UIError {

        // Already handled
        if (ex is UIError) {
            return ex
        }

        val error = UIError(
            "Login",
            errorCode,
            "A technical problem occurred during login processing"
        )

        if (ex is AuthorizationException) {
            this.updateFromAppAuthException(ex, error)
        } else {
            this.updateFromException(ex, error)
        }

        return error
    }

    /*
     * Return an error to indicate that there is no end session endpoint
     */
    fun fromLogoutNotSupportedError(): UIError {

        return UIError(
            "Logout",
            ErrorCodes.logoutNotSupported,
            "Logout cannot be invoked because there is no end session endpoint"
        )
    }

    /*
     * Return an error to indicate a problem with logout processing
     */
    fun fromLogoutOperationError(ex: Throwable): UIError {

        // Already handled
        if (ex is UIError) {
            return ex
        }

        val error = UIError(
            "Logout",
            ErrorCodes.logoutRequestFailed,
            "A technical problem occurred during logout processing"
        )

        this.updateFromException(ex, error)
        return error
    }

    /*
     * Handle errors from the token endpoint
     */
    fun fromTokenError(ex: Throwable, errorCode: String): UIError {

        // Already handled
        if (ex is UIError) {
            return ex
        }

        val error = UIError(
            "Token",
            errorCode,
            "A technical problem occurred during token processing"
        )

        if (ex is AuthorizationException) {
            this.updateFromAppAuthException(ex, error)
        } else {
            this.updateFromException(ex, error)
        }

        return error
    }

    /*
     * Get details from the underlying exception
     */
    private fun updateFromException(ex: Throwable, error: UIError) {

        error.details = this.getErrorDescription(ex)
        error.stackTrace = ex.stackTrace
    }

    /*
     * Add AppAuth details to our standard error object
     */
    private fun updateFromAppAuthException(ex: AuthorizationException, error: UIError) {

        if (ex.code != 0) {

            val appAuthErrorType = when {
                ex.type == 1 -> {
                    "AUTHORIZATION"
                }
                ex.type == 2 -> {
                    "TOKEN"
                }
                else -> {
                    "GENERAL"
                }
            }

            error.appAuthCode = "$appAuthErrorType / ${ex.code}"
        }

        this.updateFromException(ex, error)
    }

    /*
     * Get the error message property's value
     */
    private fun getErrorDescription(ex: Throwable): String? {

        if (ex.message != null) {
            return ex.message
        } else {
            return ex.javaClass.simpleName
        }
    }
}
