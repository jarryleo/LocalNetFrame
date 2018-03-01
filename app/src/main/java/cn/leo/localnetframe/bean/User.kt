package cn.leo.localnetframe.bean

/**
 * Created by Leo on 2018/2/28.
 */
data class User(var ip: String, var name: String, var heart: Int = 0) {
    override fun equals(other: Any?): Boolean {
        val user = other as? User
        return ip == user?.ip
    }
}