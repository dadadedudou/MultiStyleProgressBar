package com.example.multistyleprogressbar.activity

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.airbnb.lottie.LottieAnimationView
import com.example.multistyleprogressbar.R
import com.example.multistyleprogressbar.adapter.FoodsAdapter
import com.example.multistyleprogressbar.bean.MealsFood
import com.example.multistyleprogressbar.ui.CircularZoomLoadingAnim
import com.example.multistyleprogressbar.ui.CustomSmoothScroller
import com.example.multistyleprogressbar.ui.RotaView
import com.example.multistyleprogressbar.util.loadGif


class MainActivity : AppCompatActivity(), Handler.Callback, View.OnClickListener {
    private lateinit var handler: Handler
    private lateinit var progressbar: ProgressBar
    private lateinit var animationAvatar1: LottieAnimationView
    private lateinit var animationAvatar2: LottieAnimationView
    private lateinit var animationAvatar3: LottieAnimationView
    private lateinit var buttonProgressbarStart: Button
    private lateinit var buttonProgressbarReset: Button
    private lateinit var buttonSwitchAvatar: Button
    private lateinit var buttonScrollingList: Button
    private lateinit var thinkGif: ImageView
    private lateinit var circularZoom: CircularZoomLoadingAnim
    private lateinit var rvScrollingList: RecyclerView
    private lateinit var rotaView: RotaView
    private var markerBitAvatar: Int = 0
    private var markerBitScrollingList: Int = 0
    private lateinit var foodList: MutableList<MealsFood>
    private lateinit var mAdapter: FoodsAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var scroller: CustomSmoothScroller

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        handler = Handler(mainLooper, this)
        initView()
        initListener()
        initData()
    }

    private fun initView() {
        progressbar = findViewById(R.id.progressBar)
        animationAvatar1 = findViewById(R.id.imageView_avatar_1)
        animationAvatar2 = findViewById(R.id.imageView_avatar_2)
        animationAvatar3 = findViewById(R.id.imageView_avatar_3)
        thinkGif = findViewById(R.id.think_gif)
        circularZoom = findViewById(R.id.circular_zoom)
        rvScrollingList = findViewById(R.id.rv_scrolling_list)
        buttonProgressbarStart = findViewById(R.id.button_progressbar_start)
        buttonProgressbarReset = findViewById(R.id.button_progressbar_reset)
        buttonSwitchAvatar = findViewById(R.id.button_switch_avatar)
        buttonScrollingList = findViewById(R.id.button_scrolling_list)
        rotaView = findViewById(R.id.rota_view)
    }

    private fun initListener() {
        buttonProgressbarStart.setOnClickListener(this)
        buttonProgressbarReset.setOnClickListener(this)
        buttonSwitchAvatar.setOnClickListener(this)
        buttonScrollingList.setOnClickListener(this)
    }

    private fun initData() {
        thinkGif.loadGif(R.drawable.think) {
            crossfade(true)
            placeholder(R.drawable.think)
            error(R.drawable.think)
        }
        circularZoom.startAnim()

        layoutManager = LinearLayoutManager(this)
        scroller = CustomSmoothScroller(this)
        rvScrollingList.layoutManager = layoutManager
        rvScrollingList.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(
                v: View?,
                event: MotionEvent?
            ): Boolean {
                return true
            }
        })

        mAdapter = FoodsAdapter()
        foodList = mutableListOf()
        rvScrollingList.adapter = mAdapter
        mAdapter.setList(foodList)

//        startRotationAnimation()
    }

    private fun switchAvatar() {
        when (++markerBitAvatar % 3) {
            0 -> {
                animationAvatar1.visibility = View.VISIBLE
                animationAvatar2.visibility = View.GONE
                animationAvatar3.visibility = View.GONE
            }

            1 -> {
                animationAvatar1.visibility = View.GONE
                animationAvatar2.visibility = View.VISIBLE
                animationAvatar3.visibility = View.GONE
            }

            2 -> {
                animationAvatar1.visibility = View.GONE
                animationAvatar2.visibility = View.GONE
                animationAvatar3.visibility = View.VISIBLE
            }
        }
    }

    override fun handleMessage(msg: Message): Boolean {
        progressbar.progress = progressbar.progress.plus(1)
        if (progressbar.progress != 100)
            handler.sendEmptyMessageDelayed(0, 1)
        return true
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button_progressbar_start -> handler.sendEmptyMessageDelayed(0, 1)
            R.id.button_progressbar_reset -> progressbar.progress = 0
            R.id.button_switch_avatar -> switchAvatar()
            R.id.button_scrolling_list -> {
                markerBitScrollingList++
                foodList.add(MealsFood("I am the $markerBitScrollingList entry. Do I look good?"))
                mAdapter.setList(foodList)

                scroller.targetPosition = foodList.size - 1
                layoutManager.startSmoothScroll(scroller)
            }
        }
    }

    private fun startRotationAnimation() {
        val rotationAnim = ObjectAnimator.ofFloat(rotaView, "rotation", 0f, 3600f)
        rotationAnim.setDuration(30000) // 设置动画持续时间，例如1000毫秒
        rotationAnim.repeatCount = ValueAnimator.INFINITE // 设置动画重复次数
        rotationAnim.repeatMode = ValueAnimator.RESTART // 设置动画重复模式
        rotationAnim.start()
    }
}