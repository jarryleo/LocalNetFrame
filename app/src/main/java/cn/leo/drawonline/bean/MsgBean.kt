package cn.leo.drawonline.bean

import cn.leo.drawonline.constant.MsgType
import com.google.gson.Gson

class MsgBean {
    var type: MsgType? = null // 消息类型
    var msg: String? = null // 消息内容
    var code: Int = 0 // 消息错误码
    var time: Long = 0 // 消息时间
    var area: Long = 0 // 房间号 如果有群或者房间的话

    init {
        time = System.currentTimeMillis()
    }

    override fun toString(): String {
        return Gson().toJson(this)
    }
}
