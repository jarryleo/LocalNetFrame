package cn.leo.localnetframe.view


import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentTransaction
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.*
import cn.leo.localnetframe.R
import kotlinx.android.synthetic.main.dialog_gift.view.*


/**
 * Created by Leo on 2018/3/19.
 */
class GiftDialog : DialogFragment() {
    //1 鲜花，2 拖鞋
    private var type = 1

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity.layoutInflater
        val view = inflater.inflate(R.layout.dialog_gift, null)
        view.ivGift.setImageResource(if (type == 1) {
            animationFlower(view.ivGift)
            R.drawable.rose
        } else {
            animationSlipper(view.ivGift)
            R.drawable.slipper_right
        })
        val dialog = Dialog(activity, R.style.DialogStyleTranslucent)
        // 关闭标题栏，setContentView() 之前调用
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(view)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        //一定要在setContentView之后调用，否则无效
        dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        return dialog
    }

    fun show(transaction: FragmentTransaction?, tag: Int) {
        type = tag
        super.show(transaction, "gift")
    }

    private val animListener = object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {

        }

        override fun onAnimationEnd(animation: Animation?) {
            dismiss()
        }

        override fun onAnimationStart(animation: Animation?) {

        }
    }

    //鲜花动画
    private fun animationFlower(view: View) {
        val animSet = AnimationSet(false)
        animSet.setAnimationListener(animListener)
        animSet.duration = 2000
        val t = TranslateAnimation(
                Animation.RELATIVE_TO_PARENT,
                0.0f,
                Animation.RELATIVE_TO_PARENT,
                0.0f,
                Animation.RELATIVE_TO_PARENT,
                0.6f,
                Animation.RELATIVE_TO_PARENT,
                0.0f)
        t.duration = 1000
        t.interpolator = DecelerateInterpolator()
        animSet.addAnimation(t)
        val s = ScaleAnimation(0.5f, 2.0f,
                0.5f, 2.0f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                1.0f)
        s.duration = 2000
        animSet.addAnimation(s)
        view.startAnimation(animSet)
    }

    //拖鞋动画
    private fun animationSlipper(view: View) {
        val animSet = AnimationSet(false)
        animSet.setAnimationListener(animListener)
        animSet.duration = 2000
        val s = ScaleAnimation(
                2.0f, 1.0f,
                2.0f, 1.0f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f)
        s.duration = 1000
        animSet.addAnimation(s)
        val t = TranslateAnimation(
                Animation.RELATIVE_TO_PARENT,
                0.0f,
                Animation.RELATIVE_TO_PARENT,
                0.0f,
                Animation.RELATIVE_TO_PARENT,
                0.0f,
                Animation.RELATIVE_TO_PARENT,
                1.0f)
        t.duration = 2000
        t.interpolator = AnticipateInterpolator()
        animSet.addAnimation(t)
        view.startAnimation(animSet)
    }
}