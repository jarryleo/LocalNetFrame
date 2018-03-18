package cn.leo.localnetframe.view

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import cn.leo.localnetframe.R
import cn.leo.localnetframe.bean.Msg
import kotlinx.android.synthetic.main.layout_popup_tips.view.*


/**
 * Created by yjtx2 on 2018/3/18.
 */
class PopTips : PopupWindow {
    constructor(context: Context?) : super(context) {
        contentView = LayoutInflater.from(context).inflate(R.layout.layout_popup_tips, null)
        contentView.measure(0, 0)
        setBackgroundDrawable(null)
    }

    fun showAsDropDown(anchor: View?, msg: Msg) {
        contentView.tvMsg.text = msg.msg
        if (msg.isAnswer) {
            contentView.tvMsg.setTextColor(Color.RED)
        } else {
            contentView.tvMsg.setTextColor(contentView
                    .context
                    .resources
                    .getColor(R.color.colorAccent))
        }
        val y = anchor?.height?.plus(contentView.measuredHeight)
        super.showAsDropDown(anchor, 0, -y!!)
    }
}