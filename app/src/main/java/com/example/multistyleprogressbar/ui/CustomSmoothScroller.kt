package com.example.multistyleprogressbar.ui

import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import androidx.recyclerview.widget.LinearSmoothScroller

class CustomSmoothScroller(context: Context) : LinearSmoothScroller(context) {
    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
        val speed = 300f / displayMetrics?.densityDpi!!
        Log.i("speed", "" + speed)
        return speed
    }
}