package com.xeniac.warrantyroster_manager.utils

import android.content.Context
import android.view.View
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.xeniac.warrantyroster_manager.R

object SnackBarHelper {

    fun showNetworkConnectionError(
        context: Context,
        view: View,
        action: () -> Unit
    ): Snackbar = Snackbar.make(
        view,
        context.getString(R.string.error_network_connection),
        LENGTH_INDEFINITE
    ).apply {
        setAction(context.getString(R.string.error_btn_retry)) {
            action()
        }
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

    fun showFirebaseAuthAccountExists(
        view: View,
        text: String,
        actionText: String,
        action: () -> Unit
    ): Snackbar = Snackbar.make(view, text, LENGTH_INDEFINITE).apply {
        setAction(actionText) { action() }
        show()
    }

    fun showFirebaseAuthAccountNotFoundError(
        view: View,
        text: String,
        actionText: String,
        action: () -> Unit
    ): Snackbar = Snackbar.make(view, text, LENGTH_INDEFINITE).apply {
        setAction(actionText) { action() }
        show()
    }

    fun showFirebaseAuthCredentialsError(view: View, text: String): Snackbar = Snackbar.make(
        view, text, LENGTH_INDEFINITE
    ).apply {
        show()
    }

    fun showNetworkFailureError(context: Context, view: View): Snackbar = Snackbar.make(
        view,
        context.getString(R.string.error_network_failure),
        LENGTH_LONG
    ).apply {
        show()
    }

    fun showTimerIsNotZeroError(context: Context, view: View, seconds: Int): Snackbar =
        Snackbar.make(
            view,
            context.resources.getQuantityString(
                R.plurals.forgot_pw_error_timer_is_not_zero,
                seconds,
                seconds
            ),
            LENGTH_LONG
        ).apply {
            show()
        }
}