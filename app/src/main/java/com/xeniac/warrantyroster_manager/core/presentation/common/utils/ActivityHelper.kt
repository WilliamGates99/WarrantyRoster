package com.xeniac.warrantyroster_manager.core.presentation.common.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.showIntentAppNotFoundToast
import timber.log.Timber

@Composable
internal fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return remember { derivedStateOf { context as Activity } }.value
        context = context.baseContext
    }
    throw IllegalStateException("findActivity function should be called in the context of an Activity")
}

fun Activity.openAppSettings() {
    try {
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts(
                /* scheme = */ "package",
                /* ssp = */ packageName,
                /* fragment = */ null
            )
        ).also(::startActivity)
    } catch (e: Exception) {
        Timber.e("Open app settings Exception:")
        e.printStackTrace()

        showIntentAppNotFoundToast()
    }
}

fun Activity.restartActivity() {
    Intent(
        /* packageContext = */ this,
        /* cls = */ this::class.java
    ).also {
        finish()
        startActivity(it)
    }
}