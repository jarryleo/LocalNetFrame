package cn.leo.localnetframe.holder

import android.view.View
import cn.leo.localnetframe.R
import cn.leo.localnetframe.base.BaseRVHolder
import cn.leo.localnetframe.bean.User
import kotlinx.android.synthetic.main.item_user.view.*

/**
 * Created by Leo on 2018/2/28.
 */
class UserListHolder(itemView: View) : BaseRVHolder<User>(itemView) {
    override fun setData(t: User, position: Int) {
        itemView.tvName.text = mContext.resources.getString(R.string.user_name, t.name)
        itemView.tvIp.text = mContext.resources.getString(R.string.id_address, t.ip)
    }
}