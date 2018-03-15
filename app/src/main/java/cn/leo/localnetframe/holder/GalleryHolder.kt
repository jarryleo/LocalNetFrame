package cn.leo.localnetframe.holder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView

/**
 * Created by Leo on 2018/3/15.
 */
class GalleryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun setImage(i: Int) {
        (itemView as? ImageView)?.setImageResource(i)
    }
}