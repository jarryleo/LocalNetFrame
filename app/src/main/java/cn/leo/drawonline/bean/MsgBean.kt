package cn.leo.drawonline.bean

import com.google.gson.Gson

class MsgBean {
    var type: Int = 0 // 消息类型
    var msg: String? = null // 消息内容
    var id: Int = 0 // 消息id
    var code: Int = 0 // 消息错误码
    var time: Long = 0 // 消息时间
    var area: Int = 0 // 房间号 如果有群或者房间的话

    override fun toString(): String {
        return Gson().toJson(this)
    }
}
