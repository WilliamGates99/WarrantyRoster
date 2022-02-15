package com.xeniac.warrantyroster_manager.utils

import android.content.Context
import coil.ImageLoader
import coil.decode.SvgDecoder

object CoilHelper {

    fun getImageLoader(context: Context): ImageLoader = ImageLoader.Builder(context)
        .componentRegistry { add(SvgDecoder(context)) }.build()
}