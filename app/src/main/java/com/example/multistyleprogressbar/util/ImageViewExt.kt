package com.example.multistyleprogressbar.util

import android.widget.ImageView
import coil3.ImageLoader
import coil3.imageLoader
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.Disposable
import coil3.request.ImageRequest
import coil3.request.target
import kotlin.apply


inline fun ImageView.loadGif(
    data: Any?,
    imageLoader: ImageLoader = GifImageLoader.gifImageLoader,
    builder: ImageRequest.Builder.() -> Unit = {}
): Disposable {

    val request = ImageRequest.Builder(context)
        .data(data)
        .apply(builder)
        .target(this)
        .build()

    return imageLoader.enqueue(request)
}

inline fun ImageView.loadWithHeaders(
    data: Any?,
    imageLoader: ImageLoader = context.imageLoader,
    builder: ImageRequest.Builder.() -> Unit = {},
): Disposable {
    val headers = NetworkHeaders.Builder()
        .add("Authorization", "")
        .build()

    val request = ImageRequest.Builder(context)
        .data(data)
        .httpHeaders(headers)
        .target(this)
        .apply(builder)
        .build()
    return imageLoader.enqueue(request)
}

