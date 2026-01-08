package com.example.multistyleprogressbar.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.toColorInt
import com.example.multistyleprogressbar.R
import com.example.multistyleprogressbar.util.DensityUtils
import java.util.Calendar
import kotlin.math.min

class AvatarPickerView: View {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attributeSet: AttributeSet) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        initPaint(context,attributeSet)
    }

    /* 全局画布 */
    private var mCanvas: Canvas? = null

    /* 12、3、6、9小时文本画笔 */
    private var mTextPaint: Paint? = null

    /* 测量小时文本宽高的矩形 */
    private val mTextRect = Rect()

    /* 小时圆圈画笔 */
    private var mCirclePaint: Paint? = null

    /* 小时圆圈线条宽度 */
    private val mCircleStrokeWidth = 4f

    /* 小时圆圈的外接矩形 */
    private val mCircleRectF = RectF()

    /* 亮色，用于分针、秒针、渐变终止色 */
    private var mLightColor = 0

    /* 暗色，圆弧、刻度线、时针、渐变起始色 */
    private var mDarkColor = 0

    /* 背景色 */
    private var mBackgroundColor = 0

    /* 小时文本字体大小 */
    private var mTextSize = 0f

    /* 时钟半径，不包括padding值 */
    private var mRadius = 0f

    /* 刻度线长度 */
    private var mScaleLength = 0f

    /* 时针角度 */
    private var mHourDegree = 0f

    /* 分针角度 */
    private var mMinuteDegree = 0f

    /* 秒针角度 */
    private var mSecondDegree = 0f

    /* 时针画笔 */
    private var mHourHandPaint: Paint? = null

    /* 分针画笔 */
    private var mMinuteHandPaint: Paint? = null

    /* 秒针画笔 */
    private var mSecondHandPaint: Paint? = null

    /* 时针路径 */
    private val mHourHandPath = Path()

    /* 分针路径 */
    private val mMinuteHandPath = Path()

    /* 秒针路径 */
    private val mSecondHandPath = Path()

    /* 加一个默认的padding值，为了防止用camera旋转时钟时造成四周超出view大小 */
    private var mDefaultPadding = 0f
    private var mPaddingLeft = 0f
    private var mPaddingTop = 0f
    private var mPaddingRight = 0f
    private var mPaddingBottom = 0f

    /* 梯度扫描渐变 */
    private var mSweepGradient: SweepGradient? = null

    /* 渐变矩阵，作用在SweepGradient */
    private var mGradientMatrix: Matrix? = null

    /* 指针的在x轴的位移 */
    private val mCanvasTranslateX = 0f

    /* 指针的在y轴的位移 */
    private val mCanvasTranslateY = 0f

    /* 指针的最大位移 */
    private val mMaxCanvasTranslate = 0f

    /* 刻度圆弧画笔 */
    private var mScaleArcPaint: Paint? = null

    /* 刻度圆弧的外接矩形 */
    private val mScaleArcRectF = RectF()

    /* 刻度线画笔 */
    private var mScaleLinePaint: Paint? = null

    private fun initPaint(
        context: Context,
        attrs: AttributeSet?
    ) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.ClockView, 0, 0)
        mBackgroundColor =
            ta.getColor(R.styleable.ClockView_clock_backgroundColor, "#237EAD".toColorInt())
        mLightColor =
            ta.getColor(R.styleable.ClockView_clock_lightColor, "#ffffff".toColorInt())
        mDarkColor =
            ta.getColor(R.styleable.ClockView_clock_darkColor, "#80FFFFFF".toColorInt())
        mTextSize =
            ta.getDimension(R.styleable.ClockView_clock_textSize,
                DensityUtils.sp2px(context, 14).toFloat()
            )
        ta.recycle()

        mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTextPaint!!.style = Paint.Style.FILL
        mTextPaint!!.setColor(mDarkColor)

        //居中绘制文字
        mTextPaint!!.textAlign = Paint.Align.CENTER
        mTextPaint!!.textSize = mTextSize

        mCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mCirclePaint!!.style = Paint.Style.STROKE
        mCirclePaint!!.strokeWidth = mCircleStrokeWidth
        mCirclePaint!!.setColor(mDarkColor)

        mScaleLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mScaleLinePaint!!.style = Paint.Style.STROKE
        mScaleLinePaint!!.setColor(mBackgroundColor)

        mScaleArcPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mScaleArcPaint!!.style = Paint.Style.STROKE

        mGradientMatrix = Matrix()

        mSecondHandPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mSecondHandPaint!!.style = Paint.Style.FILL
        mSecondHandPaint!!.setColor(mLightColor)

        mHourHandPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mHourHandPaint!!.style = Paint.Style.FILL
        mHourHandPaint!!.setColor(mDarkColor)

        mMinuteHandPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mMinuteHandPaint!!.style = Paint.Style.FILL
        mMinuteHandPaint!!.setColor(mLightColor)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            measureDimension(widthMeasureSpec),
            measureDimension(heightMeasureSpec)
        )
    }

    private fun measureDimension(measureSpec: Int): Int {
        val defaultSize = 800
        val model = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)
        return when (model) {
            MeasureSpec.EXACTLY -> size
            MeasureSpec.AT_MOST -> min(size, defaultSize)
            MeasureSpec.UNSPECIFIED -> defaultSize
            else -> defaultSize
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mRadius = (min(
            w - getPaddingLeft() - getPaddingRight(),
            h - paddingTop - paddingBottom
        ) / 2).toFloat() // 各个指针长度
        mDefaultPadding = 0.12f * mRadius
        mPaddingLeft = mDefaultPadding + w / 2 - mRadius + getPaddingLeft() // 钟离左边界距离
        mPaddingRight = mDefaultPadding + w / 2 - mRadius + getPaddingRight() // 钟离右边界距离
        mPaddingTop = mDefaultPadding + h / 2 - mRadius + paddingTop // 钟离上边界距离
        mPaddingBottom = mDefaultPadding + h / 2 - mRadius + paddingBottom // 钟离下边界距离

        mScaleLength = 0.12f * mRadius // 根据比例确定刻度线长度
        mScaleLinePaint!!.strokeWidth = 0.012f * mRadius // 刻度圈的宽度

        mScaleArcPaint!!.strokeWidth = mScaleLength


        //梯度扫描渐变，以(w/2,h/2)为中心点，两种起止颜色梯度渐变
        //float数组表示，[0,0.75)为起始颜色所占比例，[0.75,1}为起止颜色渐变所占比例
        mSweepGradient = SweepGradient(
            (w / 2).toFloat(), (h / 2).toFloat(),
            intArrayOf(mDarkColor, mLightColor), floatArrayOf(0.75f, 1f)
        )
    }

    override fun onDraw(canvas: Canvas) {
        mCanvas = canvas
        drawOutSideArc() // 外面的圈圈
        getCurrentTime()
        drawScaleLine() // 刻度 加 渐变色
        drawSecondNeedle() // 三个针
        drawMinuteNeedle()
        drawHourHand()
        invalidate()
    }

    private fun getCurrentTime() {
        val calendar = Calendar.getInstance()
        val milliSecond = calendar.get(Calendar.MILLISECOND).toFloat()
        val second = calendar.get(Calendar.SECOND) + milliSecond / 1000 // 精确到小数点后 保证圆滑
        val minute = calendar.get(Calendar.MINUTE) + second / 60
        val hour = calendar.get(Calendar.HOUR) + minute / 60
        mSecondDegree = second / 60 * 360
        mMinuteDegree = minute / 60 * 360
        mHourDegree = hour / 12 * 360
    }

    private fun drawOutSideArc() {
        val timeList = mutableListOf("12", "3", "6", "9")

        //计算数字的高度
        mTextPaint!!.getTextBounds(timeList[0], 0, timeList[1].length, mTextRect)
        mCircleRectF.set(
            mPaddingLeft + mTextRect.width() / 2 + mCircleStrokeWidth / 2,  // 画一个外界小矩形，在矩形里画圆
            mPaddingTop + mTextRect.height() / 2 + mCircleStrokeWidth / 2,
            width - mPaddingRight - mTextRect.width() / 2 - mCircleStrokeWidth / 2,
            height - mPaddingBottom - mTextRect.height() / 2 - mCircleStrokeWidth / 2
        )
        mCanvas!!.drawText(
            timeList[0],
            (width / 2).toFloat(),
            mCircleRectF.top + mTextRect.height() / 2,
            mTextPaint!!
        ) // 定点写字
        mCanvas!!.drawText(
            timeList[1],
            mCircleRectF.right,
            (height / 2 + mTextRect.height() / 2).toFloat(),
            mTextPaint!!
        )
        mCanvas!!.drawText(
            timeList[2],
            (width / 2).toFloat(),
            mCircleRectF.bottom + mTextRect.height() / 2,
            mTextPaint!!
        )
        mCanvas!!.drawText(
            timeList[3],
            mCircleRectF.left,
            (height / 2 + mTextRect.height() / 2).toFloat(),
            mTextPaint!!
        )

        //画连接数字的4段弧线
        for (i in 0..3) {
            // 画四个弧线 sweepAngle 弧线角度（扇形角度）
            mCanvas!!.drawArc(mCircleRectF, (5 + 90 * i).toFloat(), 80f, false, mCirclePaint!!)
        }
    }

    private fun drawScaleLine() {
        mCanvas!!.save()
        mCanvas!!.translate(mCanvasTranslateX, mCanvasTranslateY)
        mScaleArcRectF.set(
            mPaddingLeft + 1.5f * mScaleLength + mTextRect.height() / 2,
            mPaddingTop + 1.5f * mScaleLength + mTextRect.height() / 2,
            getWidth() - mPaddingRight - mTextRect.height() / 2 - 1.5f * mScaleLength,
            getHeight() - mPaddingBottom - mTextRect.height() / 2 - 1.5f * mScaleLength
        )


        // matrix默认会在三点钟方向开始颜色的渐变，为了吻合钟表十二点钟顺时针旋转的方向，把秒针旋转的角度减去90度
        mGradientMatrix!!.setRotate(
            mSecondDegree - 90,
            (getWidth() / 2).toFloat(),
            (getHeight() / 2).toFloat()
        )
        mSweepGradient!!.setLocalMatrix(mGradientMatrix)
        mScaleArcPaint!!.setShader(mSweepGradient)
        mCanvas!!.drawArc(mScaleArcRectF, 0f, 360f, false, mScaleArcPaint!!)


        // 画背景色刻度线
        for (i in 0..199) {
            mCanvas!!.drawLine(
                (getWidth() / 2).toFloat(),
                mPaddingTop + mScaleLength + mTextRect.height() / 2,
                (getWidth() / 2).toFloat(),
                mPaddingTop + 2 * mScaleLength + mTextRect.height() / 2,
                mScaleLinePaint!!
            )
            mCanvas!!.rotate(1.8f, (getWidth() / 2).toFloat(), (getHeight() / 2).toFloat())
        }
        mCanvas!!.restore()
    }

    private fun drawSecondNeedle() {
        mCanvas!!.save() // ❑ save：用来保存Canvas的状态。save之后，可以调用Canvas的平移、放缩、旋转、错切、裁剪等操作。
        mCanvas!!.rotate(
            mSecondDegree,
            (getWidth() / 2).toFloat(),
            (getHeight() / 2).toFloat()
        ) // 设置指针位置
        mSecondHandPath.reset()
        val offset = mPaddingTop + mTextRect.height() / 2

        mSecondHandPath.moveTo((getWidth() / 2).toFloat(), offset + 0.26f * mRadius) // 这三行绘制三角尖
        mSecondHandPath.lineTo(getWidth() / 2 - 0.05f * mRadius, offset + 0.34f * mRadius)
        mSecondHandPath.lineTo(getWidth() / 2 + 0.05f * mRadius, offset + 0.34f * mRadius)
        mSecondHandPath.close()
        mSecondHandPaint!!.setColor(mLightColor)
        mCanvas!!.drawPath(mSecondHandPath, mSecondHandPaint!!)
        mCanvas!!.restore() // ❑ restore：用来恢复Canvas之前保存的状态。防止save后对Canvas执行的操作对后续的绘制有影响。
    }

    private fun drawMinuteNeedle() {
        mCanvas!!.save()
        mCanvas!!.translate(mCanvasTranslateX * 2f, mCanvasTranslateY * 2f)
        mCanvas!!.rotate(mMinuteDegree, (getWidth() / 2).toFloat(), (getHeight() / 2).toFloat())
        mMinuteHandPath.reset()

        val offset = mPaddingTop + mTextRect.height() / 2
        mMinuteHandPath.moveTo(getWidth() / 2 - 0.01f * mRadius, getHeight() / 2 - 0.03f * mRadius)
        mMinuteHandPath.lineTo(getWidth() / 2 - 0.008f * mRadius, offset + 0.365f * mRadius)
        mMinuteHandPath.quadTo(
            (getWidth() / 2).toFloat(), offset + 0.345f * mRadius,
            getWidth() / 2 + 0.008f * mRadius, offset + 0.365f * mRadius
        )
        mMinuteHandPath.lineTo(getWidth() / 2 + 0.01f * mRadius, getHeight() / 2 - 0.03f * mRadius)
        mMinuteHandPath.close()
        mMinuteHandPaint!!.setStyle(Paint.Style.FILL)
        mCanvas!!.drawPath(mMinuteHandPath, mMinuteHandPaint!!)

        mCircleRectF.set(
            getWidth() / 2 - 0.03f * mRadius, getHeight() / 2 - 0.03f * mRadius,  //绘制指针轴的小圆圈
            getWidth() / 2 + 0.03f * mRadius, getHeight() / 2 + 0.03f * mRadius
        )
        mMinuteHandPaint!!.setStyle(Paint.Style.STROKE)
        mMinuteHandPaint!!.setStrokeWidth(0.02f * mRadius)
        mCanvas!!.drawArc(mCircleRectF, 0f, 360f, false, mMinuteHandPaint!!)
        mCanvas!!.restore()
    }

    private fun drawHourHand() {
        mCanvas!!.save()
        mCanvas!!.translate(mCanvasTranslateX * 1.2f, mCanvasTranslateY * 1.2f)
        mCanvas!!.rotate(mHourDegree, (getWidth() / 2).toFloat(), (getHeight() / 2).toFloat())
        mHourHandPath.reset()
        val offset = mPaddingTop + mTextRect.height() / 2
        mHourHandPath.moveTo(getWidth() / 2 - 0.018f * mRadius, getHeight() / 2 - 0.03f * mRadius)
        mHourHandPath.lineTo(getWidth() / 2 - 0.009f * mRadius, offset + 0.48f * mRadius)
        mHourHandPath.quadTo(
            (getWidth() / 2).toFloat(), offset + 0.46f * mRadius,
            getWidth() / 2 + 0.009f * mRadius, offset + 0.48f * mRadius
        )
        mHourHandPath.lineTo(getWidth() / 2 + 0.018f * mRadius, getHeight() / 2 - 0.03f * mRadius)
        mHourHandPath.close()
        mHourHandPaint!!.setStyle(Paint.Style.FILL)
        mCanvas!!.drawPath(mHourHandPath, mHourHandPaint!!)

        mCircleRectF.set(
            getWidth() / 2 - 0.03f * mRadius, getHeight() / 2 - 0.03f * mRadius,
            getWidth() / 2 + 0.03f * mRadius, getHeight() / 2 + 0.03f * mRadius
        )
        mHourHandPaint!!.setStyle(Paint.Style.STROKE)
        mHourHandPaint!!.setStrokeWidth(0.01f * mRadius)
        mCanvas!!.drawArc(mCircleRectF, 0f, 360f, false, mHourHandPaint!!)
        mCanvas!!.restore()
    }
}