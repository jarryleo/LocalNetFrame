package cn.leo.nio_client.other

import cn.leo.aio.header.Packet
import cn.leo.aio.header.PacketFactory
import cn.leo.aio.utils.Constant
import cn.leo.nio_client.core.ClientCore
import cn.leo.nio_client.core.ClientListener


class Receiver(var clientListener: ClientListener) {
    var cache: Packet? = null

    //接收数据成功，result是数据长度，-1表示异常
    fun completed(result: Int?, client: ClientCore?) {
        if (result!! >= 0) {
            val buffer = client?.buffer
            if (cache == null) {
                cache = PacketFactory.decodePacketBuffer(buffer!!)
            } else {
                val packet = cache!!.addData(buffer!!)
                if (packet != cache) {
                    notifyData(cache!!.data, cache!!.cmd)
                    cache = packet
                }
            }
            //数据包完整后
            if (cache!!.isFull()) {
                notifyData(cache!!.data, cache!!.cmd)
                cache = null
            }
        } else {
            //client?.close()
        }

    }

    private fun notifyData(data: ByteArray?, cmd: Short) {
        if (cmd == Constant.heartCmd) {
            return
        }
        clientListener.onDataArrived(data!!)
    }

    fun failed(exc: Throwable?, client: ClientCore?) {
        exc!!.printStackTrace()
        clientListener.onIntercept()
        client?.close()
    }
}