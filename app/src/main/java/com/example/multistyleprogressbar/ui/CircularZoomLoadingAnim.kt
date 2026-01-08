package com.example.multistyleprogressbar.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import com.example.multistyleprogressbar.R

class CircularZoomLoadingAnim : View {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attributeSet: AttributeSet) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        initPaint(context)
    }

    private lateinit var mPaint: Paint
    private var mWidth: Float = 0f
    private var mHeight: Float = 0f
    private var mMaxRadius: Float = 8f
    private var circularCount: Int = 3
    private var mAnimatedValue: Float = 1.0f
    private var mJumpValue: Int = 0

    private fun initPaint(context: Context) {
        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL
        mPaint.color = context.resources.getColor(R.color.ai_meals_menu_bg_item_color_end)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val circularX = mWidth / circularCount
        for (i in 0..circularCount - 1) {
            if (i == mJumpValue % circularCount) {
                canvas.drawCircle(
                    i * circularX + circularX / 2f,
                    mHeight / 2,
                    mMaxRadius * mAnimatedValue,
                    mPaint
                )
            } else {
                canvas.drawCircle(i * circularX + circularX / 2f, mHeight / 2, mMaxRadius, mPaint)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = measuredWidth.toFloat()
        mHeight = measuredHeight.toFloat()
    }

    private var valueAnimator: ValueAnimator? = null

    public fun startAnim() {
        stopAnim()
        startViewAnim(0f, 1f, 650)
    }

    public fun stopAnim() {
        if (valueAnimator != null) {
            clearAnimation()
            mAnimatedValue = 0f
            mJumpValue = 0
            valueAnimator!!.repeatCount = 0
            valueAnimator!!.cancel()
            valueAnimator!!.end()
        }
    }

    fun startViewAnim(startF: Float, endF: Float, time: Long): ValueAnimator? {
        valueAnimator = ValueAnimator.ofFloat(startF, endF)
        valueAnimator?.setDuration(time)
        valueAnimator?.interpolator = LinearInterpolator()
        valueAnimator?.repeatCount = ValueAnimator.INFINITE
        valueAnimator?.repeatMode = ValueAnimator.RESTART
        valueAnimator?.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(animation: ValueAnimator) {
                mAnimatedValue = valueAnimator!!.getAnimatedValue() as Float
                if (mAnimatedValue < 0.2) {
                    mAnimatedValue = 0.2f
                }
                invalidate()
            }
        })
        valueAnimator?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
            }

            override fun onAnimationRepeat(animation: Animator) {
                super.onAnimationRepeat(animation)
                mJumpValue++
            }

            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
            }
        })
        valueAnimator?.isRunning?.let {
            if (!(it)) {
                valueAnimator?.start()
            }
        }
        return valueAnimator
    }

}