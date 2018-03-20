package cn.leo.localnetframe.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import cn.leo.localnet.BuildConfig
import cn.leo.localnetframe.R
import cn.leo.localnetframe.utils.Config
import cn.leo.localnetframe.utils.get
import kotlinx.android.synthetic.main.content_splash.*

class SplashActivity : AppCompatActivity() {
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        tvVersion.text = getString(R.string.splash_version, BuildConfig.VERSION_NAME)
        countDown()
    }

    private fun countDown() {
        handler.postDelayed(runnable, 2000)
    }

    private var runnable = Runnable { openActivity() }

    private fun openActivity() {
        var icon = get(Config.ICON, -1)
        if (icon == -1) {
            startActivity(Intent(this, SettingActivity::class.java))
        } else {
            startActivity(Intent(this, RoomListActivity::class.java))
        }
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }
}
