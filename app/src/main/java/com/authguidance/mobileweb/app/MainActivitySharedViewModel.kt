package com.authguidance.mobileweb.app

import androidx.lifecycle.ViewModel
import com.authguidance.mobileweb.configuration.Configuration
import com.authguidance.mobileweb.plumbing.oauth.OIDCManager

/*
 * Details from the main activity that are shared with child fragments
 * This is done by the Android system using 'by viewModels()' and 'by activityViewModels()' calls
 */
class MainActivitySharedViewModel : ViewModel() {

    // When first created this will return null
    lateinit var configurationAccessor: () -> Configuration?

    // This simplifies calls to login and logout from the menu view
    lateinit var oidcManager: OIDCManager

    // Error callbacks
    lateinit var onError: (ex: Throwable) -> Unit
    lateinit var onResetError: () -> Unit
}
