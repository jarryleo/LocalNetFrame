package cn.leo.localnetframe.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import cn.leo.localnetframe.R
import cn.leo.localnetframe.base.BaseRVAdapter
import cn.leo.localnetframe.base.BaseRVHolder
import cn.leo.localnetframe.bean.User
import cn.leo.localnetframe.holder.UserIconListHolder

/**
 * Created by Leo on 2018/2/28.
 */
class UserIconListAdapter : BaseRVAdapter<User>() {
    override fun getViewHolder(parent: ViewGroup?, viewType: Int): BaseRVHolder<User> {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_user_icon, parent, false)
        return UserIconListHolder(view)
    }
}