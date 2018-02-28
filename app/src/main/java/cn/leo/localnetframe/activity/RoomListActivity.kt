package cn.leo.localnetframe.activity

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import cn.leo.localnetframe.MyApplication
import cn.leo.localnetframe.R
import cn.leo.localnetframe.adapter.RoomListAdapter
import cn.leo.localnetframe.bean.Room
import cn.leo.localnetframe.net.NetManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_room_list.*

class RoomListActivity : AppCompatActivity(), NetManager.OnMsgArrivedListener {
    private var netManager: NetManager? = null
    private var adapter: RoomListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_list)
        MyApplication.getNetManager(this)
        netManager = MyApplication.getNetManager(this)

        fab.setOnClickListener { view ->
            netManager?.createRoom()
            Snackbar.make(view, "创建房间成功", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        initView()
    }

    private fun initView() {
        swipeRefresh.setOnRefreshListener { netManager?.findRoom() }
        adapter = RoomListAdapter()
        recyclerView.adapter = adapter
        netManager?.findRoom()
    }

    override fun onMsgArrived(data: String) {

        swipeRefresh.isRefreshing = false
        when (data.first()) {
            'R' -> {
                //返回一个房间json
                val json = data.substring(1)
                val room = Gson().fromJson<Room>(json, Room::class.java)
                adapter?.addData(room)
            }
            else -> {
            }
        }
    }
}
