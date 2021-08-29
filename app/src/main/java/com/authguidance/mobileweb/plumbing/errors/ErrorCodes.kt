package com.authguidance.mobileweb.plumbing.errors

/*
 * Error codes that the UI can program against
 */
object ErrorCodes {

    // A general exception in the UI
    const val generalUIError = "mobile_ui_error"

    // A problem loading the web view at a URL
    const val loadWebViewFailed = "load_web_view"

    // A problem loading the custom tab at a URL
    const val loadCustomTabFailed = "load_custom_tab"

    // A problem loading the system browser at a URL
    const val loadSystemBrowserFailed = "load_system_browser"

    // Used to indicate that the API cannot be called until the user logs in
    const val loginRequired = "login_required"

    // Used to indicate that the Chrome Custom Tab was cancelled
    const val redirectCancelled = "redirect_cancelled"

    // A technical error starting a login request, such as contacting the metadata endpoint
    const val loginRequestFailed = "login_request_failed"

    // A technical error processing the login response containing the authorization code
    const val loginResponseFailed = "login_response_failed"

    // A technical error exchanging the authorization code for tokens
    const val authorizationCodeGrantFailed = "authorization_code_grant"

    // A technical problem during background token renewal
    const val tokenRenewalError = "token_renewal_error"

    // Indicate when logout is not supported
    const val logoutNotSupported = "logout_not_supported"

    // An error starting a logout request, such as contacting the metadata endpoint
    const val logoutRequestFailed = "logout_request_failed"
}
