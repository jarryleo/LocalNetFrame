package cn.leo.drawonline

import android.app.Application
import cn.leo.drawonline.net.NetImpl
import cn.leo.drawonline.net.NetInterFace
import cn.leo.drawonline.utils.Config
import cn.leo.nio_client.core.Client
import com.tencent.bugly.Bugly

/**
 * Created by Leo on 2018/2/28.
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        netManager = NetImpl(this)
        netManager?.initNetWork(this)
        //初始化bugly
        Bugly.init(applicationContext, Config.buglyId, false)
        //初始化即时通讯服务
        Client.init(this, "118.89.48.219", 25627)
    }

    override fun onTerminate() {
        super.onTerminate()
        netManager?.stopNet()
        netManager = null
    }

    companion object {
        private var netManager: NetImpl? = null
        fun getNetManager(dataListener: NetInterFace.OnDataArrivedListener): NetImpl? {
            netManager?.setDataArrivedListener(dataListener)
            return netManager
        }
    }
}