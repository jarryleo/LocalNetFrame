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
    //房间状态 0 等待开始游戏  1-n 表示游戏进行到第几轮
    var state = 0
    //正在画画的玩家序号
    var painter = 0
    //房间内用户列表
    var users = Collections.synchronizedList(ArrayList<User>())

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        state = parcel.readInt()
        painter = parcel.readInt()
        parcel.readList(users, this.javaClass.classLoader)
    }

    //用户进入房间
    fun addUser(user: User) {
        val find = users.find { it.ip == user.ip }
        if (find == null) {
            users.add(user)
        }
    }

    //用户离开房间
    fun removeUser(user: User) {
        users.remove(user)
    }

    //房间内用户数
    fun getUserCount() = users.size

    //获取当前绘画玩家
    fun getCurrentUser() = users[painter]

    //获取下一个画画的玩家
    fun getNextUser() = if (painter >= getUserCount() - 1) {
        users[0]
    } else {
        users[painter + 1]
    }

    //控制权移交到下一个玩家(判断玩家不是离线状态 TODO)
    fun next() {
        painter = if (painter >= getUserCount() - 1) {
            state++
            0
        } else {
            painter + 1
        }
    }

    override fun equals(other: Any?): Boolean {
        val o: Room = (other as? Room)!!
        return o.id == id
    }

    //对象转json
    override fun toString(): String {
        return Gson().toJson(this)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeInt(state)
        parcel.writeInt(painter)
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