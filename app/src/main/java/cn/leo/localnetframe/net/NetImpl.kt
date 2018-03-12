package cn.leo.localnetframe.net

import android.content.Context
import android.util.Log
import cn.leo.localnetframe.bean.User
import com.google.gson.Gson

/**
 * Created by Leo on 2018/3/12.
 */
class NetImpl(context: Context) : NetInterFace() {
    private val roomManager = RoomManager(context)


    override fun onFindRoom(pre: Char, msg: String, host: String) {
        if (roomManager.getRoomUserCount() > 0) {
            Log.e("host = ", host)
            roomResult(roomManager.getRoomJson(), host)
        }
    }

    override fun onJoinRoom(pre: Char, msg: String, host: String) {
        roomManager.addUser(User(host, msg))
    }

    override fun onUpdateScore(pre: Char, msg: String, host: String) {
        val user = Gson().fromJson<User>(msg, User::class.java)
        roomManager.getRoomUsers().find { it.ip == user.ip }?.score = user.score
    }

    override fun onExitRoom(pre: Char, msg: String, host: String) {
        roomManager.removeUser(User(host, msg))
    }

    override fun onNextPainter(pre: Char, msg: String, host: String) {
        roomManager.getRoom().next()
    }

    override fun onHeart(pre: Char, msg: String, host: String) {
        roomManager.getSendMsgUser(host)?.heart = msg.toLong()
    }


}