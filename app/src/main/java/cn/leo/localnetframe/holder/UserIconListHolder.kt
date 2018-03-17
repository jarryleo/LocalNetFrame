package cn.leo.localnetframe.holder

import android.view.View
import android.widget.ImageView
import cn.leo.localnetframe.base.BaseRVHolder
import cn.leo.localnetframe.bean.Icons
import cn.leo.localnetframe.bean.User

/**
 * Created by Leo on 2018/2/28.
 */
class UserIconListHolder(itemView: View) : BaseRVHolder<User>(itemView) {
    override fun setData(t: User, position: Int) {
        val icon = Icons.getIconList()[t.icon % Icons.getIconList().size]
        (itemView as? ImageView)?.setImageResource(icon)
    }
}