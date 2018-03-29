package cn.leo.drawonline.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import cn.leo.drawonline.R
import cn.leo.drawonline.base.BaseRVAdapter
import cn.leo.drawonline.base.BaseRVHolder
import cn.leo.drawonline.bean.MsgBean
import cn.leo.drawonline.holder.MsgListHolder

/**
 * Created by Leo on 2018/2/28.
 */
class MsgListAdapter : BaseRVAdapter<MsgBean>() {
    override fun getViewHolder(parent: ViewGroup?, viewType: Int): BaseRVHolder<MsgBean> {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_msg, parent, false)
        return MsgListHolder(view)
    }
}