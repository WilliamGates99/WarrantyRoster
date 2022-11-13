package com.xeniac.warrantyroster_manager.utils

import android.content.Context
import android.view.View
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.xeniac.warrantyroster_manager.R

object SnackBarHelper {

    fun showNormalSnackbarError(view: View, message: String): Snackbar = Snackbar.make(
        view, message, LENGTH_LONG
    ).apply { show() }

    fun showActionSnackbarError(
        view: View,
        message: String,
        actionBtn: String,
        action: () -> Unit
    ): Snackbar = Snackbar.make(
        view, message, LENGTH_INDEFINITE
    ).apply {
        setAction(actionBtn) { action() }
        show()
    }

    fun showNetworkConnectionError(
        context: Context,
        view: View,
        action: () -> Unit
    ): Snackbar = Snackbar.make(
        view,
        context.getString(R.string.error_network_connection),
        LENGTH_INDEFINITE
    ).apply {
        setAction(context.getString(R.string.error_btn_retry)) { action() }
        show()
    }

    fun show403Error(context: Context, view: View): Snackbar = Snackbar.make(
        view,
        context.getString(R.string.error_firebase_403),
        LENGTH_INDEFINITE
    ).apply {
        setAction(context.getString(R.string.error_btn_confirm)) { dismiss() }
        show()
    }

    fun showFirebaseDeviceBlockedError(context: Context, view: View): Snackbar = Snackbar.make(
        view,
        context.getString(R.string.error_firebase_device_blocked),
        LENGTH_INDEFINITE
    ).apply {
        setAction(context.getString(R.string.error_btn_confirm)) { dismiss() }
        show()
    }

    fun showNetworkFailureError(context: Context, view: View): Snackbar = Snackbar.make(
        view,
        context.getString(R.string.error_network_failure),
        LENGTH_LONG
    ).apply { show() }

    fun showIntentAppNotFoundError(context: Context, view: View): Snackbar = Snackbar.make(
        view,
        context.getString(R.string.error_intent_app_not_found),
        LENGTH_LONG
    ).apply { show() }
}