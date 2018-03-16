package cn.leo.localnetframe.holder

import android.view.View
import android.widget.ImageView
import cn.leo.localnetframe.adapter.GalleryAdapter
import cn.leo.localnetframe.base.BaseRVHolder
import cn.leo.localnetframe.bean.User

/**
 * Created by Leo on 2018/2/28.
 */
class UserIconListHolder(itemView: View) : BaseRVHolder<User>(itemView) {
    override fun setData(t: User, position: Int) {
        val icon = GalleryAdapter().getIcon(t.icon)
        (itemView as? ImageView)?.setImageResource(icon)
    }
}