package cn.leo.localnetframe.net

import android.content.Context
import android.os.Handler
import android.util.Log
import cn.leo.localnet.manager.ApManager
import cn.leo.localnetframe.bean.Room
import cn.leo.localnetframe.bean.User
import com.google.gson.Gson

/**
 * Created by Leo on 2018/3/12.
 */
class NetImpl(context: Context) : NetInterFace() {
    private val roomManager = RoomManager(context)
    private val handler = Handler()
    private lateinit var heartTask: Runnable

    init {
        //心跳任务
        heartTask = Runnable {
            if (roomManager.getRoomUserCount() > 1) {
                sendMsgOther("", ::heart)
            }
            roomManager.getMe().heart = System.currentTimeMillis()
            handler.removeCallbacks(heartTask)
            handler.postDelayed(heartTask, 2000)
            if (!ApManager.isApOn(context)) {
                broadCastApInfo()
            }
        }
        handler.postDelayed(heartTask, 2000)
    }

    /**
     * 重置网络
     */
    fun initNetWork(context: Context) {
        roomManager.initNet(context)
    }

    /**
     * 停止网络
     */
    override fun stopNet() {
        handler.removeCallbacks(heartTask)
        super.stopNet()
    }

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
        //如果房主掉线则发消息给下一个画画的人，下一个画画的人接管信息中转
        if (roomManager.getRoomPainter().isOffline()) {
            sendData(data, roomManager.getNextPainter().ip)
        } else {
            sendData(data, roomManager.getRoomPainter().ip)
        }
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
    fun createRoom(id: String = "") {
        roomManager.createRoom(id)
        broadCastRoomInfo()
    }

    /**
     * 退出房间
     */
    fun exitRoom() {
        //如果房间只有我，那么广播这个房间消失信息
        if (roomManager.getRoomUserCount() == 1) {
            roomManager.exitRoom()
            broadCastRoomInfo()
        } else {
            sendMsgOther("", ::exitRoom)
            roomManager.exitRoom()
        }
    }

    /**
     * 加入房间
     */
    fun joinRoom(room: Room) {
        roomManager.joinRoom(room)
        sendMsgOther(roomManager.getMe().toString(), ::joinRoom)
        //broadCastRoomInfo()
    }

    /**
     * 同步房间信息
     */
    fun uploadRoomInfo(room: Room) {
        roomManager.joinRoom(room)
    }

    /**
     * 获取房间
     */
    fun getRoom() = roomManager.getRoom()

    /**
     * 获取房间号
     */
    fun getRoomId() = roomManager.getRoom().id

    /**
     *获取第几轮
     */
    fun getRoomRound() = roomManager.getRoom().state

    /**
     * 获取自己的名字
     */
    fun getMeName() = roomManager.getMe().name

    /**
     * 获取自己的IP
     */
    fun getMeIp() = roomManager.getMeIp()

    /**
     * 获取自己对象
     */
    fun getMe() = roomManager.getMe()

    /**
     * 设置自己的名字
     */
    fun setMeName(name: String) {
        roomManager.getMe().name = name
    }

    /**
     * 设置自己的头像
     */
    fun setMeIcon(icon: Int) {
        roomManager.getMe().icon = icon
    }

    /**
     *获取房间内玩家
     */
    fun getRoomUsers() = roomManager.getRoomUsers()

    /**
     *获取我的IP尾号
     */
    fun getMeLastIp() = roomManager.getLastIp()

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
    fun meIsRoomOwner() = roomManager.meIsRoomOwner()

    /**
     * 判断自己是不是画画的人
     */
    fun meIsPainter() = roomManager.meIsPainter()

    /**
     * 判断自己是不是下一个画画的人
     */
    fun meIsNextPainter() = roomManager.meIsNextPainter()

    /**
     * 开始游戏
     */
    fun startGame() {
        roomManager.getRoom().state++
        sendMsgOther("", ::startGame)
        broadCastRoomInfo()
    }

    /**
     * 下一个人开始游戏
     */
    fun nextPainter() {
        roomManager.getRoom().next()
        sendMsgOther("", ::nextPainter)
    }

    /**
     * 送鲜花或拖鞋 F , S
     */
    fun sendOpinion(msg: String) {
        if (!meIsPainter()) {
            opinion(msg, roomManager.getRoomPainter().ip)
        }
    }

    /**
     * 获取当前画画的玩家
     */
    fun getPainter() = roomManager.getRoomPainter()

    /**
     * 结束游戏(先退出房间)
     */
    fun stopGame() {
        exitRoom()
    }

    /**
     * 广播房间信息，房间信息变化时主动推送：房间人数变化，房间状态变化
     */
    private fun broadCastRoomInfo() {
        roomResult(roomManager.getRoomJson(), roomManager.getBroadCastAddress())
    }

    /**
     * 广播热点IP段
     */
    private fun broadCastApInfo() {
        sendData("X${roomManager.getPreIp()}", "${roomManager.getPreIp()}.255")
    }

    /**
     * 收到查找房间指令
     * 只要房间内人数大于0 ，则系统自动应答
     */
    override fun onFindRoom(pre: Char, msg: String, host: String) {
        if (roomManager.getRoomUserCount() > 0 && host != roomManager.getMeIp()) {
            Log.e("host = ", host)
            roomResult(roomManager.getRoomJson(), host)
        }
    }


    /**
     * 收到加入房间指令，系统应答
     */
    override fun onJoinRoom(pre: Char, msg: String, host: String) {
        val user = Gson().fromJson<User>(msg, User::class.java)
        roomManager.addUser(user)
        //我是房主，广播一次房间信息变化
        if (roomManager.meIsRoomOwner()) {
            broadCastRoomInfo()
        }
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
        roomManager.getSendMsgUser(host)?.heart = System.currentTimeMillis()
    }

    /**
     * 更新ip
     */
    override fun onGetApHost(first: Char, body: String, host: String) {
        if (body != roomManager.getMeIp()) {
            roomManager.setIp(body)
        }
    }
}