package com.authguidance.mobileweb.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.authguidance.mobileweb.R
import com.authguidance.mobileweb.databinding.ActivityMainBinding
import com.authguidance.mobileweb.plumbing.errors.ErrorCodes
import com.authguidance.mobileweb.plumbing.errors.ErrorHandler
import com.authguidance.mobileweb.plumbing.events.InitialLoadEvent
import com.authguidance.mobileweb.views.errors.ErrorSummaryFragment
import com.authguidance.mobileweb.views.utilities.Constants
import org.greenrobot.eventbus.EventBus

/*
 * Our Single Activity App's activity
 */
class MainActivity : AppCompatActivity() {

    // The binding contains our view model
    private lateinit var binding: ActivityMainBinding

    /*
     * Create the activity in a safe manner, to set up navigation and data binding
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create our view model
        val model = MainActivityViewModel()

        // Populate the shared view model used by child fragments
        this.createSharedViewModel(model)

        // Inflate the view, which will trigger child fragments to run
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        this.binding.model = model

        // Run the application startup logic, to load configuration
        this.initialiseApp()
    }

    /*
     * Create or update a view model with data needed by child fragments
     */
    private fun createSharedViewModel(model: MainActivityViewModel) {

        // Get the model from the Android system, which will be created the first time
        val sharedViewModel: MainActivitySharedViewModel by viewModels()

        // Provide properties we need to expose to fragments
        sharedViewModel.configurationAccessor = model::configuration
        sharedViewModel.oidcManager = model.oidcManager
        sharedViewModel.onError = this::handleError
        sharedViewModel.onResetError = this::resetError
    }

    /*
     * Try to initialise the app
     */
    private fun initialiseApp() {

        try {
            // Load configuration and create global objects
            val model = this.binding.model!!
            model.initialise(this.applicationContext)

            // Send an initial load event to inform views of the logged in state
            EventBus.getDefault().post(InitialLoadEvent(model.isLoggedIn()))

        } catch (ex: Throwable) {

            // Report any startup errors
            val error = ErrorHandler().fromStartupError(ex)
            this.handleError(error)
        }
    }

    /*
     * Handle the result from other activities, such as AppAuth or lock screen activities
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        // Handle login responses
        if (requestCode == Constants.LOGIN_REDIRECT_REQUEST_CODE) {
            this.binding.model!!.finishLogin(data)
        }

        // Handle logout responses and reset state
        else if (requestCode == Constants.LOGOUT_REDIRECT_REQUEST_CODE) {
            this.binding.model!!.finishLogout()
        }
    }

    /*
     * Receive unhandled exceptions and render the error fragment
     */
    private fun handleError(ex: Throwable) {

        // Get the error as a known object and ignore expected errors
        val uiError = ErrorHandler().fromException(ex)
        if (uiError.errorCode.equals(ErrorCodes.redirectCancelled)) {
            return
        }

        // Display summary details that can be clicked to invoke a dialog
        val errorFragment =
            this.supportFragmentManager.findFragmentById(R.id.main_error_summary_fragment) as ErrorSummaryFragment
        errorFragment.reportError(
            this.getString(R.string.main_error_hyperlink),
            this.getString(R.string.main_error_dialogtitle),
            uiError
        )
    }

    /*
     * Reset errors at the start of each new menu operation
     */
    private fun resetError() {

        val errorFragment =
            this.supportFragmentManager.findFragmentById(R.id.main_error_summary_fragment) as ErrorSummaryFragment
        errorFragment.clearError()
    }
}
