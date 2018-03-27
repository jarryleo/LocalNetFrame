package cn.leo.drawonline.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import cn.leo.drawonline.BuildConfig
import cn.leo.drawonline.R
import cn.leo.drawonline.utils.Config
import cn.leo.drawonline.utils.get
import cn.leo.localnet.utils.ToastUtilK
import cn.leo.nio_client.core.ClientListener
import kotlinx.android.synthetic.main.content_splash.*

class SplashActivity : AppCompatActivity(), ClientListener {

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

    override fun onConnectSuccess() {
        ToastUtilK.show(this, "连接服务器成功")
    }

    override fun onConnectFailed() {
        ToastUtilK.show(this, "连接服务器失败")
    }

    override fun onIntercept() {
        ToastUtilK.show(this, "被服务器拒绝")
    }

    override fun onDataArrived(data: ByteArray?) {

    }
}
