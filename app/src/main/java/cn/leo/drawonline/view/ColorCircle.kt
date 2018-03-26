package cn.leo.drawonline.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import cn.leo.drawonline.R

/**
 * Created by yjtx2 on 2018/3/3.
 */
class ColorCircle : View {
    //画笔
    private var mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    //像素密度
    private var density = resources.displayMetrics.density
    //画笔粗细
    private var mStrokeWidth: Float = 16.0f * density
    //画笔颜色
    private var mColor: Int = Color.BLACK
    //颜色选择监听
    private var mColorClickListener: OnColorClickListener? = null

    //构造
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0)

    constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int) :
            super(context, attr, defStyleAttr) {
        setOnClickListener {
            if (mColorClickListener != null) {
                mColorClickListener?.onColorClick(mColor)
            }
        }
        val typedArray = context.obtainStyledAttributes(attr, R.styleable.ColorCircleStyle)
        val color = typedArray.getColor(R.styleable.ColorCircleStyle_color, Color.BLACK)
        val size = typedArray.getDimension(R.styleable.ColorCircleStyle_size, 16.0f)
        setColor(color)
        setStrokeWidth(size)
        typedArray.recycle()
    }

    fun setColor(color: Int) {
        mColor = color
        mPaint.color = color
    }

    fun setStrokeWidth(width: Float) {
        mStrokeWidth = width * density
    }


    override fun onDraw(canvas: Canvas?) {
        canvas?.drawCircle(width / 2f, height / 2f, mStrokeWidth, mPaint)
    }

    fun setOnColorClickListener(listener: OnColorClickListener) {
        mColorClickListener = listener
    }

    interface OnColorClickListener {
        fun onColorClick(color: Int)
    }
}

