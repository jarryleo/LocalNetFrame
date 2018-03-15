package cn.leo.localnetframe.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.ImageView
import cn.leo.localnetframe.R
import cn.leo.localnetframe.holder.GalleryHolder

/**
 * Created by Leo on 2018/3/15.
 */
class GalleryAdapter : RecyclerView.Adapter<GalleryHolder>() {
    private val icons = arrayListOf(
            R.drawable.icon_head_1, R.drawable.icon_head_2, R.drawable.icon_head_3,
            R.drawable.icon_head_4, R.drawable.icon_head_5, R.drawable.icon_head_6,
            R.drawable.icon_head_7, R.drawable.icon_head_8, R.drawable.icon_head_9,
            R.drawable.icon_head_10, R.drawable.icon_head_11, R.drawable.icon_head_12,
            R.drawable.icon_head_13, R.drawable.icon_head_14, R.drawable.icon_head_15,
            R.drawable.icon_head_16, R.drawable.icon_head_17, R.drawable.icon_head_18,
            R.drawable.icon_head_19, R.drawable.icon_head_20, R.drawable.icon_head_21,
            R.drawable.icon_head_22, R.drawable.icon_head_23, R.drawable.icon_head_24
    )

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): GalleryHolder {
        return GalleryHolder(ImageView(parent?.context))
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }

    override fun onBindViewHolder(holder: GalleryHolder?, position: Int) {
        holder?.setImage(icons[position % icons.size])
    }

    fun getIcon(position: Int) = icons[position % icons.size]
}