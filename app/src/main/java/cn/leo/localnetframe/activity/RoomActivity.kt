package cn.leo.localnetframe.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import cn.leo.localnetframe.MyApplication
import cn.leo.localnetframe.R
import cn.leo.localnetframe.net.NetManager

class RoomActivity : AppCompatActivity(), NetManager.OnMsgArrivedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        MyApplication.getNetManager(this)
    }

    override fun onMsgArrived(data: String) {

    }
}
