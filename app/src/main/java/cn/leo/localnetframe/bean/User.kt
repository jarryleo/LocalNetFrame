package cn.leo.localnetframe.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson

/**
 * Created by Leo on 2018/2/28.
 */
data class User(var ip: String,
                var name: String,
                var heart: Int = 0,
                var score: Int = 0) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt())

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
        parcel.writeInt(heart)
        parcel.writeInt(score)
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