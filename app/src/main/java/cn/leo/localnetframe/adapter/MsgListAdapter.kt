package cn.leo.localnetframe.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import cn.leo.localnetframe.R
import cn.leo.localnetframe.base.BaseRVAdapter
import cn.leo.localnetframe.base.BaseRVHolder
import cn.leo.localnetframe.bean.Msg
import cn.leo.localnetframe.holder.MsgListHolder

/**
 * Created by Leo on 2018/2/28.
 */
class MsgListAdapter : BaseRVAdapter<Msg>() {
    override fun getViewHolder(parent: ViewGroup?, viewType: Int): BaseRVHolder<Msg> {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_msg, parent, false)
        return MsgListHolder(view)
    }
}