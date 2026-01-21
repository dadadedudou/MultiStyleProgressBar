package com.example.multistyleprogressbar.ui

import kotlinx.coroutines.Runnable
import java.lang.ref.WeakReference

class AutoPollTask : Runnable {
    private var mReference: WeakReference<AutoPollRecycleView>

    constructor(reference: AutoPollRecycleView) {
        mReference = WeakReference<AutoPollRecycleView>(reference)
    }

    override fun run() {
        val recycle: AutoPollRecycleView? = mReference.get()
        if (recycle != null && recycle.running && recycle.canRun) {
            recycle.scrollBy(2, 2)
            recycle.postDelayed(recycle.autoPollTask, recycle.TIME_AUTO_POLL)
        }
    }
}