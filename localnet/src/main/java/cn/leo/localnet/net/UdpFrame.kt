package cn.leo.localnet.net

import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress

/**
 * Created by Leo on 2018/2/26.
 */
class UdpFrame(private var mOnDataArrivedListener: OnDataArrivedListener) : Thread() {
    private val sendSocket = DatagramSocket()
    private val receiveSocket = DatagramSocket(NetConfig.port)
    private var handlerThread: HandlerThread = HandlerThread("sendThread")
    private var sendHandler: Handler

    interface OnDataArrivedListener {
        fun onDataArrived(data: ByteArray, length: Int, host: String)
    }

    override fun run() {
        listen()
    }

    init {
        handlerThread.start()
        sendHandler = Handler(handlerThread.looper) {
            val data = it.data
            val host = data.get("host")
            val byteArray = data.getByteArray("data")
            sendData(byteArray, host as String)
            true
        }
    }

    /**
     *发送
     */
    fun send(data: ByteArray, host: String) {
        val message = Message.obtain()
        val bundle = Bundle()
        bundle.putString("host", host)
        bundle.putByteArray("data", data)
        message.data = bundle
        sendHandler.sendMessage(message)
    }

    /**
     * 监听UDP信息,接受数据
     */
    private fun listen() {
        val data = ByteArray(NetConfig.dataSize)
        val dp = DatagramPacket(data, data.size)
        //缓存数据
        val cache = ArrayList<ByteArray>()
        while (true) {
            receiveSocket.receive(dp)
            //检查数据包头部
            val head = ByteArray(2)
            val body = ByteArray(dp.length - 2)
            //取出头部
            System.arraycopy(data, 0, head, 0, head.size)
            //取出数据体
            System.arraycopy(data, 2, body, 0, body.size)
            //安全退出
            if (head[0] == (-0xEE).toByte() && head[1] == (-0xDD).toByte()) {
                break
            }
            //数据只有1个包
            if (head[0] == 1.toByte()) {
                //数据回调给上层协议层
                mOnDataArrivedListener.onDataArrived(body, body.size,
                        dp.address.hostAddress)
            } else {
                //新的数据包组到来清空缓存
                if (head[1] == 1.toByte()) {
                    cache.clear()
                }
                //缓存数据包(漏数据包则不缓存)
                if (cache.size + 1 == head[1].toInt()) {
                    cache.add(body)
                }
                //多个数据到达完成则拼接
                if (head[0] == head[1]) {
                    //数据包完整的话
                    if (cache.size == head[0].toInt()) {
                        //开始组装数据
                        //获取数据总长度
                        val dataLength = cache.sumBy { it.size }
                        val sumData = ByteArray(dataLength)
                        //已经拼接长度
                        var length = 0
                        for (bytes in cache) {
                            System.arraycopy(bytes, 0, sumData, length, bytes.size)
                            length += bytes.size
                        }
                        //数据回调给上层协议层
                        mOnDataArrivedListener.onDataArrived(sumData, sumData.size,
                                dp.address.hostAddress)
                    } else {
                        //数据包不完整
                        Log.e("udp", " -- data is incomplete")
                    }
                }
            }
        }
        receiveSocket.disconnect()
        receiveSocket.close()
        sendSocket.close()
        handlerThread.quit()
    }

    /**
     *发送数据包
     */
    private fun sendData(data: ByteArray, host: String) {
        //发送地址
        val ia = InetSocketAddress(host, NetConfig.port)
        //已发送字节数
        var sendLength = 0
        //循环发送数据包
        while (sendLength < data.size) {
            //要发送的数据(长度不超过最小包长)
            val length = if (data.size - sendLength > NetConfig.dataSize - 2) {
                NetConfig.dataSize - 2
            } else {
                (data.size - sendLength)
            } + 2
            val pack = ByteArray(length)
            //拆分后包个数
            val packCount = data.size / (NetConfig.dataSize - 2 + 1) + 1
            //-2 表示去掉头长度，+1表示，长度刚好1个包的时候不会多出来
            //当前包序号，从1开始
            val packIndex = sendLength / (NetConfig.dataSize - 2) + 1
            val head = byteArrayOf(packCount.toByte(), packIndex.toByte())
            //添加数据头
            System.arraycopy(head, 0, pack, 0, head.size)
            System.arraycopy(data, sendLength, pack, head.size, pack.size - head.size)
            //发送小包
            val dp = DatagramPacket(pack, pack.size, ia)
            sendSocket.send(dp)
            sendLength += pack.size - 2
        }
    }

    /**
     * 安全关闭udp并释放端口
     */
    fun stopNet() {
        val ia = InetSocketAddress("127.0.0.1", NetConfig.port)
        val head = byteArrayOf((-0xEE).toByte(), (-0xDD).toByte())
        val dp = DatagramPacket(head, head.size, ia)
        sendSocket.send(dp)
    }
}