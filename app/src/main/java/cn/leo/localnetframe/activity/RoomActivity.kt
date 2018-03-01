package cn.leo.localnetframe.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import cn.leo.localnetframe.MyApplication
import cn.leo.localnetframe.R
import cn.leo.localnetframe.adapter.UserListAdapter
import cn.leo.localnetframe.bean.Room
import cn.leo.localnetframe.net.NetManager
import kotlinx.android.synthetic.main.activity_room.*

class RoomActivity : AppCompatActivity(), NetManager.OnMsgArrivedListener {
    private lateinit var netManager: NetManager
    private var adapter: UserListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)
        netManager = MyApplication.getNetManager(this)
        initView()
        initData()
    }

    private fun initData() {
        val room = intent.getParcelableExtra<Room>("room")
        if (room != null) {
            netManager.joinRoom(room)
        }
        title = getString(R.string.room_id, netManager.getMeRoomId())
    }

    override fun onRestart() {
        super.onRestart()
        netManager = MyApplication.getNetManager(this)
    }

    private fun initView() {
        adapter = UserListAdapter()
        netManager.getRoomUsers().forEach {
            adapter?.addData(it)
        }
        rvUserList.layoutManager =
                GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        rvUserList.adapter = adapter

        btnStartGame.setOnClickListener { startActivity(Intent(this, PaintActivity::class.java)) }
    }

    override fun onMsgArrived(data: String) {
        when (data.first()) {
            'J' -> {
                adapter?.notifyDataSetChanged()
            }
            'E' -> {
                adapter?.notifyDataSetChanged()
            }
            else -> {

            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        //退出房间
        //netManager.exitRoom()
    }


}
