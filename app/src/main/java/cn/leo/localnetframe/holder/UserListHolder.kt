package cn.leo.localnetframe.holder

import android.view.View
import cn.leo.localnetframe.adapter.GalleryAdapter
import cn.leo.localnetframe.base.BaseRVHolder
import cn.leo.localnetframe.bean.User
import kotlinx.android.synthetic.main.item_user.view.*

/**
 * Created by Leo on 2018/2/28.
 */
class UserListHolder(itemView: View) : BaseRVHolder<User>(itemView) {
    override fun setData(t: User, position: Int) {
        val icon = GalleryAdapter().getIcon(t.icon)
        itemView.ivIcon.setImageResource(icon)
        itemView.tvName.text = t.name
        itemView.tvIp.text = t.ip
    }
}