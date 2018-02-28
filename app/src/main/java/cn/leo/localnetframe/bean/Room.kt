package cn.leo.localnetframe.bean

import com.google.gson.Gson
import java.util.*

/**
 * Created by Leo on 2018/2/28.
 */
class Room {
    var id = "0"
    var users = Collections.synchronizedList(ArrayList<User>())

    fun addUser(user: User) {
        val has = users.find { it.ip == user.ip }
        if (has == null) {
            users.add(user)
        }
    }

    fun removeUser(user: User) {
        users.remove(user)
    }

    fun getUserCount() = users.size

    override fun toString(): String {
        return Gson().toJson(this)
    }
}