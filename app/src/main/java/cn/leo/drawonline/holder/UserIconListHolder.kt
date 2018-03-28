package cn.leo.drawonline.holder

import android.view.View
import android.widget.ImageView
import cn.leo.drawonline.base.BaseRVHolder
import cn.leo.drawonline.bean.Icons
import cn.leo.drawonline.bean.UserBean

/**
 * Created by Leo on 2018/2/28.
 */
class UserIconListHolder(itemView: View) : BaseRVHolder<UserBean>(itemView) {
    override fun setData(t: UserBean, position: Int) {
        val icon = Icons.getIconList()[t.icon % Icons.getIconList().size]
        (itemView as? ImageView)?.setImageResource(icon)
    }
}