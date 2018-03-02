package cn.leo.localnetframe.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import cn.leo.localnet.utils.ToastUtilK
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
        initView()
    }

    override fun onRestart() {
        super.onRestart()
        netManager = MyApplication.getNetManager(this)
    }

    private fun initView() {
        fab.setOnClickListener {
            netManager?.createRoom()
            ToastUtilK.show(this, "创建房间成功，房间号${netManager?.getMeRoomId()}")
            startActivity(Intent(this@RoomListActivity, RoomActivity::class.java))
        }

        adapter = RoomListAdapter()
        swipeRefresh.setOnRefreshListener {
            adapter?.clearData()
            netManager?.findRoom()
            refresh()
        }
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter

        netManager?.findRoom()
        swipeRefresh.isRefreshing = true
        refresh()
    }

    private fun refresh() {
        handler.postDelayed(runnable, 5000)
    }

    private val handler = Handler()
    private val runnable = Runnable {
        if (swipeRefresh.isRefreshing) {
            swipeRefresh.isRefreshing = false
            ToastUtilK.show(this, "没有搜索到房间，请重试或者创建房间")
        }
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
