package cn.leo.aio.utils

import java.text.SimpleDateFormat
import java.util.*

object Logger {
    const val debug = true
    private val currentTime: String
        get() {
            val sdf = SimpleDateFormat("[yyyy-MM-dd HH:mm:ss-SSS]")
            return sdf.format(Date())
        }

    fun i(msg: String) {
        print(currentTime + "Information:" + msg)
    }

    fun d(msg: String) {
        print(currentTime + "Debug:" + msg)
    }

    fun e(msg: String) {
        print(currentTime + "Error:" + msg)
    }

    fun print(msg: String) {
        if (debug) {
            println(msg)
        }
    }

}
