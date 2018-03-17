package cn.leo.localnetframe.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import cn.leo.localnetframe.R
import cn.leo.localnetframe.base.BaseRVAdapter
import cn.leo.localnetframe.base.BaseRVHolder
import cn.leo.localnetframe.bean.User
import cn.leo.localnetframe.holder.ScoreListHolder
import cn.leo.localnetframe.net.NetImpl

/**
 * Created by Leo on 2018/2/28.
 */
class ScoreListAdapter(private val netImpl: NetImpl) : BaseRVAdapter<User>() {
    override fun getViewHolder(parent: ViewGroup?, viewType: Int): BaseRVHolder<User> {
        val view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.item_score, parent, false)
        return ScoreListHolder(view,netImpl)
    }
}