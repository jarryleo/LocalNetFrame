package cn.leo.localnetframe.view


import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentTransaction
import android.view.Window
import cn.leo.localnetframe.R
import kotlinx.android.synthetic.main.dialog_answer.view.*


/**
 * Created by Leo on 2018/3/19.
 */
class AnswerDialog : DialogFragment() {
    private var opinionClickListener: OnOpinionClickListener? = null
    private var answer: String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //为了样式统一和兼容性，可以使用 V7 包下的 AlertDialog.Builder
        //val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val view = inflater.inflate(R.layout.dialog_answer, null)
        val dialog = Dialog(activity, R.style.DialogStyle)
        // 关闭标题栏，setContentView() 之前调用
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(view)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        view?.tvAnswer?.text = answer
        view?.ivFlower?.setOnClickListener {
            dialog.dismiss()
            if (opinionClickListener != null) {
                opinionClickListener?.onFLower()
            }
        }
        view?.ivSlipper?.setOnClickListener {
            dialog.dismiss()
            if (opinionClickListener != null) {
                opinionClickListener?.onSlipper()
            }
        }
        return dialog
    }

    override fun show(transaction: FragmentTransaction?, tag: String?): Int {
        answer = tag
        return super.show(transaction, "answer")
    }

    override fun dismiss() {
        if (dialog != null && dialog.isShowing) {
            dialog.dismiss()
        }
    }

    fun setOnOpinionClickListenrt(listener: OnOpinionClickListener) {
        opinionClickListener = listener
    }

    interface OnOpinionClickListener {
        fun onFLower()
        fun onSlipper()
    }

}