package cn.leo.nio_client.core;

public interface ClientListener {
    /**
     * 连接成功
     */
    void onConnectSuccess();

    /**
     * 连接失败
     */
    void onConnectFailed();

    /**
     * 连接中断
     */
    void onIntercept();

    /**
     * 数据到达
     *
     * @param data
     */
    void onDataArrived(byte[] data);
}
