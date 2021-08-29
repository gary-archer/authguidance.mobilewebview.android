package com.authguidance.mobileweb.views.menu

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.activityViewModels
import com.authguidance.mobileweb.app.MainActivitySharedViewModel
import com.authguidance.mobileweb.configuration.Configuration
import com.authguidance.mobileweb.databinding.FragmentMenuBinding
import com.authguidance.mobileweb.plumbing.errors.ErrorHandler
import com.authguidance.mobileweb.plumbing.events.InitialLoadEvent
import com.authguidance.mobileweb.plumbing.oauth.OIDCManager
import com.authguidance.mobileweb.views.webview.WebViewDialogFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/*
 * The menu fragment shows operations for invoking secured web content
 */
class MenuFragment : androidx.fragment.app.Fragment() {

    private lateinit var binding: FragmentMenuBinding

    // Local members used for UI operations
    private lateinit var oidcManager: OIDCManager
    private lateinit var configurationAccessor: () -> Configuration?
    private lateinit var handleError: (ex: Throwable) -> Unit
    private lateinit var resetError: () -> Unit

    /*
     * Initialise the view
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        this.binding = FragmentMenuBinding.inflate(inflater, container, false)

        // Get details that the main activity supplies to child views
        val mainViewModel: MainActivitySharedViewModel by activityViewModels()
        this.oidcManager = mainViewModel.oidcManager
        this.configurationAccessor = mainViewModel.configurationAccessor
        this.handleError = mainViewModel.onError
        this.resetError = mainViewModel.onResetError

        // Create this fragment's view model
        this.binding.model = MenuViewModel(
            this::login,
            this::openSystemBrowser,
            this::openInAppBrowser,
            this::openWebViewDialog,
            this::logout
        )

        return this.binding.root
    }

    /*
     * View initialization
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Subscribe to the initial load event
        EventBus.getDefault().register(this)
    }

    /*
     * Handle the initial load event to get the logged in state
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: InitialLoadEvent) {
        this.binding.model!!.updateLoggedInState(event.loggedIn)
    }

    /*
     * Perform the login redirect
     */
    private fun login() {

        val onSuccess = {
            this.resetError()
            this.binding.model!!.updateLoggedInState(true)
        }

        this.oidcManager.startLogin(this.activity as Activity, onSuccess, this.handleError)
    }

    /*
     * Perform the logout redirect
     */
    private fun logout() {

        val onSuccess = {
            this.resetError()
            this.binding.model!!.updateLoggedInState(false)
        }

        this.oidcManager.startLogout(this.activity as Activity, onSuccess, this.handleError)
    }

    /*
     * Invoke the SPA in the system browser, which requires a second redirect
     * This relies on Single Sign On cookies shared between the 2 apps and runs via an external browser
     */
    private fun openSystemBrowser() {

        this.resetError()
        val intent = Intent(Intent.ACTION_VIEW)
        val configuration = this.configurationAccessor()!!

        try {

            // Try to open the SPA's HTTPS URL in the system browser
            intent.data = Uri.parse(configuration.app.webBaseUrl)
            this.startActivity(intent)

        } catch (ex: Throwable) {

            // Handle errors
            val uiError = ErrorHandler().fromSystemBrowserLoadError(ex, configuration.app.webBaseUrl)
            this.handleError(uiError)
        }
    }

    /*
     * Invoke the SPA in a Chrome Custom Tab, which requires a second redirect
     * This relies on Single Sign On cookies shared between the 2 apps and runs via an integrated browser
     */
    private fun openInAppBrowser() {

        this.resetError()
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        val configuration = this.configurationAccessor()!!

        try {

            // Try to open the SPA's HTTPS URL in the system browser
            customTabsIntent.launchUrl(this.context as Activity, Uri.parse(configuration.app.webBaseUrl))

        } catch (ex: Throwable) {

            // Handle errors
            val uiError = ErrorHandler().fromCustomTabLoadError(ex, configuration.app.webBaseUrl)
            this.handleError(uiError)
        }
    }

    /*
     * Invoke the SPA in a web view shown in a modal dialog
     * The SPA will call back the mobile host to perform OAuth redirects and to get tokens
     */
    private fun openWebViewDialog() {

        this.resetError()
        val onDialogClosed = {
            this.binding.model!!.updateLoggedInState(
                this.oidcManager.authenticator?.isLoggedIn() ?: false
            )
        }

        val dialog = WebViewDialogFragment.create(this.handleError, onDialogClosed)
        dialog.show(this.childFragmentManager, "")
    }
}
