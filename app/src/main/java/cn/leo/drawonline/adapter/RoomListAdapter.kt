package cn.leo.drawonline.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import cn.leo.drawonline.R
import cn.leo.drawonline.base.BaseRVAdapter
import cn.leo.drawonline.base.BaseRVHolder
import cn.leo.drawonline.bean.Room
import cn.leo.drawonline.holder.RoomListHolder

/**
 * Created by Leo on 2018/2/28.
 */
class RoomListAdapter : BaseRVAdapter<Room>() {
    override fun getViewHolder(parent: ViewGroup?, viewType: Int): BaseRVHolder<Room> {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_room, parent, false)
        return RoomListHolder(view)
    }
}