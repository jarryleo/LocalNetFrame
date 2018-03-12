package cn.leo.localnetframe.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson

/**
 * Created by Leo on 2018/2/28.
 */
data class User(var ip: String,
                var name: String,
                var heart: Long = 0,
                var score: Int = 0) : Parcelable {
    var icon = 0
    var state = 0

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readLong(),
            parcel.readInt()) {
        icon = parcel.readInt()
        state = parcel.readInt()
    }

    //重载+号为积分增长
    operator fun User.plus(s: Int): Int = apply { score += s }.score

    //是否掉线
    fun isOffline() = System.currentTimeMillis() - heart > 5L * 1024


    override fun equals(other: Any?): Boolean {
        val user = other as? User
        return ip == user?.ip
    }

    override fun toString(): String {
        return Gson().toJson(this)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(ip)
        parcel.writeString(name)
        parcel.writeLong(heart)
        parcel.writeInt(score)
        parcel.writeInt(icon)
        parcel.writeInt(state)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}