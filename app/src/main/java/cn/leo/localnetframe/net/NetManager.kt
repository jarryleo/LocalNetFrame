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
import com.google.gson.Gson

/**
 * Created by Leo on 2018/2/27.
 */
class NetManager(private var context: Context) : UdpFrame.OnDataArrivedListener {
    private var listener: OnMsgArrivedListener? = null
    private var room: Room = Room()
    private var me: User
    private var preIp: String
    private var lastIp: String
    private var udpFrame: UdpFrame? = null

    init {
        val ip = WifiLManager.getLocalIpAddress(context)
        val lastIndexOf = ip.lastIndexOf(".")
        preIp = ip.substring(0, lastIndexOf)
        lastIp = ip.substring(lastIndexOf + 1)
        me = User(ip, "灵魂画手$lastIp")
        clearRoom()
    }

    /**
     * 打扫房间
     */
    private fun clearRoom() {
        room.users.clear()
        room.id = lastIp
        room.state = 0
        room.painter = 0
    }

    /**
     * 数据接收回调接口
     */
    interface OnMsgArrivedListener {
        fun onMsgArrived(data: String, host: String)
    }

    /**
     * 设置自己的昵称
     */
    fun setMeName(name: String) {
        me.name = name
    }

    /**
     * 获取我的对象
     */
    fun getMe() = me

    /**
     *获取自己的昵称
     */
    fun getMeName() = me.name

    /**
     * 获取我的房间id
     */
    fun getMeRoomId() = room.id

    /**
     *获取房间内用户列表
     */
    fun getRoomUsers() = room.users

    /**
     * 获取当前画画的人
     */
    fun getRoomPainter() = room.users[room.painter]

    /**
     * 找到刚刚发消息的人(进入房间之前的返回null)
     */
    fun getSendMsgUser(host: String) = getRoomUsers().find { it.ip == host }

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
     * 创建房间
     */
    fun createRoom() {
        clearRoom()
        room.addUser(me)
        sendData(("R" + room.toString()).toByteArray(), preIp + ".255")
    }

    /**
     * 查找房间
     */
    fun findRoom() {
        udpFrame!!.send("F".toByteArray(), "$preIp.255")
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
        clearRoom()
        sendData("E")
    }

    /**
     * 判断自己是不是房主(列表第一人才有权开始游戏)
     */
    fun isAdmin(): Boolean = room.users.indexOf(me) == 0

    /**
     * 判断是否正在游戏中
     */
    fun isGaming(): Boolean = room.state > 0

    /**
     * 是否是我在画画
     */
    fun isMePlaying(): Boolean = getRoomPainter() == me

    /**
     * 开始游戏
     */
    fun startGame() {
        room.state = 1
        sendData("S")
    }

    /**
     * 下一个玩家开始
     */
    fun nextPainter() {
        room.painter = if (room.painter >= room.users.size - 1) {
            0
        } else {
            room.painter + 1
        }
        sendData("N")
    }

    /**
     * 游戏结束
     */
    fun stopGame() {
        room.state = 0
    }

    /**
     * 发送数据到房间内其他人
     */
    fun sendData(data: ByteArray) {
        room.users
                .filterNot { it.ip == me.ip }
                .forEach { udpFrame!!.send(data, it.ip) }
    }

    /**
     * 发送文本信息
     */
    fun sendData(data: String) {
        sendData(data.toByteArray())
    }

    /**
     * 发送数据给指定目标
     */
    fun sendData(data: ByteArray, host: String) {
        udpFrame!!.send(data, host)
    }

    /**
     * 发送数据给画画的人
     */
    fun sendToPainter(data: ByteArray) {
        udpFrame!!.send(data, getRoomPainter().ip)
    }

    /**
     * 底层网络数据返回
     */
    override fun onDataArrived(data: ByteArray, length: Int, host: String) {
        Log.d("host", host)
        val message = Message.obtain()
        val bundle = Bundle()
        bundle.putString("host", host)
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
        val host = data.getString("host")
        val length = data.getInt("length")
        val byteArray = data.getByteArray("data")
        decode(byteArray, length, host)
        true
    }


    /**
     * 解码信息
     * A 显示答案
     * F 查找房间
     * R 返回房间信息
     * J 申请加入房间
     * S 开始游戏
     * U 同步积分
     * T 提示内容
     * D 倒计时
     * E 退出房间
     * N 下一个开始游戏
     * C 聊天
     * P 画画
     * H 心跳
     * 其它 包括 房间信息分布记录，分数记录等再考虑
     */
    private fun decode(data: ByteArray, length: Int, host: String) {
        val str = String(data, 0, length)
        Log.e("receive = ", str.first().toString())
        when (str.first()) {
            'F' -> {
                if (room.users.size > 0) {
                    Log.e("host = ", host)
                    sendData(("R" + room.toString()).toByteArray(), host)
                }
            }
            'H' -> {
                room.users.takeWhile { it.ip == host }
                        .forEach { it.heart = System.currentTimeMillis() }
            }
            'J' -> {
                room.addUser(User(host, str.substring(1)))
                listener?.onMsgArrived(str, host)
            }
            'E' -> {
                room.removeUser(User(host, str.substring(1)))
                listener?.onMsgArrived(str, host)
            }
            'U' -> {
                val user = Gson().fromJson<User>(str.substring(1), User::class.java)
                getRoomUsers().find { it.ip == user.ip }?.score = user.score
                listener?.onMsgArrived(str, host)
            }
            'N' -> {
                room.painter = if (room.painter >= room.users.size - 1) {
                    0
                } else {
                    room.painter + 1
                }
                listener?.onMsgArrived(str, host)
            }
            else -> {
                listener?.onMsgArrived(str, host)
            }
        }
    }
}