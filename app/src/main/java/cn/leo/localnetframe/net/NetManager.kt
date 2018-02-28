package cn.leo.localnetframe.net

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import cn.leo.localnet.manager.WifiLManager
import cn.leo.localnet.net.UdpFrame
import cn.leo.localnetframe.bean.Room
import cn.leo.localnetframe.bean.User

/**
 * Created by Leo on 2018/2/27.
 */
class NetManager(private var context: Context) : UdpFrame.OnDataArrivedListener {
    private var listener: OnMsgArrivedListener? = null
    private var room: Room = Room()
    private var me: User
    private var preIp: String
    private var udpFrame: UdpFrame? = null
    private var host: String = "192.168.0.180"

    init {
        val ip = WifiLManager.getLocalIpAddress(context)
        val lastIndexOf = ip.lastIndexOf(".")
        preIp = ip.substring(0, lastIndexOf)
        val lastIp = ip.substring(lastIndexOf + 1)
        room.id = lastIp
        me = User(ip, "灵魂画手$lastIp")
    }

    /**
     * 设置自己的昵称
     */
    fun setMeName(name: String) {
        me.name = name
    }


    interface OnMsgArrivedListener {
        fun onMsgArrived(data: String)
    }

    fun setDataListener(listener: OnMsgArrivedListener) {
        this.listener = listener
    }

    fun startNet() {
        if (udpFrame == null) {
            udpFrame = UdpFrame(this)
            udpFrame!!.start()
        }
    }

    fun stopNet() = this.udpFrame?.stopNet()

    override fun onDataArrived(data: ByteArray, length: Int, host: String) {
        this.host = host
        Log.d("host", host)
        //decode(data, length)
        val message = Message.obtain()
        val bundle = Bundle()
        bundle.putInt("length", length)
        bundle.putByteArray("data", data)
        message.data = bundle
        handler.sendMessage(message)
    }

    private val handler = Handler(Looper.getMainLooper()) {
        val data = it.data
        val length = data.getInt("length")
        val byteArray = data.getByteArray("data")
        decode(byteArray, length)
        true
    }


    fun sendData(data: ByteArray) {
        udpFrame!!.send(data, host)
    }

    /**
     * 创建房间
     */
    fun createRoom() {
        room.addUser(me)
    }

    /**
     * 查找房间
     */
    fun findRoom() {
        udpFrame!!.send("F".toByteArray(), preIp + ".255")
    }

    /**
     * 申请加入房间
     */
    fun joinRoom(room: Room) {
        room.users.forEach { udpFrame!!.send("J${me.name}".toByteArray(), it.ip) }
    }

    /**
     * 解码信息
     * F 查找房间
     * R 返回房间信息
     * J 加入房间
     * C 聊天
     * P 画画
     * H 心跳
     * 其它 包括 房间信息分布记录，分数记录等再考虑
     */
    private fun decode(data: ByteArray, length: Int) {
        val str = String(data, 0, length)

        when (str.first()) {
            'F' -> {
                sendData(("R" + room.toString()).toByteArray())
            }
            'H' -> {
                room.users.takeWhile { it.ip == host }.forEach { it.heart = 0 }
            }
            'J' -> {
                room.addUser(User(host, str.substring(1)))
            }
            else -> {
                listener?.onMsgArrived(str)
            }
        }
    }
}