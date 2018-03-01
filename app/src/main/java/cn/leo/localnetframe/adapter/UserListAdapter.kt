package cn.leo.localnetframe.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import cn.leo.localnetframe.R
import cn.leo.localnetframe.base.BaseRVAdapter
import cn.leo.localnetframe.base.BaseRVHolder
import cn.leo.localnetframe.bean.User
import cn.leo.localnetframe.holder.UserListHolder

/**
 * Created by Leo on 2018/2/28.
 */
class UserListAdapter : BaseRVAdapter<User>() {
    override fun getViewHolder(parent: ViewGroup?, viewType: Int): BaseRVHolder<User> {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_user, parent, false)
        return UserListHolder(view)
    }
}