package cn.leo.drawonline.holder

import android.support.v4.content.ContextCompat
import android.view.View
import cn.leo.drawonline.R
import cn.leo.drawonline.base.BaseRVHolder
import cn.leo.drawonline.bean.Icons
import cn.leo.drawonline.bean.UserBean
import cn.leo.drawonline.utils.get
import kotlinx.android.synthetic.main.item_score.view.*


/**
 * Created by Leo on 2018/2/28.
 */
class ScoreListHolder(itemView: View) : BaseRVHolder<UserBean>(itemView) {
    override fun setData(t: UserBean, position: Int) {
        val icon = Icons.getIconList()[t.icon % Icons.getIconList().size]
        val drawable = ContextCompat.getDrawable(mContext, icon)
        val meName = mContext.get("nickname", "")
        val isMe = t.userName == meName
        /*if (t.isOffline() && !isMe) {
            //玩家掉线，头像变暗
            drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY)
        } else {
            drawable.clearColorFilter()
        }*/
        itemView.ivIcon.setImageDrawable(drawable)
        itemView.tvName.text = t.userName
        itemView.tvScore.text = t.score.toString()
        if (isMe) {
            itemView.tvName.setTextColor(mContext.resources.getColor(R.color.colorOrange))
        } else {
            itemView.tvName.setTextColor(mContext.resources.getColor(R.color.colorAccent))
        }
        itemView.ivBrush.visibility = View.GONE
        /*val painter = netImpl.getPainter()!!.ip
        if (painter == t.ip) {
            itemView.ivBrush.visibility = View.VISIBLE
        } else {
            itemView.ivBrush.visibility = View.GONE
        }*/
    }
}