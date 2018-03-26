package cn.leo.drawonline.holder

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import cn.leo.drawonline.R
import cn.leo.drawonline.activity.RoomActivity
import cn.leo.drawonline.adapter.UserIconListAdapter
import cn.leo.drawonline.base.BaseRVHolder
import cn.leo.drawonline.bean.Room
import kotlinx.android.synthetic.main.item_room.view.*

/**
 * Created by Leo on 2018/2/28.
 */
class RoomListHolder(itemView: View) : BaseRVHolder<Room>(itemView) {
    override fun setData(t: Room, position: Int) {
        itemView.tvRoomId.text = mContext.resources.getString(R.string.room_id, t.id)
        itemView.tvUserCount.text = mContext.resources.getString(R.string.user_count, t.getUserCount().toString())
        itemView.rvUserList.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        itemView.rvUserList.adapter = UserIconListAdapter().apply { setDatas(t.users) }
        itemView.tvEnter.setOnClickListener {
            val intent = Intent(mContext, RoomActivity::class.java)
            intent.putExtra("room", t)
            mContext.startActivity(intent)
        }
    }
}