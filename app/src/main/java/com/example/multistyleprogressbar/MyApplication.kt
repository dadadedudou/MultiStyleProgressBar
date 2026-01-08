package com.example.multistyleprogressbar

import android.app.Application
import com.example.multistyleprogressbar.util.GifImageLoader

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        GifImageLoader.initialize(this)
    }
}