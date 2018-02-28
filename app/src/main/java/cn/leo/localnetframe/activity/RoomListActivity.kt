package cn.leo.localnetframe.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import cn.leo.localnetframe.MyApplication
import cn.leo.localnetframe.R
import cn.leo.localnetframe.net.NetManager

class RoomListActivity : AppCompatActivity(), NetManager.OnMsgArrivedListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_list)
        MyApplication.getNetManager(this)
    }

    override fun onMsgArrived(data: String) {

        when (data.first()) {
            'R' -> {
                //返回一个房间json
            }
            else -> {
            }
        }
    }
}
