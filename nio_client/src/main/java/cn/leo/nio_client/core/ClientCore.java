package cn.leo.nio_client.core;

import android.os.SystemClock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;


public class ClientCore extends Thread {
    private static final int INT_LENGTH = 4; // 一个int 占4个byte
    private static final int BUFFER_CACHE = 1024; // 缓冲区大小
    private static final int TIME_OUT = 3000; // 频道遍历超时时间
    private String mIp; // 服务器IP地址
    private int mPort; // 服务器端口号
    private ClientListener mListener; // 接口回调
    private Selector selector;
    private ByteBuffer buffer;
    private SocketChannel socketChannel;

    public static ClientCore startClient(String ip, int port, ClientListener listener) {

        ClientCore clientCore = new ClientCore(ip, port, listener);
        clientCore.start();
        return clientCore;
    }

    private ClientCore(String ip, int port, ClientListener listener) {
        mIp = ip;
        mPort = port;
        mListener = listener;
        buffer = ByteBuffer.allocate(BUFFER_CACHE);
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
            }

        } catch (Exception e) {

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
    public void sendMsg(byte[] bytes) {

        try {
            if (socketChannel.isConnected()) { // 如果连接成功，则循环发送消息
                int length = bytes.length; // 要发送数据的长度，如果长度大于缓冲区就分段发送
                int start = 0; // 分段起始点
                while (length > 0) {
                    int part = 0; // 每段大小
                    if (length >= (BUFFER_CACHE - INT_LENGTH)) {
                        part = (BUFFER_CACHE - INT_LENGTH);
                    } else {
                        part = length % (BUFFER_CACHE - INT_LENGTH); // 最后一段大小，
                    }
                    byte[] b = new byte[part]; // 分段数组

                    System.arraycopy(bytes, start, b, 0, part);// 复制分段数据
                    // 写入数据内容
                    buffer.clear(); // 清除缓冲区
                    if (start == 0) {
                        buffer.putInt(length); // 第一次分段头写入总数据长度
                    }
                    buffer.put(b);// 把字符串的字节数据写入缓冲区
                    buffer.flip();// 重置缓冲区limit
                    while (buffer.hasRemaining()) {
                        socketChannel.write(buffer); // 缓冲区数据写入频道
                    }
                    start += part;
                    length -= part;
                }
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
     * @throws IOException
     */
    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel(); // 获取key的频道
        ByteBuffer headBuffer = ByteBuffer.allocate(INT_LENGTH); // 1个int值的头字节存储数据长度
        ByteBuffer buf = (ByteBuffer) key.attachment(); // 获取key的附加对象（因为附加的缓冲区）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        while (sc.read(headBuffer) == INT_LENGTH) {
            int dataLength = headBuffer.getInt(0); // 读取数据头部4个字节的int值表述的数据长度
            headBuffer.clear();
            byte[] bytes;
            int receiveLength = 0; // 已接受长度
            int bytesRead = 0;// 读取频道内的数据到缓冲区
            while (receiveLength < dataLength) { //根据头部数据长度把所有数据读入内存输出流
                if (dataLength - receiveLength < buf.capacity()) {
                    buf.limit(dataLength - receiveLength); //调整缓冲区大小为剩余字节数
                }
                bytesRead = sc.read(buf); //读取数据到缓冲区
                buf.flip();// 重置缓冲区limit

                if (bytesRead < 1) { // 读取不到数据退出
                    break;
                }

                if (receiveLength + bytesRead > dataLength) { // 如果接受的数据大于指定长度
                    bytes = new byte[dataLength - receiveLength]; // 则新的数据为剩下数据长度
                } else {
                    bytes = new byte[bytesRead]; // 否则为读取长度
                }
                buf.get(bytes); //从缓冲区读取数据到数组
                baos.write(bytes); //写入内存输出流
                buf.clear();// 清空缓冲区
                receiveLength += bytes.length; // 已读取的数据长度
            }
            if (mListener != null) {
                mListener.onDataArrived(baos.toByteArray());
            }
            baos.reset(); //重置内存输出流
            if (bytesRead == -1) { // 服务器断开连接，关闭频道
                if (mListener != null) {
                    mListener.onIntercept();
                }
                close();
            }
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

    // 异常关闭连接
    private void close() {
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
