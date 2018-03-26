package cn.leo.drawonline.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import cn.leo.drawonline.R

/**
 * Created by Leo on 2017/11/1.
 */
class DrawBoard : View {

    //画笔
    private var mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    //画笔颜色
    private var mColor: Int = Color.BLACK
    //画笔粗细
    private var mStrokeWidth: Float = 3.0f
    //画布
    private var mCanvas = Canvas()
    //图像
    private var mBitmap: Bitmap? = null
    //像素密度
    private var density = resources.displayMetrics.density
    //缩放等级(匹配不同机型的画板大小)
    private var scale = 1.0f
    //图像文字编码
    private var bitmapCode: StringBuilder = StringBuilder()
    //绘画回调
    var onDrawListener: OnDrawListener? = null
    //锁住画板
    var lock = false
    private var startX: Float = 0f
    private var startY: Float = 0f
    private var dis: Long = 0
    private var mWidth: Int = 0


    //构造
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0)

    constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int) :
            super(context, attr, defStyleAttr) {
        init()
    }

    private fun init() {
        mColor = Color.BLACK
        mStrokeWidth = 3.0f * density
        mPaint.color = mColor
        mPaint.strokeWidth = mStrokeWidth * scale
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeCap = Paint.Cap.ROUND
        //setBackgroundColor(Color.WHITE)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mWidth = MeasureSpec.getSize(widthMeasureSpec)
        val spec = MeasureSpec.makeMeasureSpec(mWidth, MeasureSpec.EXACTLY)
        if (bitmapCode.isEmpty()) {
            bitmapCode.append("I$mWidth|")
        }
        super.onMeasure(widthMeasureSpec, spec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            mBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.RGB_565)
            mCanvas = Canvas(mBitmap)
            drawBackGround()
        }
    }

    /**
     * 设置画笔颜色
     */
    fun setColor(color: Int) {
        mColor = color
        mPaint.color = mColor
        //动作编码
        bitmapCode.append("C")
                .append(color)
                .append("|")
    }

    /**
     * 设置笔迹粗细
     */
    fun setStrokeWidth(width: Float) {
        mStrokeWidth = width * density
        mPaint.strokeWidth = mStrokeWidth
        //动作编码
        bitmapCode.append("B")
                .append(width)
                .append("|")
    }

    /**
     * 触摸动作处理
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (lock) return true
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> down(event.x, event.y)
            MotionEvent.ACTION_MOVE -> move(event.x, event.y)
            MotionEvent.ACTION_UP -> getDraw()
        }
        return true
    }

    /**
     * 下笔
     */
    private fun down(x: Float, y: Float) {
        startX = x
        startY = y
        draw(x + mStrokeWidth, y)
        //动作编码
        bitmapCode.append("D")
                .append(x.toInt())
                .append(",")
                .append(y.toInt())
                .append("|")
    }

    /**
     * 笔迹移动
     */
    private fun move(x: Float, y: Float) {
        draw(x, y)
        startX = x
        startY = y
        //动作编码
        bitmapCode.append("M")
                .append(x.toInt())
                .append(",")
                .append(y.toInt())
                .append("|")
    }

    /**
     * 设置画板背景
     */
    private fun drawBackGround() {
        mCanvas.drawColor(resources.getColor(R.color.colorPaintBg))
    }

    /**
     * 绘制图案
     */
    private fun draw(x: Float, y: Float) {
        mCanvas.drawLine(startX, startY, x, y, mPaint)
        show()
    }

    /**
     * 显示图案
     */
    private fun show() {
        if (mBitmap == null) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            background = BitmapDrawable(null, mBitmap)
        } else {
            setBackgroundDrawable(BitmapDrawable(null, mBitmap))
        }
        if (System.currentTimeMillis() - dis > 1000) {
            getDraw()
        }
    }

    /**
     *取出编码并传输
     */
    private fun getDraw() {
        dis = System.currentTimeMillis()
        Log.e("codeSize", "=${bitmapCode.length}")
        if (onDrawListener != null && !lock) {
            (onDrawListener as OnDrawListener).onDraw(bitmapCode.toString())
        }
    }

    /**
     *获取图像编码
     */
    fun getDrawCode() = bitmapCode.toString()

    /**
     * 撤销一笔
     */
    fun undo() {
        val last = bitmapCode.lastIndexOf("D")
        if (last < 0) return
        bitmapCode.delete(last, bitmapCode.length)
        setBitmapCode(bitmapCode.toString())
    }

    /**
     * 清空画板
     */
    fun clear() {
        bitmapCode.delete(0, bitmapCode.length)
        //bitmapCode.append("I$density|")
        bitmapCode.append("I$mWidth|")
        setBitmapCode(bitmapCode.toString())
    }

    /**
     * 接受数据还原画板
     */
    fun setBitmapCode(code: String) {
        bitmapCode.delete(0, bitmapCode.length).append(code)
        decode(code)
    }

    /**
     * 解码：
     * I  清空
     * D  下笔
     * M  移动
     * C  颜色
     * B  粗细
     * 每个动作用竖线 | 分割  坐标用逗号 , 分割
     */
    private fun decode(code: String) {
        //画笔还原
        init()
        //清空画板
        drawBackGround()
        //分解动作
        code.split("|")
                .takeWhile { !it.isEmpty() }
                .forEach {
                    try {
                        when (it.first()) {
                            'I' -> {
                                val d = it.substring(1).toFloat()
                                scale = mWidth / d
                                init()
                            }
                            'D' -> {
                                val point = it.substring(1)
                                val split = point.split(",")
                                startX = split[0].toFloat()
                                startY = split[1].toFloat()
                                mCanvas.drawLine(startX * scale, startY * scale,
                                        (startX + mStrokeWidth) * scale, startY * scale, mPaint)
                            }
                            'M' -> {
                                val point = it.substring(1)
                                val split = point.split(",")
                                val a = split[0].toFloat()
                                val b = split[1].toFloat()
                                mCanvas.drawLine(startX * scale, startY * scale, a * scale, b * scale, mPaint)
                                startX = a
                                startY = b
                            }
                            'C' -> {
                                val color = it.substring(1)
                                mColor = color.toInt()
                                mPaint.color = mColor
                            }
                            'B' -> {
                                val b = it.substring(1)
                                mStrokeWidth = b.toFloat() * scale * density
                                mPaint.strokeWidth = mStrokeWidth
                            }
                        }
                    } catch (e: Exception) {
                        //不处理
                    }
                }
        dis = 0
        show() //展示还原的图案
    }

    interface OnDrawListener {
        fun onDraw(code: String)
    }
}

