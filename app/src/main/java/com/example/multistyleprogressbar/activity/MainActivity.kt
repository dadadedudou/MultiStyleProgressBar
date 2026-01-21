package com.example.multistyleprogressbar.activity

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.method.ScrollingMovementMethod
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.airbnb.lottie.LottieAnimationView
import com.example.multistyleprogressbar.R
import com.example.multistyleprogressbar.adapter.AutoPollAdapter
import com.example.multistyleprogressbar.adapter.FoodsAdapter
import com.example.multistyleprogressbar.bean.MealsFood
import com.example.multistyleprogressbar.ui.AutoPollRecycleView
import com.example.multistyleprogressbar.ui.CircularZoomLoadingAnim
import com.example.multistyleprogressbar.ui.CustomSmoothScroller
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
    private lateinit var horizontalFlowStyleSliding: AutoPollRecycleView
    private var markerBitAvatar: Int = 0
    private var markerBitScrollingList: Int = 0
    private lateinit var foodList: MutableList<MealsFood>
    private lateinit var mAdapter: FoodsAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var scroller: CustomSmoothScroller
    private lateinit var tvSwitchText: TextView
    private lateinit var buttonSwitchText: Button
    private lateinit var horizontalFlowStyleSlidingAdapter: AutoPollAdapter
    private lateinit var horizontalFlowStyleSlidingList: MutableList<MealsFood>

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
        tvSwitchText = findViewById(R.id.tv_switch_text)
        buttonSwitchText = findViewById(R.id.button_switch_text)
        tvSwitchText.movementMethod = object : ScrollingMovementMethod(){}
        tvSwitchText.text = resources.getText(R.string.main_switch_text_content_1)

        horizontalFlowStyleSliding = findViewById(R.id.horizontal_flow_style_sliding)

        horizontalFlowStyleSlidingList = mutableListOf()
        horizontalFlowStyleSlidingList.add(MealsFood("0 Small essay originally"))
        horizontalFlowStyleSlidingList.add(MealsFood("1 strengthened AI auditing, and heavier penalties"))
        horizontalFlowStyleSlidingList.add(MealsFood("2 In the financial sector"))
        horizontalFlowStyleSlidingList.add(MealsFood("3 strengthened AI auditing, and heavier penalties"))
        horizontalFlowStyleSlidingList.add(MealsFood("4 used to enhance promotional effects; for ordinary people"))
        horizontalFlowStyleSlidingList.add(MealsFood("5 often mixing narration with argument and lyricism"))
        horizontalFlowStyleSlidingList.add(MealsFood("6 In the entertainment industry, the term refers to the personal journey"))
        horizontalFlowStyleSlidingList.add(MealsFood("7 curbing the chaos of small essays in the financial sector [4]. Regulatory authorities have further addressed"))
        horizontalFlowStyleSlidingList.add(MealsFood("8 releasing TV dramas or variety shows, used to enhance promotional effects"))
        horizontalFlowStyleSlidingList.add(MealsFood("9 and took administrative regulatory measures against the involved agents"))

        horizontalFlowStyleSlidingAdapter = AutoPollAdapter(this,horizontalFlowStyleSlidingList)
        horizontalFlowStyleSliding.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false)
        horizontalFlowStyleSliding.adapter = horizontalFlowStyleSlidingAdapter
        horizontalFlowStyleSliding.start()
    }

    private fun initListener() {
        buttonProgressbarStart.setOnClickListener(this)
        buttonProgressbarReset.setOnClickListener(this)
        buttonSwitchAvatar.setOnClickListener(this)
        buttonScrollingList.setOnClickListener(this)
        buttonSwitchText.setOnClickListener(this)
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

            R.id.button_switch_text -> {
                switchTextContent()
            }
        }
    }

    private fun switchTextContent(){
        tvSwitchText.scrollTo(0,0)
        when (++markerBitAvatar % 3) {
            0 -> {
                tvSwitchText.text = resources.getText(R.string.main_switch_text_content_1)
            }

            1 -> {
                tvSwitchText.text = resources.getText(R.string.main_switch_text_content_2)
            }

            2 -> {
                tvSwitchText.text = resources.getText(R.string.main_switch_text_content_3)
            }
        }
    }
}