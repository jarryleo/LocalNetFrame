package cn.leo.localnetframe.holder

import android.view.View
import cn.leo.localnetframe.base.BaseRVHolder
import cn.leo.localnetframe.bean.Icons
import cn.leo.localnetframe.bean.User
import kotlinx.android.synthetic.main.item_score.view.*

/**
 * Created by Leo on 2018/2/28.
 */
class ScoreListHolder(itemView: View) : BaseRVHolder<User>(itemView) {
    override fun setData(user: User, position: Int) {
        val icon = Icons.getIconList()[user.icon % Icons.getIconList().size]
        itemView.ivIcon.setImageResource(icon)
        itemView.tvName.text = user.name
        itemView.tvScore.text = user.score.toString()
    }
}