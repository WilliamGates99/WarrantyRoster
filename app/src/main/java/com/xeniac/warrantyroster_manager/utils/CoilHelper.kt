package com.xeniac.warrantyroster_manager.utils

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import coil.ImageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import timber.log.Timber

object CoilHelper {
    fun loadCategoryImage(
        context: Context,
        categoryIcon: String,
        imageLoader: ImageLoader,
        imageView: ImageView,
        progressBar: ProgressBar
    ) {
        val request = ImageRequest.Builder(context).apply {
            data(categoryIcon)
            target(imageView)
            listener(
                onStart = {
                    progressBar.visibility = View.VISIBLE
                },
                onSuccess = { _, _ ->
                    progressBar.visibility = View.GONE
                },
                onError = { _, throwable ->
                    Timber.e("loading error: $throwable")
                }
            )
            crossfade(true)
            memoryCachePolicy(CachePolicy.ENABLED)
            diskCachePolicy(CachePolicy.ENABLED)
            networkCachePolicy(CachePolicy.ENABLED)
        }.build()
        imageLoader.enqueue(request)
    }
}