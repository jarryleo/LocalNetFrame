package cn.leo.drawonline.utils

import android.content.Context

/**
 * Created by Leo on 2018/3/14.
 */
private const val sp_name = "config"
private const val sp_mode = Context.MODE_PRIVATE

fun Context.put(key: String, value: Any) {
    val sp = this.getSharedPreferences(sp_name, sp_mode)
    val edit = sp.edit()
    when (value) {
        is String -> edit.putString(key, value)
        is Int -> edit.putInt(key, value)
        is Long -> edit.putLong(key, value)
        is Boolean -> edit.putBoolean(key, value)
    }
    edit.apply()
}

fun <T> Context.get(key: String, value: T): T {
    val sp = this.getSharedPreferences(sp_name, sp_mode)
    return when (value) {
        is String -> sp.getString(key, value)
        is Int -> sp.getInt(key, value)
        is Long -> sp.getLong(key, value)
        is Boolean -> sp.getBoolean(key, value)
        else -> value
    } as T
}