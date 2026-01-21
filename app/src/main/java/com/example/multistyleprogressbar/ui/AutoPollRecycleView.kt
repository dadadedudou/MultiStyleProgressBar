package com.example.multistyleprogressbar.ui

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class AutoPollRecycleView : RecyclerView {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attributeSet: AttributeSet) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )

    val TIME_AUTO_POLL = 16L
    var autoPollTask: AutoPollTask = AutoPollTask(this)
    var running: Boolean = false
    var canRun: Boolean = false

    fun start() {
        if (running)
            stop()
        canRun = true
        running = true
        postDelayed(autoPollTask, TIME_AUTO_POLL)
    }

    fun stop(){
        running = false
        removeCallbacks(autoPollTask)
    }

    override fun onTouchEvent(e: MotionEvent?): Boolean {
        when(e?.action){
            MotionEvent.ACTION_DOWN->{
                if (running)
                    stop()
            }
            MotionEvent.ACTION_UP,MotionEvent.ACTION_CANCEL,MotionEvent.ACTION_OUTSIDE->{
                if (canRun)
                    start()
            }
        }
        return super.onTouchEvent(e)
    }
}