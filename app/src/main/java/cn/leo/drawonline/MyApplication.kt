package cn.leo.drawonline

import android.app.Application
import cn.leo.drawonline.utils.Config
import cn.leo.nio_client.core.Client
import com.tencent.bugly.Bugly

/**
 * Created by Leo on 2018/2/28.
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        //初始化即时通讯服务
        //Client.init(this, "118.89.48.219", 25627)
        Client.init(this, "10.0.2.2", 25627)
        //初始化bugly
        Bugly.init(applicationContext, Config.buglyId, false)
    }
}