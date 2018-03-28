package cn.leo.drawonline.bean

import com.google.gson.Gson
import java.util.*

class RoomBean {
    //房间内的玩家列表
    var users = Collections.synchronizedList(ArrayList<UserBean>())
    //房间id
    var roomId: Int = 0
    //房间正在画画的词汇
    var word: String? = null
    //画画的词汇提示语
    var wordTips: String? = null
    //房间状态，0为未开始游戏，1-n为进行到第n轮
    var roomState: Int = 0
    //房主
    var roomOwner: UserBean? = null
    //当前房间绘画人
    var roomPainter: UserBean? = null
    //当前画画倒计时
    var paintCountDown: Int = 0
    //获取房间内所有人列表

    //获取房间总人数

    val userCount: Int
        get() = users.size

    override fun toString(): String {
        return Gson().toJson(this)
    }
}
