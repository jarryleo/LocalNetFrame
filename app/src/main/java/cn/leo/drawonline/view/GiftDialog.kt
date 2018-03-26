package cn.leo.drawonline.view


import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentTransaction
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.*
import cn.leo.drawonline.R
import cn.leo.drawonline.utils.SoundsUtil
import kotlinx.android.synthetic.main.dialog_gift.view.*


/**
 * Created by Leo on 2018/3/19.
 */
class GiftDialog : DialogFragment() {
    //1 鲜花，2 拖鞋
    private var type = 1
    //声音池
    private var soundsUtil: SoundsUtil? = null

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

    fun show(transaction: FragmentTransaction?, tag: Int, sound: SoundsUtil) {
        type = tag
        soundsUtil = sound
        super.show(transaction, "gift")
    }

    private val animListener = object : AnimListener() {
        override fun onAnimationEnd(animation: Animation?) {
            dismiss()
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
        s.duration = 1500
        s.startOffset = 500
        animSet.addAnimation(s)
        view.startAnimation(animSet)
    }

    //拖鞋动画
    private fun animationSlipper(view: View) {
        val animSet = AnimationSet(false)
        animSet.setAnimationListener(animListener)
        animSet.duration = 500
        val s = ScaleAnimation(
                3.0f, 1.0f,
                3.0f, 1.0f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f)
        s.duration = 800
        animSet.addAnimation(s)
        val r = RotateAnimation(0f, 360f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f)
        r.duration = 800
        animSet.addAnimation(r)
        val t = TranslateAnimation(
                Animation.RELATIVE_TO_PARENT,
                0.0f,
                Animation.RELATIVE_TO_PARENT,
                0.0f,
                Animation.RELATIVE_TO_PARENT,
                0.0f,
                Animation.RELATIVE_TO_PARENT,
                1.0f)
        t.duration = 1200
        t.interpolator = AccelerateInterpolator()
        setEndAnimListener(animSet, {
            soundsUtil?.playSound(R.raw.pa, false)
            view.startAnimation(t)
        })
        t.setAnimationListener(animListener)
        view.startAnimation(animSet)
    }


    private fun setEndAnimListener(animation: Animation, listener: () -> Unit) {
        animation.setAnimationListener(object : AnimListener() {
            override fun onAnimationEnd(animation: Animation?) {
                listener()
            }
        })
    }


    open inner class AnimListener : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {
        }

        override fun onAnimationEnd(animation: Animation?) {
        }

        override fun onAnimationStart(animation: Animation?) {
        }

    }
}