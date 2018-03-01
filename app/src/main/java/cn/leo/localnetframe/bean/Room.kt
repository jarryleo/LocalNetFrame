package cn.leo.localnetframe.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson
import java.util.*

/**
 * Created by Leo on 2018/2/28.
 */
class Room() : Parcelable {
    //房间ID
    var id = "0"
    //房间状态 0 等待开始游戏 1 正在游戏
    var state = 0
    //房间内用户列表
    var users = Collections.synchronizedList(ArrayList<User>())

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        state = parcel.readInt()
        parcel.readList(users, this.javaClass.classLoader)
    }

    //用户进入房间
    fun addUser(user: User) {
        val has = users.find { it.ip == user.ip }
        if (has == null) {
            users.add(user)
        }
    }

    //用户李凯房间
    fun removeUser(user: User) {
        users.remove(user)
    }

    //房间内用户数
    fun getUserCount() = users.size

    override fun equals(other: Any?): Boolean {
        val o: Room = (other as? Room)!!
        return o.id == id
    }

    override fun toString(): String {
        return Gson().toJson(this)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeInt(state)
        parcel.writeList(users)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Room> {
        override fun createFromParcel(parcel: Parcel): Room {
            return Room(parcel)
        }

        override fun newArray(size: Int): Array<Room?> {
            return arrayOfNulls(size)
        }
    }
}