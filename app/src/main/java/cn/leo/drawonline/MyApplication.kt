package cn.leo.drawonline

import android.app.Application
import android.os.Handler
import cn.leo.drawonline.bean.UserBean
import cn.leo.drawonline.net.NetManager
import cn.leo.drawonline.utils.Config
import cn.leo.nio_client.core.Client
import com.tencent.bugly.Bugly

/**
 * Created by Leo on 2018/2/28.
 */
class MyApplication : Application() {
    private val handler = Handler()
    private val netManager = NetManager()
    override fun onCreate() {
        super.onCreate()
        //初始化即时通讯服务
        Client.init(this, "118.89.48.219", 25627)
//        Client.init(this, "10.0.2.2", 25627)
        //初始化bugly
        Bugly.init(applicationContext, Config.buglyId, false)
        doHeart()
    }

    private val heart = Runnable {
        doHeart()
    }

    private fun doHeart() {
        handler.removeCallbacks(heart)
        netManager.heart()
        handler.postDelayed(heart, 10_000)
    }

    companion object {
        private var userBean: UserBean? = null
        fun getUser() = userBean
        fun setUser(user: UserBean) {
            userBean = user
        }
    }
}