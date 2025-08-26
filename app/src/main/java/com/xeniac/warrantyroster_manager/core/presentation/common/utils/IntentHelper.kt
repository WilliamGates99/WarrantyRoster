package com.xeniac.warrantyroster_manager.core.presentation.common.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import com.xeniac.warrantyroster_manager.BuildConfig
import com.xeniac.warrantyroster_manager.core.presentation.common.ui.components.showIntentAppNotFoundToast
import timber.log.Timber

fun Context.openLinkInInAppBrowser(
    urlString: String
) {
    try {
        val intent = CustomTabsIntent.Builder().build().apply {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        intent.launchUrl(
            /* context = */ this,
            /* url = */ urlString.toUri()
        )
    } catch (e: ActivityNotFoundException) {
        Timber.e("Open link in in-app browser Exception:")
        e.printStackTrace()

        openLinkInExternalBrowser(urlString)
    }
}

fun Context.openLinkInExternalBrowser(
    urlString: String
) {
    try {
        Intent().apply {
            action = Intent.ACTION_VIEW
            data = urlString.toUri()
            startActivity(this)
        }
    } catch (e: ActivityNotFoundException) {
        Timber.e("Open link in external browser Exception:")
        e.printStackTrace()

        showIntentAppNotFoundToast()
    }
}

fun Context.openAppPageInStore(
    customUrlString: String? = null
) {
    val appStoreUrlString = customUrlString ?: BuildConfig.URL_APP_STORE

    try {
        Intent().apply {
            action = Intent.ACTION_VIEW
            data = appStoreUrlString.toUri()
            setPackage(BuildConfig.PACKAGE_NAME_APP_STORE)
            startActivity(this)
        }
    } catch (e: ActivityNotFoundException) {
        Timber.e("Open app page in store Exception:")
        e.printStackTrace()

        openLinkInExternalBrowser(urlString = appStoreUrlString)
    }
}

fun Context.openAppUpdatePageInStore() {
    val appStoreUrl = if (isAppInstalledFromGitHub()) {
        BuildConfig.URL_APP_STORE + "/releases/latest"
    } else BuildConfig.URL_APP_STORE

    try {
        Intent().apply {
            action = Intent.ACTION_VIEW
            data = appStoreUrl.toUri()
            setPackage(BuildConfig.PACKAGE_NAME_APP_STORE)
            startActivity(this)
        }
    } catch (e: ActivityNotFoundException) {
        Timber.e("Open app update page in store Exception:")
        e.printStackTrace()

        openLinkInExternalBrowser(urlString = appStoreUrl)
    }
}