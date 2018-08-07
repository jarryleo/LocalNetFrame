package cn.leo.aio.header

import cn.leo.aio.utils.Constant
import cn.leo.aio.utils.Logger
import java.nio.ByteBuffer

class Packet() {
    constructor(packet: ByteBuffer) : this() {
        packet.flip()
        val array = packet.array().copyOf(packet.limit())
        val magicFlag = array.copyOf(3)
        //判断接收的数据包是否有数据头
        if (!Constant.magic.contentEquals(magicFlag) || array.size < 16) {
            //野数据
            ver = 0
            len = array.size
            data = array
            Logger.d("收到野数据")
        } else {
            //本框架数据
            ver = array[3]
            cmd = PacketFactory.getInt(array.copyOfRange(4, 6)).toShort()
            len = PacketFactory.getInt(array.copyOfRange(6, 10))
            data = array.copyOfRange(16, array.size)
        }
    }

    //组装数据包
    fun addData(packet: ByteBuffer): Packet {
        packet.flip()
        val array = packet.array().copyOf(packet.limit())
        val size = array.size
        if (data!!.size + size > len) {
            val last = len - data!!.size
            val lastBytes = array.copyOf(last)
            data = data!! + lastBytes
            val out = array.copyOfRange(last, array.size)
            return Packet(ByteBuffer.wrap(out))
        }
        data = data!! + array
        return this
    }

    //数据包是否完整
    fun isFull() = len == data!!.size

    var ver: Byte = 1
    var cmd: Short = 0
    var len = 0
    var data: ByteArray? = null

}