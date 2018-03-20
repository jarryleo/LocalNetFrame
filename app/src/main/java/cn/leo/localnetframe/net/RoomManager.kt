package cn.leo.localnetframe.net

import android.content.Context
import cn.leo.localnet.manager.ApManager
import cn.leo.localnet.manager.WifiLManager
import cn.leo.localnet.utils.ToastUtilK
import cn.leo.localnetframe.bean.Room
import cn.leo.localnetframe.bean.User
import cn.leo.localnetframe.utils.Config
import cn.leo.localnetframe.utils.get

/**
 * Created by Leo on 2018/3/12.
 */
class RoomManager(context: Context) {
    private var room: Room = Room()
    private lateinit var me: User
    private lateinit var ip: String
    private lateinit var preIp: String
    private lateinit var lastIp: String

    init {
        initNet(context)
    }

    fun initNet(context: Context) {
        ip = if (ApManager.isApOn(context)) {
            ApManager.getHotspotIpAddress(context)
        } else {
            if (!WifiLManager.isWifiConnected(context)) {
                ToastUtilK.show(context, "连接WIFI或者开启热点后才能正常游戏")
            }
            WifiLManager.getLocalIpAddress(context)
        }
        val lastIndexOf = ip.lastIndexOf(".")
        preIp = ip.substring(0, lastIndexOf)
        lastIp = ip.substring(lastIndexOf + 1)
        val nickname = context.get(Config.NICKNAME, "灵魂画手$lastIp")
        val icon = context.get(Config.ICON, 3)
        me = User(ip, nickname)
        me.icon = icon
        initRoom()
    }

    /**
     * 初始化房间
     */
    private fun initRoom(id: String = lastIp) {
        room.users.clear()
        room.id = id
        room.state = 0
        room.painter = 0
    }

    /**
     * 创建房间，清空所有玩家，数据归零，把自己加入房间
     */
    fun createRoom(id: String = lastIp) {
        initRoom(id)
        addUser(me)
        me = room.users.find { it.ip == me.ip } ?: me
    }

    /**
     * 获取房间对象
     */
    fun getRoom() = room

    /**
     *加入别的房间
     */
    fun joinRoom(room: Room) {
        this.room = room
        addUser(me)
        me = room.users.find { it.ip == me.ip } ?: me
    }

    /**
     *自己退出房间
     */
    fun exitRoom() {
        me.score = 0
        initRoom()
    }

    /**
     * 获取房间json
     */
    fun getRoomJson() = room.toString()

    /**
     * 获取房间人数
     */
    fun getRoomUserCount() = room.getUserCount()

    /**
     * 获取房间玩家列表
     */
    fun getRoomUsers() = room.users

    /**
     * 获取当前画画的人
     */
    fun getRoomPainter() = room.getCurrentPainter()

    /**
     * 获取下一个画画的人
     */
    fun getNextPainter() = room.getNextPainter()

    /**
     * 我是不是房主，房主才能开始游戏
     */
    fun meIsRoomOwner() = room.users[0] == me

    /**
     * 判断自己是不是画画的人
     */
    fun meIsPainter() = me == room.getCurrentPainter()

    /**
     * 判断自己是不是下一个画画的人
     */
    fun meIsNextPainter() = me == room.getNextPainter()

    /**
     * 找到刚刚发消息的人(进入房间之前的返回null)
     */
    fun getSendMsgUser(host: String) = getRoomUsers().find { it.ip == host }

    /**
     * 添加玩家到房间
     */
    fun addUser(user: User) {
        room.addUser(user)
    }

    /**
     * 移除玩家
     */
    fun removeUser(user: User) {
        room.removeUser(user)
    }

    /**
     * 设置自己名称
     */
    fun setMeName(name: String) {
        me.name = name
    }

    /**
     * 设置自己头像
     */
    fun setMeIcon(icon: Int) {
        me.icon = icon
    }

    /**
     * 获取自己对象
     */
    fun getMe() = me

    /**
     * 获取自己的ip
     */
    fun getMeIp() = ip

    /**
     * 获取ip段前三位地址
     */
    fun getPreIp() = preIp

    /**
     * 获取ip最后一位数
     */
    fun getLastIp() = lastIp

    /**
     * 获取ip段广播地址
     */
    fun getBroadCastAddress() = "$preIp.255"

    /**
     * 设置自己的ip，根据其它客户端告知的热点主机ip
     */
    fun setIp(host: String) {
        ip = host
        me.ip = ip
        val lastIndexOf = ip.lastIndexOf(".")
        preIp = ip.substring(0, lastIndexOf)
        lastIp = ip.substring(lastIndexOf + 1)
    }
}