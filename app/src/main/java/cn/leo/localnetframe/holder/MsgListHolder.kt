package cn.leo.localnetframe.holder

import android.graphics.Color
import android.view.View
import cn.leo.localnetframe.R
import cn.leo.localnetframe.base.BaseRVHolder
import cn.leo.localnetframe.bean.Msg
import kotlinx.android.synthetic.main.item_msg.view.*

/**
 * Created by Leo on 2018/2/28.
 */
class MsgListHolder(itemView: View) : BaseRVHolder<Msg>(itemView) {
    override fun setData(t: Msg, position: Int) {
        itemView.tvName.text = t.name
        itemView.tvMsg.text = t.msg
        if (t.isAnswer) {
            itemView.tvMsg.setTextColor(Color.RED)
        } else {
            itemView.tvMsg.setTextColor(mContext.resources.getColor(R.color.colorAccent))
        }
    }
}