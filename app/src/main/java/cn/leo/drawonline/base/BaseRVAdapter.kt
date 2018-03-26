package cn.leo.drawonline.base

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import java.util.*

/**
 * Created by Leo on 2018/2/3.
 */

abstract class BaseRVAdapter<T> : RecyclerView.Adapter<BaseRVHolder<T>>() {
    var mList = ArrayList<T>()

    fun setDatas(list: List<T>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    fun addDatas(list: List<T>) {
        mList.addAll(list)
        notifyDataSetChanged()
    }

    fun addData(t: T) {
        mList.add(t)
        notifyDataSetChanged()
    }

    fun removeData(t: T) {
        mList.remove(t)
        notifyDataSetChanged()
    }

    fun clearData() {
        mList.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BaseRVHolder<T> {
        return getViewHolder(parent, viewType)
    }

    //由子类选择ViewHolder
    protected abstract fun getViewHolder(parent: ViewGroup?, viewType: Int): BaseRVHolder<T>

    override fun onBindViewHolder(holder: BaseRVHolder<T>?, position: Int) {
        val commRVHolder = holder as BaseRVHolder<T>
        commRVHolder.setData(mList[position], position)
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}
