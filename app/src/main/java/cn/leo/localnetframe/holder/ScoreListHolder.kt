package cn.leo.localnetframe.holder

import android.view.View
import cn.leo.localnetframe.R
import cn.leo.localnetframe.base.BaseRVHolder
import cn.leo.localnetframe.bean.Icons
import cn.leo.localnetframe.bean.User
import cn.leo.localnetframe.net.NetImpl
import cn.leo.localnetframe.utils.get
import kotlinx.android.synthetic.main.item_score.view.*

/**
 * Created by Leo on 2018/2/28.
 */
class ScoreListHolder(itemView: View, private val netImpl: NetImpl) : BaseRVHolder<User>(itemView) {
    override fun setData(t: User, position: Int) {
        val icon = Icons.getIconList()[t.icon % Icons.getIconList().size]
        itemView.ivIcon.setImageResource(icon)
        itemView.tvName.text = t.name
        itemView.tvScore.text = t.score.toString()
        val nickname = netImpl.getMeName()
        if (nickname == t.name) {
            itemView.tvName.setTextColor(mContext.resources.getColor(R.color.colorOrange))
        } else {
            itemView.tvName.setTextColor(mContext.resources.getColor(R.color.colorAccent))
        }
        val painter = netImpl.getPainter()!!.name
        if (painter == t.name) {
            itemView.setBackgroundResource(R.color.colorPrimary)
        } else {
            itemView.setBackgroundResource(R.color.colorPrimaryDark)
        }
    }
}