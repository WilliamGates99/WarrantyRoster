package com.xeniac.warrantyroster_manager.feature_firebase_cloud_messaging.services.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import timber.log.Timber
import java.net.HttpURLConnection
import java.net.URL

fun getBitmapFromUrl(
    imageUrl: String
): Bitmap? = try {
    val url = URL(imageUrl)

    val connection = url.openConnection() as HttpURLConnection
    connection.apply {
        doInput = true
    }.connect()

    val inputStream = connection.inputStream

    BitmapFactory.decodeStream(inputStream)
} catch (e: Exception) {
    Timber.e("Get bitmap from URL failed:")
    e.printStackTrace()
    null
}