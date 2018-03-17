package cn.leo.localnetframe.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import cn.leo.localnet.utils.ToastUtilK
import cn.leo.localnetframe.MyApplication
import cn.leo.localnetframe.R
import cn.leo.localnetframe.adapter.RoomListAdapter
import cn.leo.localnetframe.bean.Room
import cn.leo.localnetframe.net.NetImpl
import cn.leo.localnetframe.net.NetInterFace
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_room_list.*

class RoomListActivity : AppCompatActivity() {
    private var netManager: NetImpl? = null
    private var adapter: RoomListAdapter? = null
    private val dataReceiver = DataReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_list)
        netManager = MyApplication.getNetManager(dataReceiver)
        initView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.setting_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_setting -> {
                startActivity(Intent(this, SettingActivity::class.java))
                return true
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRestart() {
        super.onRestart()
        netManager = MyApplication.getNetManager(dataReceiver)
        adapter?.clearData()
        netManager?.findRoom()
    }

    private fun initView() {
        title = "当前WIFI网络大厅"

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

        //创建房间
        fab.setOnClickListener {
            netManager?.createRoom()
            ToastUtilK.show(this, "创建房间成功，房间号${netManager?.getRoomId()}")
            startActivity(Intent(this@RoomListActivity, RoomActivity::class.java))
        }
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

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }

    /**
     * 接受到数据
     */
    inner class DataReceiver : NetInterFace.OnDataArrivedListener() {
        override fun onRoomResult(pre: Char, msg: String, host: String) {
            swipeRefresh.isRefreshing = false
            val room = Gson().fromJson<Room>(msg, Room::class.java)
            adapter?.removeData(room)
            //房间人数大于0才加入列表
            if (room.getUserCount() > 0) {
                adapter?.addData(room)
            }
        }
    }
}
