package com.authguidance.mobileweb.app

import android.content.Context
import android.content.Intent
import com.authguidance.mobileweb.configuration.Configuration
import com.authguidance.mobileweb.configuration.ConfigurationLoader
import com.authguidance.mobileweb.plumbing.oauth.AuthenticatorImpl
import com.authguidance.mobileweb.plumbing.oauth.OIDCManager

/*
 * The view model class for the main activity
 */
class MainActivityViewModel {

    // State used by the main activity
    private var isInitialised = false

    // The configuration data
    var configuration: Configuration? = null

    // A helper object that serves as the entry point to OAuth handling
    var oidcManager = OIDCManager()

    /*
     * Load configuration and create global objects
     */
    fun initialise(context: Context) {

        // Reset state flags
        this.isInitialised = false

        // Load configuration and create global objects
        this.configuration = ConfigurationLoader().load(context)
        val authenticator = AuthenticatorImpl(this.configuration!!.oauth, context)
        this.oidcManager.authenticator = authenticator

        // Indicate successful startup
        this.isInitialised = true
    }

    /*
     * Return the logged in state to the view
     */
    fun isLoggedIn(): Boolean {
        return this.oidcManager.authenticator?.isLoggedIn() ?: false
    }

    /*
     * Enable the view to finish a login
     */
    fun finishLogin(data: Intent?) {
        this.oidcManager.finishLogin(data)
    }

    /*
     * Enable the view to finish a logout
     */
    fun finishLogout() {
        this.oidcManager.finishLogout()
    }
}
