package com.example.multistyleprogressbar

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.Button
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * 测试 第一次提交
 * 测试 第二次提交
 * 测试 第三次提交
 * 测试 第四次提交 on github
 * 测试 第五次提交
 * 测试 第七次提交 on github
 */
class MainActivity : AppCompatActivity(), Handler.Callback {
    private lateinit var handler: Handler
    var progressbar: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        handler = Handler(mainLooper, this)
        progressbar = findViewById<ProgressBar>(R.id.progressBar)
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            handler.sendEmptyMessageDelayed(0, 1)

        }
    }

    override fun handleMessage(msg: Message): Boolean {
        progressbar?.progress = progressbar?.progress?.plus(1)!!
        if (progressbar?.progress != 100)
            handler.sendEmptyMessageDelayed(0, 1)
        return true
    }
}
