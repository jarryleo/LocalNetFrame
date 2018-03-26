package cn.leo.drawonline.holder

import android.view.View
import android.widget.ImageView
import cn.leo.drawonline.base.BaseRVHolder
import cn.leo.drawonline.bean.Icons
import cn.leo.drawonline.bean.User

/**
 * Created by Leo on 2018/2/28.
 */
class UserIconListHolder(itemView: View) : BaseRVHolder<User>(itemView) {
    override fun setData(t: User, position: Int) {
        val icon = Icons.getIconList()[t.icon % Icons.getIconList().size]
        (itemView as? ImageView)?.setImageResource(icon)
    }
}