package com.authguidance.mobileweb.configuration

/*
 * OAuth configuration settings
 */
class OAuthConfiguration {

    // The authority base URL
    lateinit var authority: String

    // The identifier for our mobile app
    lateinit var clientId: String

    // The base URL for interstitial post login pages
    lateinit var interstitialBaseUrl: String

    // The interstitial page that receives the login response
    lateinit var loginRedirectPath: String

    // The interstitial page that receives the logout response
    lateinit var postLogoutRedirectPath: String

    // OAuth scopes being requested, for use when calling APIs after login
    lateinit var scope: String

    // The Authorization Server endpoint used for logouts
    lateinit var customLogoutEndpoint: String

    // Identity provider specific details might be configured by an install program
    lateinit var idpParameterName: String
    lateinit var idpParameterValue: String
}
