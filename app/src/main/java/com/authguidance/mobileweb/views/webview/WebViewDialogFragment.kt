package com.authguidance.mobileweb.views.webview

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.authguidance.mobileweb.R
import com.authguidance.mobileweb.app.MainActivitySharedViewModel
import com.authguidance.mobileweb.databinding.FragmentWebviewBinding
import com.authguidance.mobileweb.plumbing.errors.UIError
import com.authguidance.mobileweb.plumbing.interop.CustomWebChromeClient
import com.authguidance.mobileweb.plumbing.interop.CustomWebViewClient
import com.authguidance.mobileweb.plumbing.interop.JavascriptBridgeImpl

/*
 * The fragment in which the web view is shown
 */
class WebViewDialogFragment(
    private val onError: (UIError) -> Unit,
    private val onClosed: () -> Unit
) : DialogFragment() {

    private lateinit var binding: FragmentWebviewBinding

    /*
     * A factory method to create the dialog in full screen mode
     */
    companion object {

        fun create(onError: (UIError) -> Unit, onClosed: () -> Unit): WebViewDialogFragment {
            val dialog = WebViewDialogFragment(onError, onClosed)
            dialog.setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
            return dialog
        }
    }

    /*
     * Inflate the view
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        this.binding = FragmentWebviewBinding.inflate(inflater, container, false)
        this.binding.model = WebViewDialogViewModel(this::onDialogClose)
        return this.binding.root
    }

    /*
     * Initialise and load the web view
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog!!.window!!.setLayout(width, height)

        // Get the shared view model supplied by the main activity
        val mainViewModel: MainActivitySharedViewModel by activityViewModels()
        val configuration = mainViewModel.configurationAccessor()
        val oidcManager = mainViewModel.oidcManager

        if (configuration != null && oidcManager.authenticator != null) {

            // Make a mobile bridge available to the SPA
            val bridge = JavascriptBridgeImpl(
                this.binding.webview,
                oidcManager,
                this.context as Activity
            )
            this.binding.webview.addJavascriptInterface(bridge, "mobileBridge")

            // Set web view properties to enable interop and debugging
            this.binding.webview.settings.javaScriptEnabled = true
            this.binding.webview.webViewClient = CustomWebViewClient(this::onWebViewLoadError)
            this.binding.webview.webChromeClient = CustomWebChromeClient()

            // Load our SPA's content, which will trigger OAuth calls back to the mobile app later
            val webRootUrl = configuration.app.webBaseUrl
            this.binding.webview.loadUrl(webRootUrl)
        }
    }

    /*
     * Perform the parent and then close the dialog
     */
    private fun onWebViewLoadError(error: UIError) {
        this.onError(error)
        this.onDialogClose()
    }

    /*
     * Close the dialog and inform the opener
     */
    private fun onDialogClose() {
        this.dismiss()
        this.onClosed()
    }

    /*
     * Clean up when destroyed
     */
    override fun onDestroyView() {
        super.onDestroyView()
        this.binding.webview.removeJavascriptInterface("mobileBridge")
    }
}
