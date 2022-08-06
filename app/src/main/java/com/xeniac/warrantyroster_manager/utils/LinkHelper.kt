package com.xeniac.warrantyroster_manager.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import com.xeniac.warrantyroster_manager.utils.Constants.URL_PLAY_STORE
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showIntentAppNotFoundError

object LinkHelper {

    fun openLink(context: Context, view: View, urlString: String) =
        Intent(Intent.ACTION_VIEW, Uri.parse(urlString)).apply {
            resolveActivity(context.packageManager)?.let {
                context.startActivity(this)
            } ?: showIntentAppNotFoundError(context, view)
        }

    fun openPlayStore(context: Context, view: View) = try {
        context.startActivity(
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(URL_PLAY_STORE + context.packageName)
                setPackage("com.android.vending")
            }
        )
    } catch (e: ActivityNotFoundException) {
        openLink(context, view, URL_PLAY_STORE + context.packageName)
    }
}