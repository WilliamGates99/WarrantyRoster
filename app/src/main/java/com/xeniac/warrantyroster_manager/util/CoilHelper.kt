package com.xeniac.warrantyroster_manager.util

import android.content.Context
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import coil.ImageLoader
import coil.request.ImageRequest
import com.xeniac.warrantyroster_manager.R

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
            error(R.drawable.ic_coil_error)
            listener(
                onStart = {
                    progressBar.visibility = VISIBLE
                },
                onSuccess = { _, _ ->
                    imageView.imageTintList =
                        ContextCompat.getColorStateList(context, R.color.black)
                    progressBar.visibility = GONE
                },
                onError = { _, _ ->
                    imageView.imageTintList = ContextCompat.getColorStateList(context, R.color.red)
                    progressBar.visibility = GONE
                }
            )
        }.build()
        imageLoader.enqueue(request)
    }
}