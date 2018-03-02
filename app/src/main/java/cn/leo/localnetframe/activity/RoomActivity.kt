package cn.leo.localnetframe.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import cn.leo.localnet.utils.ToastUtilK
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
            refreshUsers()
            if (netManager.isGaming()) {
                title = getString(R.string.room_id, netManager.getMeRoomId()) + "(游戏中)"
                btnStartGame.text = "加入游戏"
            }
        } else {
            title = getString(R.string.room_id, netManager.getMeRoomId())
        }
    }

    override fun onRestart() {
        super.onRestart()
        netManager = MyApplication.getNetManager(this)
    }

    private fun initView() {
        adapter = UserListAdapter()
        refreshUsers()
        rvUserList.layoutManager =
                GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        rvUserList.adapter = adapter
        btnStartGame.setOnClickListener {
            startGame()
        }
    }

    private fun refreshUsers() {
        adapter?.clearData()
        netManager.getRoomUsers().forEach {
            adapter?.addData(it)
        }
    }

    private fun startGame() {
        if (netManager.isGaming()) {
            startActivity(Intent(this, PaintActivity::class.java))
        } else if (netManager.getRoomUsers().size > 1) {
            if (netManager.isAdmin()) {
                //发送开始游戏指令
                netManager.startGame()
                //跳转到游戏界面
                startActivity(Intent(this, PaintActivity::class.java))
            } else {
                ToastUtilK.show(this, "排在第一位置的人才能开始游戏")
            }
        } else {
            ToastUtilK.show(this, "最少两人才能开始游戏")
            startActivity(Intent(this, PaintActivity::class.java))
        }
    }

    override fun onMsgArrived(data: String) {
        when (data.first()) {
            'J' -> {
                refreshUsers()
            }
            'E' -> {
                refreshUsers()
            }
            'S' -> {
                startActivity(Intent(this, PaintActivity::class.java))
            }
            else -> {

            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        //退出房间
        netManager.exitRoom()
    }


}
