package com.xeniac.warrantyroster_manager.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.xeniac.warrantyroster_manager.R

object LinkHelper {

    fun openLink(context: Context, view: View, urlString: String) {
        Intent(Intent.ACTION_VIEW, Uri.parse(urlString)).apply {
            resolveActivity(context.packageManager)?.let {
                context.startActivity(this)
            } ?: Snackbar.make(
                view,
                context.getString(R.string.error_intent_app_not_found),
                LENGTH_LONG
            ).show()
        }
    }
}