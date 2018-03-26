package cn.leo.drawonline.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import cn.leo.drawonline.R
import cn.leo.drawonline.base.BaseRVAdapter
import cn.leo.drawonline.base.BaseRVHolder
import cn.leo.drawonline.bean.User
import cn.leo.drawonline.holder.ScoreListHolder
import cn.leo.drawonline.net.NetImpl

/**
 * Created by Leo on 2018/2/28.
 */
class ScoreListAdapter(private val netImpl: NetImpl) : BaseRVAdapter<User>() {
    override fun getViewHolder(parent: ViewGroup?, viewType: Int): BaseRVHolder<User> {
        val view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.item_score, parent, false)
        return ScoreListHolder(view, netImpl)
    }

    fun findPositionForName(name: String): Int {
        mList.forEachIndexed { index, user -> if (user.name == name) return index }
        return -1
    }
}