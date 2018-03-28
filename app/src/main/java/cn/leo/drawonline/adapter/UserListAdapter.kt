package cn.leo.drawonline.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import cn.leo.drawonline.R
import cn.leo.drawonline.base.BaseRVAdapter
import cn.leo.drawonline.base.BaseRVHolder
import cn.leo.drawonline.bean.UserBean
import cn.leo.drawonline.holder.UserListHolder

/**
 * Created by Leo on 2018/2/28.
 */
class UserListAdapter : BaseRVAdapter<UserBean>() {
    override fun getViewHolder(parent: ViewGroup?, viewType: Int): BaseRVHolder<UserBean> {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_user, parent, false)
        return UserListHolder(view)
    }
}