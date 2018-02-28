package cn.leo.localnetframe.net

import cn.leo.localnet.net.UdpFrame

/**
 * Created by Leo on 2018/2/27.
 */
class NetManager(private var listener: OnMsgArrivedListener) : UdpFrame.OnDataArrivedListener {
    private var udpFrame: UdpFrame? = null
    var host: String = "192.168.0.180"

    interface OnMsgArrivedListener {
        fun onMsgArrived(data: String)
    }

    fun startNet() {
        if (udpFrame == null) {
            udpFrame = UdpFrame(this)
            udpFrame!!.start()
        }
    }

    fun stopNet() = this.udpFrame?.stopNet()

    override fun onDataArrived(data: ByteArray, length: Int, host: String) {
        this.host = host
        decode(data, length)
    }

    fun sendData(data: ByteArray) {
        udpFrame!!.send(data, host)
    }


    /**
     * 解码信息
     * F 查找房间
     * L 返回房间信息
     * J 加入房间
     * C 聊天
     * P 画画
     * H 心跳
     * 其它 包括 房间信息分布记录，分数记录等再考虑
     */
    fun decode(data: ByteArray, length: Int) {
        val str = String(data, 0, length)
        listener.onMsgArrived(str)
        when (str.first()) {
            'C' -> {

            }
            'P' -> {
                str.substring(1)
            }
            else -> {

            }
        }
    }
}