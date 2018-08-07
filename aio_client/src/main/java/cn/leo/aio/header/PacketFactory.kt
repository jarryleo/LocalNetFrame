package cn.leo.aio.header

import cn.leo.aio.utils.Constant
import java.nio.ByteBuffer
import kotlin.math.min

object PacketFactory {


    fun encodePacketBuffer(data: ByteArray, cmd: Short = 0): List<ByteBuffer> {
        //数据头
        val magic = Constant.magic
        val ver: Byte = Constant.version
        val len = data.size
        val reverse = byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00, 0x00)
        //分包
        val head = magic + ver + getByteArray(cmd) + getByteArray(len) + reverse
        val packSize = Constant.packetSize - head.size
        val list = ArrayList<ByteBuffer>()
        //第一个包，携带头部
        val min = min(len, packSize)
        val dp1 = data.copyOf(min)
        val packet = head + dp1
        list.add(ByteBuffer.wrap(packet))
        //剩余的包全是数据
        var position = min
        while (position < len - 1) {
            val step = min(len - position, Constant.packetSize)
            list.add(ByteBuffer.wrap(data.copyOfRange(position, position + step)))
            position += step
        }
        return list
    }

    fun decodePacketBuffer(packet: ByteBuffer): Packet {
        return Packet(packet)
    }

    fun getInt(byteArray: ByteArray): Int {
        if (byteArray.size == 4) {
            return byteArray[0].toInt().and(0xFF).shl(24)
                    .or(byteArray[1].toInt().and(0xFF).shl(16))
                    .or(byteArray[2].toInt().and(0xFF).shl(8))
                    .or(byteArray[3].toInt().and(0xFF))
        } else if (byteArray.size == 2) {
            return byteArray[0].toInt().and(0xFF).shl(8)
                    .or(byteArray[1].toInt().and(0xFF))
        }
        return 0
    }

    private fun getByteArray(num: Int): ByteArray {
        val b1: Byte = (num.ushr(24) and 0xFF).toByte()
        val b2: Byte = (num.ushr(16) and 0xFF).toByte()
        val b3: Byte = (num.ushr(8) and 0xFF).toByte()
        val b4: Byte = (num and 0xFF).toByte()
        return byteArrayOf(b1, b2, b3, b4)
    }

    private fun getByteArray(num: Short): ByteArray {
        val toInt = num.toInt()
        val b1: Byte = (toInt.ushr(8) and 0xFF).toByte()
        val b2: Byte = (toInt and 0xFF).toByte()
        return byteArrayOf(b1, b2)
    }
}