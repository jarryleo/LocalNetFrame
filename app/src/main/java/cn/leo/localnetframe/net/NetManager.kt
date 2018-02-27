package cn.leo.localnetframe.net

import cn.leo.localnet.net.UdpFrame

/**
 * Created by Leo on 2018/2/27.
 */
class NetManager : UdpFrame.OnDataArrivedListener {
    private  var udpFrame: UdpFrame? = null


    fun startNet() {
        if (udpFrame == null) {
            udpFrame = UdpFrame(this)
            udpFrame!!.start()
        }
    }

    override fun onDataArrived(data: ByteArray, length: Int, host: String) {
        decode(data, length)
    }

    fun sendData(data: ByteArray) {
        //udpFrame.send(data)
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