package cn.leo.drawonline.bean

import com.google.gson.Gson

class UserBean {
    //用户所在房间
    var room: RoomBean? = null
    //用户姓名
    var userName: String? = null
    //用户ip
    var ip: String? = null
    //用户id
    var userId: Int = 0
    //用户性别
    var sex: Int = 0
    //用户头像
    var icon: Int = 0
    //用户积分
    var score: Int = 0

    override fun toString(): String {
        return Gson().toJson(this)
    }
}
