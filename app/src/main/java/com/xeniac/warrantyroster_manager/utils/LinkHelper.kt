package com.xeniac.warrantyroster_manager.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import com.xeniac.warrantyroster_manager.utils.Constants.URL_PLAY_STORE
import com.xeniac.warrantyroster_manager.utils.SnackBarHelper.showIntentAppNotFoundError

object LinkHelper {

    fun openLink(context: Context, view: View, urlString: String) = Intent().apply {
        action = Intent.ACTION_VIEW
        data = Uri.parse(urlString)

        resolveActivity(context.packageManager)?.let {
            context.startActivity(this)
        } ?: showIntentAppNotFoundError(context, view)
    }

    fun openPlayStore(context: Context, view: View) = Intent().apply {
        action = Intent.ACTION_VIEW
        data = Uri.parse(URL_PLAY_STORE + context.packageName)
        setPackage("com.android.vending")

        resolveActivity(context.packageManager)?.let {
            context.startActivity(this)
        } ?: openLink(context, view, URL_PLAY_STORE + context.packageName)
    }
}