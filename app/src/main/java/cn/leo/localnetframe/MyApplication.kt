package cn.leo.localnetframe

import android.app.Application
import cn.leo.localnetframe.net.NetImpl
import cn.leo.localnetframe.net.NetInterFace
import cn.leo.localnetframe.utils.Config
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