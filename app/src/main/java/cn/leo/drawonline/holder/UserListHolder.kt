package cn.leo.drawonline.holder

import android.view.View
import cn.leo.drawonline.R
import cn.leo.drawonline.base.BaseRVHolder
import cn.leo.drawonline.bean.Icons
import cn.leo.drawonline.bean.User
import kotlinx.android.synthetic.main.item_user.view.*

/**
 * Created by Leo on 2018/2/28.
 */
class UserListHolder(itemView: View) : BaseRVHolder<User>(itemView) {
    override fun setData(t: User, position: Int) {
        val icon = Icons.getIconList()[t.icon % Icons.getIconList().size]
        itemView.ivIcon.setImageResource(icon)
        itemView.tvName.text = t.name
        itemView.tvIp.text = mContext.getString(R.string.ip_address, t.ip)
    }
}