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

    interface OnMsgArrivedListener {
        fun onMsgArrived(data: String)
    }

    /**
     * 设置自己的昵称
     */
    fun setMeName(name: String) {
        me.name = name
    }

    /**
     * 获取我的房间id
     */
    fun getMeRoomId() = room.id

    /**
     *获取房间内用户列表
     */
    fun getRoomUsers() = room.users

    /**
     * 设置数据回调监听
     */
    fun setDataListener(listener: OnMsgArrivedListener) {
        this.listener = listener
    }

    /**
     * 开启网络服务
     */
    fun startNet() {
        if (udpFrame == null) {
            udpFrame = UdpFrame(this)
            udpFrame!!.start()
        }
    }

    /**
     * 停止网络服务
     */
    fun stopNet() = this.udpFrame?.stopNet()

    /**
     * 底层网络数据返回
     */
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

    /**
     * 返回的数据在子线程，这里切换到主线程
     */
    private val handler = Handler(Looper.getMainLooper()) {
        val data = it.data
        val length = data.getInt("length")
        val byteArray = data.getByteArray("data")
        decode(byteArray, length)
        true
    }

    /**
     * 发送数据到房间内其他人
     */
    fun sendData(data: ByteArray) {
        room.users
                .takeWhile { it.ip != me.ip }
                .forEach { udpFrame!!.send(data, it.ip) }
    }

    /**
     * 发送数据给指定目标
     */
    fun sendData(data: ByteArray, host: String) {
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
        this.room = room
        room.users.forEach { udpFrame!!.send("J${me.name}".toByteArray(), it.ip) }
        room.addUser(me)
    }

    /**
     * 退出房间
     */
    fun exitRoom() {
        room.removeUser(me)
        sendData("E".toByteArray())
    }

    /**
     * 判断自己是不是房主(列表第一人才有权开始游戏)
     */
    fun isAdmin(): Boolean = room.users.indexOf(me) == 0

    /**
     * 判断是否正在游戏中
     */
    fun isGaming(): Boolean = room.state == 1

    /**
     * 开始游戏
     */
    fun startGame() {
        room.state = 1
        sendData("S".toByteArray())
    }

    /**
     * 游戏结束
     */
    fun stopGame() {
        room.state = 0
    }

    /**
     * 解码信息
     * F 查找房间
     * R 返回房间信息
     * J 申请加入房间
     * S 开始游戏
     * E 退出房间
     * C 聊天
     * P 画画
     * H 心跳
     * 其它 包括 房间信息分布记录，分数记录等再考虑
     */
    private fun decode(data: ByteArray, length: Int) {
        val str = String(data, 0, length)

        when (str.first()) {
            'F' -> {
                if (room.users.size > 0) {
                    sendData(("R" + room.toString()).toByteArray(), host)
                }
            }
            'H' -> {
                room.users.takeWhile { it.ip == host }.forEach { it.heart = 0 }
            }
            'J' -> {
                room.addUser(User(host, str.substring(1)))
                listener?.onMsgArrived(str)
            }
            'E' -> {
                room.removeUser(User(host, str.substring(1)))
                listener?.onMsgArrived(str)
            }
            else -> {
                listener?.onMsgArrived(str)
            }
        }
    }
}