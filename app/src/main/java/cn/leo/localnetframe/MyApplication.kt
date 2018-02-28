package cn.leo.localnetframe

import android.app.Application
import cn.leo.localnetframe.net.NetManager

/**
 * Created by Leo on 2018/2/28.
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        netManager = NetManager(this)
        netManager.startNet()
    }

    override fun onTerminate() {
        super.onTerminate()
        netManager.stopNet()
    }

    companion object {
        private lateinit var netManager: NetManager
        fun getNetManager(dataListener: NetManager.OnMsgArrivedListener): NetManager {
            netManager.setDataListener(dataListener)
            return netManager
        }
    }
}