package com.example.multistyleprogressbar.ui

import android.content.Context
import android.graphics.BitmapFactory
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
import kotlin.math.min
import kotlin.math.sqrt

class RotaView : View {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attributeSet: AttributeSet) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        initPaint(context, attributeSet)
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
            ta.getDimension(
                R.styleable.ClockView_clock_textSize,
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
        invalidate()
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

        val bitmap1 = BitmapFactory.decodeResource(resources, R.drawable.icon_switch_voice)
        val matrix1 = Matrix()
        matrix1.postScale(0.2f, 0.2f)
        matrix1.postTranslate(
            (width / 2).toFloat() - bitmap1.width * 0.2f / 2,
            mCircleRectF.top - bitmap1.height * 0.2f / 2
        )
        mCanvas!!.drawBitmap(bitmap1, matrix1, Paint())

        val bitmap2 = BitmapFactory.decodeResource(resources, R.drawable.icon_switch_voice)
        val matrix2 = Matrix()
        matrix2.postScale(0.2f, 0.2f)
        matrix2.postTranslate(
            mCircleRectF.right - bitmap2.width * 0.2f / 2,
            height / 2 - bitmap2.height * 0.2f / 2
        )
        mCanvas!!.drawBitmap(bitmap2, matrix2, Paint())

        val bitmap3 = BitmapFactory.decodeResource(resources, R.drawable.icon_switch_voice)
        val matrix3 = Matrix()
        matrix3.postScale(0.2f, 0.2f)
        matrix3.postTranslate(
            (width / 2).toFloat() - bitmap1.width * 0.2f / 2,
            mCircleRectF.bottom - bitmap3.height * 0.2f / 2
        )
        mCanvas!!.drawBitmap(bitmap3, matrix3, Paint())

        val bitmap4 = BitmapFactory.decodeResource(resources, R.drawable.icon_switch_voice)
        val matrix4 = Matrix()
        matrix4.postScale(0.2f, 0.2f)
        matrix4.postTranslate(
            mCircleRectF.left - bitmap1.width * 0.2f / 2,
            height / 2 - bitmap4.height * 0.2f / 2
        )
        mCanvas!!.drawBitmap(bitmap4, matrix4, Paint())


        //画连接数字的4段弧线
        for (i in 0..3) {
            // 画四个弧线 sweepAngle 弧线角度（扇形角度）
            mCanvas!!.drawArc(mCircleRectF, (10 + 90 * i).toFloat(), 70f, false, mCirclePaint!!)
        }
    }
}