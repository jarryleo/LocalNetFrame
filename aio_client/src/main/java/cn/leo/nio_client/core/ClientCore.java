package cn.leo.nio_client.core;

import android.os.SystemClock;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;

import cn.leo.aio.header.PacketFactory;
import cn.leo.aio.utils.Constant;
import cn.leo.nio_client.other.Heart;
import cn.leo.nio_client.other.Receiver;


public class ClientCore extends Thread {
    private static final int BUFFER_CACHE = Constant.packetSize; // 缓冲区大小
    private static final long TIME_OUT = Constant.heartTimeOut; // 频道遍历超时时间
    private String mIp; // 服务器IP地址
    private int mPort; // 服务器端口号
    private ClientListener mListener; // 接口回调
    private Selector selector;
    public ByteBuffer buffer;
    private SocketChannel socketChannel;
    private Receiver mReceiver;
    private long lastHeartStamp;

    public static ClientCore startClient(String ip, int port, ClientListener listener) {

        ClientCore clientCore = new ClientCore(ip, port, listener);
        clientCore.start();
        return clientCore;
    }

    private ClientCore(String ip, int port, ClientListener listener) {
        mIp = ip;
        mPort = port;
        mListener = listener;
        buffer = ByteBuffer.allocate(Constant.packetSize);
        mReceiver = new Receiver(mListener);
        try {
            selector = Selector.open(); // 开启选择器
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        client();
    }

    private void client() {
        int timeout = 5;
        try {
            socketChannel = SocketChannel.open(); // 开启频道
            socketChannel.configureBlocking(false); // 频道设置无阻塞
            socketChannel.connect(new InetSocketAddress(mIp, mPort));// 连接服务器

            while (!socketChannel.finishConnect() && timeout > 0) {
                SystemClock.sleep(1000);// 1秒循环检测一次是否连接完毕
                timeout--;
            }

            if (socketChannel.finishConnect()) { // 如果连接成功，则循环发送消息
                socketChannel.register(selector,
                        SelectionKey.OP_READ,
                        ByteBuffer.allocate(BUFFER_CACHE)); // 绑定频道到选择器
                if (mListener != null) {
                    mListener.onConnectSuccess();// 已连接
                    executeSelector(); //进入消息检测循环
                }
                new Heart(this);//开启心跳
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally { //走到这里表示连接服务器失败
            if (mListener != null) {
                mListener.onConnectFailed();// 连接失败
            }
            close();
        }
    }

    public void executeSelector() {

        try {
            while (selector != null) {
                if (selector.select(TIME_OUT) == 0) {
                    continue;
                }
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator(); // 遍历频道选择器的连接通知，有连接就获取
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isReadable()) { // 如果是读取数据
                        handleRead(key);
                    } else if (key.isWritable() && key.isValid()) { // 可写数据
                        handleWrite(key);
                    }
                    iterator.remove(); // 处理后从队列移除
                }
            }
        } catch (IOException e) {
            if (mListener != null) {
                mListener.onIntercept();
            }
            close();
        }
    }

    /**
     * 发送数据
     *
     * @param bytes 数据字节
     */
    public void sendMsg(byte[] bytes, short cmd) {

        try {
            if (socketChannel.isConnected()) { // 如果连接成功，则循环发送消息
                List<ByteBuffer> byteBuffers = PacketFactory.INSTANCE.encodePacketBuffer(bytes, cmd);
                for (ByteBuffer byteBuffer : byteBuffers) {
                    socketChannel.write(byteBuffer); // 缓冲区数据写入频道
                }
                lastHeartStamp = System.currentTimeMillis();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (mListener != null) {
                mListener.onIntercept();
            }
            close();
        }
    }

    /**
     * 处理读取数据
     *
     * @param key key
     */
    private void handleRead(SelectionKey key) {
        buffer.clear();
        SocketChannel sc = (SocketChannel) key.channel(); // 获取key的频道
        int len;
        try {
            while ((len = sc.read(buffer)) > 0) {
                mReceiver.completed(len, this);
            }
        } catch (Exception e) {
            mReceiver.failed(e, this);
        }


    }

    /**
     * 处理写入数据
     *
     * @param key key
     * @throws IOException
     */
    private void handleWrite(SelectionKey key) throws IOException {
        ByteBuffer buf = (ByteBuffer) key.attachment(); // 拿取key附加的缓冲区
        SocketChannel sc = (SocketChannel) key.channel(); // 拿取key的频道

        buf.flip();// 重置缓冲区limit
        while (buf.hasRemaining()) {
            sc.write(buf); // 写入缓冲区数据到频道
        }
        buf.compact(); //

    }

    //心跳
    public void heart() {
        if (System.currentTimeMillis() - lastHeartStamp > Constant.heartTimeOut / 2
                && selector != null) {
            sendMsg(String.valueOf(System.currentTimeMillis()).getBytes(), Constant.heartCmd);
        }
    }

    // 异常关闭连接
    public void close() {
        try {
            if (selector != null) {
                selector.close();
            }
            if (socketChannel != null) {
                socketChannel.close();
            }
            selector = null;
            socketChannel = null;
            buffer = null;
            mListener = null;
            System.gc();
        } catch (IOException e1) {
            // e1.printStackTrace();
        }

    }
}
