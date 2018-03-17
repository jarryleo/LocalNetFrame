package cn.leo.localnetframe.net

import android.content.Context
import cn.leo.localnet.manager.WifiLManager
import cn.leo.localnetframe.bean.Room
import cn.leo.localnetframe.bean.User
import cn.leo.localnetframe.utils.get

/**
 * Created by Leo on 2018/3/12.
 */
class RoomManager(context: Context) {
    private var room: Room = Room()
    private val me: User
    private val ip: String = WifiLManager.getLocalIpAddress(context)
    private val preIp: String
    private val lastIp: String

    init {
        val lastIndexOf = ip.lastIndexOf(".")
        preIp = ip.substring(0, lastIndexOf)
        lastIp = ip.substring(lastIndexOf + 1)
        val nickname = context.get("nickname", "灵魂画手$lastIp")
        val icon = context.get("icon", 3)
        me = User(ip, nickname)
        me.icon = icon
        initRoom()
    }

    /**
     * 初始化房间
     */
    private fun initRoom() {
        room.users.clear()
        room.id = lastIp
        room.state = 0
        room.painter = 0
    }

    /**
     * 创建房间，清空所有玩家，数据归零，把自己加入房间
     */
    fun createRoom() {
        initRoom()
        addUser(me)
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
    }

    /**
     *自己退出房间
     */
    fun exitRoom() {
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
    fun getRoomPainter() = if (room.users.size > room.painter) {
        room.users[room.painter]
    } else {
        me
    }

    /**
     * 我是不是房主，房主才能开始游戏
     */
    fun meIsRoomOwner() = room.users[0] == me

    /**
     * 判断自己是不是画画的人
     */
    fun meIsPainter() = me == getRoomPainter()

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
}