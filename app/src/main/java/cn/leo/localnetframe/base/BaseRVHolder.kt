package cn.leo.localnetframe.base

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by Leo on 2017/7/19.
 */

abstract class BaseRVHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    protected var mContext: Context = itemView.context

    abstract fun setData(t: T, position: Int)
}
