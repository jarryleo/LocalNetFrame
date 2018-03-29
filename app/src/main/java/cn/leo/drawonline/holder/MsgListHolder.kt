package cn.leo.drawonline.holder

import android.graphics.Color
import android.view.View
import cn.leo.drawonline.R
import cn.leo.drawonline.base.BaseRVHolder
import cn.leo.drawonline.bean.MsgBean
import kotlinx.android.synthetic.main.item_msg.view.*

/**
 * Created by Leo on 2018/2/28.
 */
class MsgListHolder(itemView: View) : BaseRVHolder<MsgBean>(itemView) {
    override fun setData(t: MsgBean, position: Int) {
        itemView.tvName.text = "${t.senderName}:"
        itemView.tvMsg.text = t.msg
        if (t.code == 100) {
            itemView.tvMsg.setTextColor(Color.RED)
        } else {
            itemView.tvMsg.setTextColor(mContext.resources.getColor(R.color.colorAccent))
        }
    }
}