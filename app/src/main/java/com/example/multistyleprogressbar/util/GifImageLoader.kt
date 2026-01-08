package com.example.multistyleprogressbar.util

import android.content.Context
import coil3.ImageLoader
import coil3.gif.GifDecoder

object GifImageLoader {

    lateinit var gifImageLoader: ImageLoader
        private set

    @JvmStatic
    fun initialize(context: Context) {
        gifImageLoader = ImageLoader.Builder(context)
            .components {
                add(GifDecoder.Factory())
            }
            .build()
    }

}