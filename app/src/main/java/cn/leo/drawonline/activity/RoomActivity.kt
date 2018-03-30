package cn.leo.drawonline.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import cn.leo.drawonline.R
import cn.leo.drawonline.adapter.UserListAdapter
import cn.leo.drawonline.bean.MsgBean
import cn.leo.drawonline.bean.RoomBean
import cn.leo.drawonline.constant.MsgCode
import cn.leo.drawonline.constant.MsgType
import cn.leo.drawonline.net.NetManager
import cn.leo.localnet.utils.ToastUtilK
import cn.leo.nio_client.core.ClientListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_room.*
import java.nio.charset.Charset

class RoomActivity : AppCompatActivity(), ClientListener {
    private var roomId: Int = -1
    private val netManager = NetManager()
    private var adapter: UserListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)
        initData()
        initView()
    }

    private fun initData() {
        roomId = intent.getIntExtra("room", -1)
        if (roomId != -1) {
            netManager.joinRoom(roomId)
        }
    }

    private fun initTitle(roomBean: RoomBean) {
        if (roomBean.roomState > 0) {
            title = getString(R.string.room_id, roomBean.roomId.toString()) + "(游戏中)"
            btnStartGame.text = "加入游戏"
        } else {
            title = getString(R.string.room_id, roomBean.roomId.toString())
        }
    }

    override fun onRestart() {
        super.onRestart()
        //刷新房间信息
        netManager.getRoomInfo()
    }

    private fun initView() {
        adapter = UserListAdapter()
        rvUserList.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvUserList.adapter = adapter
        btnStartGame.setOnClickListener {
            startGame()
        }
    }

    private fun refreshUsers(roomBean: RoomBean) {
        adapter?.setDatas(roomBean.users)
        initTitle(roomBean)
    }

    private fun startGame() {
        netManager.startGame()
    }

    override fun onConnectSuccess() {

    }

    override fun onConnectFailed() {

    }

    override fun onIntercept() {
        ToastUtilK.show(this, "服务器断开连接")
    }

    override fun onDataArrived(data: ByteArray?) {
        val msg = String(data!!, Charset.forName("utf-8"))
        if (msg.isEmpty()) return
        if (msg.first() == 'P') {
            return
        }
        val msgBean = Gson().fromJson<MsgBean>(msg, MsgBean::class.java)
        if (msgBean.type == MsgType.GAME.getType()) {
            if (msgBean.code == MsgCode.ROOM_JOIN_FAI.code) {
                //加入失败
                ToastUtilK.show(this, "加入房间失败")
                finish()
            } else if (msgBean.code == MsgCode.ROOM_JOIN_SUC.code ||
                    msgBean.code == MsgCode.ROOM_INFO.code) {
                //加入成功
                val json = msgBean.msg
                Log.e("roomJson", json)
                val roomBean = Gson().fromJson<RoomBean>(json, RoomBean::class.java)
                refreshUsers(roomBean)
            } else if (msgBean.code == MsgCode.GAME_START_SUC.code) {
                //游戏开始成功
                val intent = Intent(this, PaintActivity::class.java)
                startActivity(intent)
                finish()
            } else if (msgBean.code == MsgCode.GAME_START_FAIL.code) {
                //游戏开始失败
                val msgStr = msgBean.msg
                ToastUtilK.show(this, msgStr!!)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        //退出房间
        netManager.exitRoom()
    }


}
