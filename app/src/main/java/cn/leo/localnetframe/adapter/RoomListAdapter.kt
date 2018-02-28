package cn.leo.localnetframe.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import cn.leo.localnetframe.R
import cn.leo.localnetframe.base.BaseRVAdapter
import cn.leo.localnetframe.base.BaseRVHolder
import cn.leo.localnetframe.bean.Room
import cn.leo.localnetframe.holder.RoomListHolder

/**
 * Created by Leo on 2018/2/28.
 */
class RoomListAdapter : BaseRVAdapter<Room>() {
    override fun getViewHolder(parent: ViewGroup?, viewType: Int): BaseRVHolder<Room> {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_room, parent, false)
        return RoomListHolder(view)
    }
}