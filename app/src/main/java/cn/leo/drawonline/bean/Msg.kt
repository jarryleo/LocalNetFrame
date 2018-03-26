package cn.leo.drawonline.bean

import com.google.gson.Gson

/**
 * Created by Leo on 2018/3/2.
 */
data class Msg(var name: String, var msg: String, var isAnswer: Boolean = false) {
    override fun toString(): String {
        return Gson().toJson(this)
    }
}