package cn.leo.localnetframe.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.ImageView
import cn.leo.localnetframe.R
import cn.leo.localnetframe.bean.Icons
import cn.leo.localnetframe.holder.GalleryHolder

/**
 * Created by Leo on 2018/3/15.
 */
class GalleryAdapter : RecyclerView.Adapter<GalleryHolder>() {
    private val icons = Icons.getIconList()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): GalleryHolder {
        return GalleryHolder(ImageView(parent?.context))
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }

    override fun onBindViewHolder(holder: GalleryHolder?, position: Int) {
        holder?.setImage(icons[position % icons.size])
    }

}