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
    @State
    private var state = STATE_ONLINE

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readLong(),
            parcel.readInt()) {
        icon = parcel.readInt()
        state = parcel.readLong()
    }

    //是否掉线
    fun isOffline(): Boolean {
        val offline = System.currentTimeMillis() - heart > 5L * 1000
        state = if (offline) {
            STATE_OFFLINE
        } else {
            STATE_ONLINE
        }
        return offline
    }

    @State
    fun getStatus() = state


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
        parcel.writeLong(state)
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