package cn.leo.localnetframe.utils

import android.graphics.PointF
import android.util.Log
import kotlin.math.abs
import kotlin.math.sqrt


/**
 * @author : ling luo
 * @date : 2019-08-09
 * 道格拉斯-普克(Douglas-Peuker)DP算法
 * 1、连接曲线首尾两点A、B；
 * 2、依次计算曲线上所有点到A、B两点所在曲线的距离；
 * 3、计算最大距离D，如果D小于阈值threshold,则去掉曲线上出A、B外的所有点；
 * 如果D大于阈值threshold,则把曲线以最大距离分割成两段；
 * 4、对所有曲线分段重复1-3步骤，知道所有D均小于阈值。即完成抽稀。
 */
object DouglasPeuker {

    /**
     * 曲线抽稀
     * @param threshold 阈值
     */
    fun getPoints(points: MutableList<PointF>, threshold: Float): List<PointF> {
        if (threshold <= 0) return points
        //需要排除的点
        val exclude = mutableSetOf<PointF>()
        val start = 0
        val end = points.size - 1
        if (end - start < 2) return points
        exclude(points, exclude, start, end, threshold)
        //排除点
        points.removeAll(exclude)
        return points
    }

    /**
     * 计算需要剔除的点
     */
    fun exclude(points: MutableList<PointF>,
                exclude: MutableSet<PointF>,
                start: Int, end: Int, threshold: Float) {
        if (end - start < 2) return
        var max = 0f
        var maxIndex = -1
        val s = points[start]
        val e = points[end]
        for (i in start + 1 until end) {
            val mid = points[i]
            val dist = point2LineDistance(s, e, mid)
            if (dist > max) {
                max = dist
                maxIndex = i
            }
            if (dist < threshold) {
                exclude.add(mid)
            }
        }
        if (maxIndex > -1 && max > threshold) {
            exclude(points, exclude, start, maxIndex, threshold)
            exclude(points, exclude, maxIndex, end, threshold)
        }
    }


    /**
     * 求点p到直线ab的距离
     */
    fun point2LineDistance(A: PointF,
                           B: PointF,
                           P: PointF): Float {
        return DouglasPeuker.point2LineDistance(
                A.x.toDouble(),
                A.y.toDouble(),
                B.x.toDouble(),
                B.y.toDouble(),
                P.x.toDouble(),
                P.y.toDouble())
                .toFloat()
    }

    fun point2LineDistance(x1: Double, y1: Double,
                           x2: Double, y2: Double,
                           x: Double, y: Double): Double {
        val a = y2 - y1
        val b = x1 - x2
        val c = x2 * y1 - x1 * y2
        return abs(a * x + b * y + c) / sqrt(a * a + b * b)
    }


}