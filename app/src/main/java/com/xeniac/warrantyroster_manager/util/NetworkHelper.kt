package com.xeniac.warrantyroster_manager.util

import android.content.Context
import android.net.ConnectivityManager

class NetworkHelper {
    companion object {
        fun hasNetworkAccess(context: Context): Boolean {
            val connectivityManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            return try {
                val activeNetwork = connectivityManager.activeNetworkInfo
                activeNetwork != null && activeNetwork.isConnectedOrConnecting
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}