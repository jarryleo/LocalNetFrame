package cn.leo.localnetframe.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import cn.leo.localnetframe.R

class SplashActivity : AppCompatActivity() {
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        countDown()
    }

    private fun countDown() {
        handler.postDelayed(runnable, 2000)
    }

    private var runnable = Runnable { openActivity() }

    private fun openActivity() {
        startActivity(Intent(this, RoomListActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }
}
