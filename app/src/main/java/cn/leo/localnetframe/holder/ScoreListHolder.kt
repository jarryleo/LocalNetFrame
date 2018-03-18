package cn.leo.localnetframe.holder

import android.graphics.Color
import android.view.View
import cn.leo.localnetframe.R
import cn.leo.localnetframe.base.BaseRVHolder
import cn.leo.localnetframe.bean.Icons
import cn.leo.localnetframe.bean.User
import cn.leo.localnetframe.net.NetImpl
import cn.leo.localnetframe.utils.get
import kotlinx.android.synthetic.main.item_score.view.*
import android.support.v4.content.ContextCompat
import android.graphics.drawable.Drawable
import android.support.v4.graphics.drawable.DrawableCompat
import android.graphics.LightingColorFilter
import android.graphics.ColorFilter
import android.graphics.PorterDuff


/**
 * Created by Leo on 2018/2/28.
 */
class ScoreListHolder(itemView: View, private val netImpl: NetImpl) : BaseRVHolder<User>(itemView) {
    override fun setData(t: User, position: Int) {
        val icon = Icons.getIconList()[t.icon % Icons.getIconList().size]
        val drawable = ContextCompat.getDrawable(mContext, icon)
        val isMe = t.ip == netImpl.getMeIp()
        if (t.isOffline() && !isMe) {
            //玩家掉线，头像变暗
            drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY)
        } else {
            drawable.clearColorFilter()
        }
        itemView.ivIcon.setImageDrawable(drawable)
        itemView.tvName.text = t.name
        itemView.tvScore.text = t.score.toString()
        if (isMe) {
            itemView.tvName.setTextColor(mContext.resources.getColor(R.color.colorOrange))
        } else {
            itemView.tvName.setTextColor(mContext.resources.getColor(R.color.colorAccent))
        }
        val painter = netImpl.getPainter()!!.ip
        if (painter == t.ip) {
            itemView.setBackgroundResource(R.color.colorPrimary)
        } else {
            itemView.setBackgroundResource(R.color.colorPrimaryDark)
        }
    }
}