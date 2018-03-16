package cn.leo.localnetframe.net

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import cn.leo.localnet.net.UdpFrame

/**
 * Created by Leo on 2018/3/12.
 * 上层网络协议
 */
abstract class NetInterFace : UdpFrame.OnDataArrivedListener {
    private var onDataArrivedListener: OnDataArrivedListener? = null
    private var udpFrame: UdpFrame = UdpFrame(this)

    init {
        udpFrame.start()
    }

    /**
     * 停止网络服务
     */
    fun stopNet() = this.udpFrame.stopNet()

    /**
     * 数据接收回调接口
     */
    abstract class OnDataArrivedListener {
        open fun onShowAnswer(pre: Char, msg: String, host: String) {}
        open fun onRoomResult(pre: Char, msg: String, host: String) {}
        open fun onJoinRoom(pre: Char, msg: String, host: String) {}
        open fun onExitRoom(pre: Char, msg: String, host: String) {}
        open fun onStartGame(pre: Char, msg: String, host: String) {}
        open fun onUpdateScore(pre: Char, msg: String, host: String) {}
        open fun onShowTips(pre: Char, msg: String, host: String) {}
        open fun onCountDown(pre: Char, msg: String, host: String) {}
        open fun onNextPainter(pre: Char, msg: String, host: String) {}
        open fun onChat(pre: Char, msg: String, host: String) {}
        open fun onPaint(pre: Char, msg: String, host: String) {}
    }


    /**
     * 设置数据回调接口
     */
    fun setDataArrivedListener(listener: OnDataArrivedListener) {
        onDataArrivedListener = listener
    }

    /**
     * 发送字节数据
     */
    fun sendData(data: ByteArray, host: String) {
        udpFrame.send(data, host)
    }

    /**
     * 发送字符串数据
     */
    fun sendData(str: String, host: String) {
        sendData(str.toByteArray(), host)
    }

    /**
     * 底层网络数据返回
     */
    override fun onDataArrived(data: ByteArray, length: Int, host: String) {
        Log.d("host", host)
        val message = Message.obtain()
        val bundle = Bundle()
        bundle.putString("host", host)
        bundle.putInt("length", length)
        bundle.putByteArray("data", data)
        message.data = bundle
        handler.sendMessage(message)
    }

    /**
     * 返回的数据在子线程，这里切换到主线程
     */
    private val handler = Handler(Looper.getMainLooper()) {
        val data = it.data
        val host = data.getString("host")
        val length = data.getInt("length")
        val byteArray = data.getByteArray("data")
        decode(byteArray, length, host)
        true
    }

    /**
     * 解码信息
     * A 显示答案
     * F 查找房间
     * R 返回房间信息
     * J 申请加入房间
     * S 开始游戏
     * U 同步积分
     * T 提示内容
     * D 倒计时
     * E 退出房间
     * N 下一个开始游戏
     * C 聊天
     * P 画画
     * H 心跳
     */
    private fun decode(data: ByteArray, length: Int, host: String) {
        val str = String(data, 0, length)
        val first = str.first()
        val body = str.substring(1)
        Log.d("receive = ", first.toString())
        when (first) {
            'A' -> {
                onShowAnswer(first, body, host)
            }
            'F' -> {
                onFindRoom(first, body, host)
            }
            'H' -> {
                onHeart(first, body, host)
            }
            'R' -> {
                onRoomResult(first, body, host)
            }
            'J' -> {
                onJoinRoom(first, body, host)
            }
            'S' -> {
                onStartGame(first, body, host)
            }
            'T' -> {
                onShowTips(first, body, host)
            }
            'D' -> {
                onCountDown(first, body, host)
            }
            'E' -> {
                onExitRoom(first, body, host)
            }
            'C' -> {
                onChat(first, body, host)
            }
            'P' -> {
                onPaint(first, body, host)
            }
            'U' -> {
                onUpdateScore(first, body, host)
            }
            'N' -> {
                onNextPainter(first, body, host)
            }
            else -> {
            }
        }
        dispatchData(first, body, host)
    }

    /**
     * 通过接口返回数据
     */
    fun dispatchData(pre: Char, data: String, host: String) {
        if (onDataArrivedListener != null) {
            when (pre) {
                'A' -> {
                    onDataArrivedListener?.onShowAnswer(pre, data, host)
                }
                'R' -> {
                    onDataArrivedListener?.onRoomResult(pre, data, host)
                }
                'J' -> {
                    onDataArrivedListener?.onJoinRoom(pre, data, host)
                }
                'E' -> {
                    onDataArrivedListener?.onExitRoom(pre, data, host)
                }
                'S' -> {
                    onDataArrivedListener?.onStartGame(pre, data, host)
                }
                'U' -> {
                    onDataArrivedListener?.onUpdateScore(pre, data, host)
                }
                'T' -> {
                    onDataArrivedListener?.onShowTips(pre, data, host)
                }
                'D' -> {
                    onDataArrivedListener?.onCountDown(pre, data, host)
                }
                'N' -> {
                    onDataArrivedListener?.onNextPainter(pre, data, host)
                }
                'C' -> {
                    onDataArrivedListener?.onChat(pre, data, host)
                }
                'P' -> {
                    onDataArrivedListener?.onPaint(pre, data, host)
                }
                else -> {
                }
            }
        }
    }

    open fun onShowAnswer(pre: Char, msg: String, host: String) {}
    open fun onRoomResult(pre: Char, msg: String, host: String) {}
    open fun onStartGame(pre: Char, msg: String, host: String) {}
    open fun onShowTips(pre: Char, msg: String, host: String) {}
    open fun onCountDown(pre: Char, msg: String, host: String) {}
    open fun onChat(pre: Char, msg: String, host: String) {}
    open fun onPaint(pre: Char, msg: String, host: String) {}
    abstract fun onFindRoom(pre: Char, msg: String, host: String)
    abstract fun onJoinRoom(pre: Char, msg: String, host: String)
    abstract fun onUpdateScore(pre: Char, msg: String, host: String)
    abstract fun onExitRoom(pre: Char, msg: String, host: String)
    abstract fun onNextPainter(pre: Char, msg: String, host: String)
    abstract fun onHeart(pre: Char, msg: String, host: String)

    open fun showAnswer(msg: String, host: String) {
        sendData("A$msg", host)
    }

    protected fun findRoom(msg: String = "", host: String) {
        sendData("F$msg", host)
    }

    open fun roomResult(msg: String, host: String) {
        sendData("R$msg", host)
    }

    open fun joinRoom(msg: String, host: String) {
        sendData("J$msg", host)
    }

    open fun startGame(msg: String = "", host: String) {
        sendData("S$msg", host)
    }

    open fun updateScore(msg: String, host: String) {
        sendData("U$msg", host)
    }

    open fun showTips(msg: String, host: String) {
        sendData("T$msg", host)
    }

    open fun countDown(msg: String, host: String) {
        sendData("C$msg", host)
    }

    open fun exitRoom(msg: String = "", host: String) {
        sendData("E$msg", host)
    }

    open fun nextPainter(msg: String = "", host: String) {
        sendData("N$msg", host)
    }

    open fun chat(msg: String, host: String) {
        sendData("C$msg", host)
    }

    open fun paint(msg: String, host: String) {
        sendData("P$msg", host)
    }

    open fun heart(msg: String = "", host: String) {
        sendData("H${System.currentTimeMillis()}", host)
    }
}