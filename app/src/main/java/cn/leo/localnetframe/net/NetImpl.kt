package cn.leo.localnetframe.net

import android.content.Context
import android.util.Log
import cn.leo.localnetframe.bean.Room
import cn.leo.localnetframe.bean.User
import com.google.gson.Gson

/**
 * Created by Leo on 2018/3/12.
 */
class NetImpl(context: Context) : NetInterFace() {
    private val roomManager = RoomManager(context)

    /**
     * 发送消息给除了自己外房间的其他人
     */
    fun sendMsgOther(data: String) {
        roomManager.getRoomUsers()
                .filterNot { it.ip == roomManager.getMe().ip }
                .forEach { sendData(data, it.ip) }
    }

    /**
     * 发送消息给除了自己外房间的其他人
     */
    private fun sendMsgOther(data: String, action: (msg: String, host: String) -> Unit) {
        roomManager.getRoomUsers()
                .filterNot { it.ip == roomManager.getMe().ip }
                .forEach { action(data, it.ip) }
    }


    /**
     * 发送消息给画画的人
     */
    fun sendMsgPainter(data: String) {
        sendData(data, roomManager.getRoomPainter().ip)
    }

    /**
     *查找房间指令
     */
    fun findRoom() {
        findRoom(host = roomManager.getBroadCastAddress())
    }

    /**
     * 创建房间
     */
    fun createRoom() {
        roomManager.createRoom()
    }

    /**
     * 退出房间
     */
    fun exitRoom() {
        sendMsgOther("", ::exitRoom)
    }

    /**
     * 加入房间
     */
    fun joinRoom(room: Room) {
        sendMsgOther(room.toString(), ::joinRoom)
    }

    /**
     * 获取房间号
     */
    fun getRoomId() = roomManager.getRoom().id

    /**
     * 获取自己的名字
     */
    fun getMeName() = roomManager.getMe().name

    /**
     *获取房间内玩家
     */
    fun getRoomUsers() = roomManager.getRoomUsers()

    /**
     * 找到刚刚发消息的人(进入房间之前的返回null)
     */
    fun getSendMsgUser(host: String) = getRoomUsers().find { it.ip == host }


    /**
     * 房间是否正在游戏
     */
    fun isGaming() = roomManager.getRoom().state > 0

    /**
     * 我是不是房主，房主才能开始游戏
     */
    fun meIsRoomOwner() = roomManager.meIsPainter()

    /**
     * 判断自己是不是画画的人
     */
    fun meIsPainter() = roomManager.meIsPainter()

    /**
     * 开始游戏
     */
    fun startGame() {
        roomManager.getRoom().state++
        sendMsgOther("", ::startGame)
    }

    /**
     * 下一个人开始游戏
     */
    fun nextPainter() {
        sendMsgOther("", ::nextPainter)
    }

    /**
     * 结束游戏
     */
    fun stopGame() {
        roomManager.getRoom().state = 0
    }

    /**
     * 收到查找房间指令
     * 只要房间内人数大于0 ，则系统自动应答
     */
    override fun onFindRoom(pre: Char, msg: String, host: String) {
        if (roomManager.getRoomUserCount() > 0) {
            Log.e("host = ", host)
            roomResult(roomManager.getRoomJson(), host)
        }
    }

    /**
     * 收到加入房间指令，系统应答
     */
    override fun onJoinRoom(pre: Char, msg: String, host: String) {
        roomManager.addUser(User(host, msg))
    }

    /**
     * 收到同步分数指令，系统应答，界面需要更新
     */
    override fun onUpdateScore(pre: Char, msg: String, host: String) {
        val user = Gson().fromJson<User>(msg, User::class.java)
        roomManager.getRoomUsers().find { it.ip == user.ip }?.score = user.score
    }

    /**
     * 收到玩家退出房间指令，系统应答，界面需要更新
     */
    override fun onExitRoom(pre: Char, msg: String, host: String) {
        roomManager.removeUser(User(host, msg))
    }

    /**
     *  收到游戏轮到下一个玩家指令，系统应答
     */
    override fun onNextPainter(pre: Char, msg: String, host: String) {
        roomManager.getRoom().next()
    }

    /**
     * 收到心跳指令，系统处理
     */
    override fun onHeart(pre: Char, msg: String, host: String) {
        roomManager.getSendMsgUser(host)?.heart = msg.toLong()
    }


}