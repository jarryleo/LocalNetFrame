package cn.leo.drawonline.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import cn.leo.drawonline.MyApplication
import cn.leo.drawonline.R
import cn.leo.drawonline.adapter.RoomListAdapter
import cn.leo.drawonline.bean.Icons
import cn.leo.drawonline.bean.MsgBean
import cn.leo.drawonline.bean.UserBean
import cn.leo.drawonline.constant.MsgCode
import cn.leo.drawonline.constant.MsgType
import cn.leo.drawonline.net.NetManager
import cn.leo.drawonline.utils.Config
import cn.leo.drawonline.utils.get
import cn.leo.localnet.utils.ToastUtilK
import cn.leo.nio_client.core.ClientListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_room_list.*

class RoomListActivity : AppCompatActivity(), ClientListener {

    private val netManager = NetManager()
    private var adapter: RoomListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_list)
        initView()
        initData()
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
        adapter?.clearData()
        netManager?.findRoom()
        initData()
    }

    private fun initData() {
        val name = get(Config.NICKNAME, "无名")
        tvName.text = name
        val score = get(Config.SCORE, 0).toString()
        tvScore.text = getString(R.string.room_list_user_score, score)
        val icon = get(Config.ICON, 0)
        ivHeadIcon.setImageResource(Icons.getIconList()[icon])
        //登录
        netManager.login(name)
    }

    private fun initView() {
        title = "当前WIFI网络房间列表"

        adapter = RoomListAdapter()
        swipeRefresh.setOnRefreshListener {
            adapter?.clearData()
            netManager.findRoom()
            refresh()
        }
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter

        netManager.findRoom()
        swipeRefresh.isRefreshing = true
        refresh()

        //创建房间
        fab.setOnClickListener {
            //            if (swipeRefresh.isRefreshing) {
//                ToastUtilK.show(this, "等待扫描房间完成后才能创建房间")
//            } else {
//                //房间号码从1开始累计
//                var roomId = "1"
//                var find = adapter?.mList?.find { it.id == roomId }
//                while (find != null) {
//                    roomId = (roomId.toInt().plus(1)).toString()
//                    find = adapter?.mList?.find { it.id == roomId }
//                }
//                netManager.createRoom()
//                ToastUtilK.show(this, "创建房间成功，房间号${netManager?.getRoomId()}")
//                startActivity(Intent(this@RoomListActivity, RoomActivity::class.java))
//            }
        }
    }

    private fun refresh() {
        handler.postDelayed(runnable, 5000)
    }

    private val handler = Handler()
    private val runnable = Runnable {
        if (swipeRefresh.isRefreshing) {
            swipeRefresh.isRefreshing = false
            if (adapter?.itemCount == 0) {
                ToastUtilK.show(this, "没有搜索到房间，请下拉重试或者创建房间")
            } else {
                ToastUtilK.show(this, "找到${adapter?.itemCount}个房间")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }

    /*  */
    /**
     * 接受到数据
     *//*
    inner class DataReceiver : NetInterFace.OnDataArrivedListener() {
        override fun onRoomResult(pre: Char, msg: String, host: String) {
            //swipeRefresh.isRefreshing = false
            val room = Gson().fromJson<Room>(msg, Room::class.java)
            adapter?.removeData(room)
            //房间人数大于0才加入列表
            if (room.getUserCount() > 0) {
                adapter?.addData(room)
            }
        }
    }*/

    override fun onConnectSuccess() {
        ToastUtilK.show(this, "连接服务器成功")
        //登录
        netManager.login(tvName.text.toString())
    }

    override fun onConnectFailed() {
        ToastUtilK.show(this, "连接服务器失败")
    }

    override fun onIntercept() {
        ToastUtilK.show(this, "被服务器拒绝")
    }

    override fun onDataArrived(data: ByteArray?) {
        val msg = String(data!!)
        val msgBean = Gson().fromJson<MsgBean>(msg, MsgBean::class.java)
        if (msgBean.type == MsgType.LOGIN.getType()) {
            if (msgBean.code == MsgCode.LOG_FAI.code) {
                //登录失败
                ToastUtilK.show(this, "登录失败")
            } else if (msgBean.code == MsgCode.LOG_SUC.code) {
                //登录成功
                logSuccess()
                val json = msgBean.msg
                val userBean = Gson().fromJson(json, UserBean::class.java)
                MyApplication.setUser(userBean)
            }
        }
    }

    private fun logSuccess() {
        //请求房间列表
        ToastUtilK.show(this, "登录成功")

    }

}
