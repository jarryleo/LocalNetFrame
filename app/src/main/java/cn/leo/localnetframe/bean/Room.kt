package cn.leo.localnetframe.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson
import java.util.*

/**
 * Created by Leo on 2018/2/28.
 */
class Room() : Parcelable {
    var id = "0"
    var users = Collections.synchronizedList(ArrayList<User>())

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
    }

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

    override fun equals(other: Any?): Boolean {
        val o: Room = (other as? Room)!!
        return o.id == id
    }

    override fun toString(): String {
        return Gson().toJson(this)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
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