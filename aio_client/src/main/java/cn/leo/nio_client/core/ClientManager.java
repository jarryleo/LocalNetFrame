package cn.leo.nio_client.core;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;


public class ClientManager implements ClientListener {
    public static final int STATUS_ONLINE = 1;
    public static final int STATUS_OFFLINE = 0;
    private int status;
    private static String ip;
    private static int port;
    private ClientCore client;
    private List<ClientListener> mListeners = new ArrayList<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());//切换线程
    private final HandlerThread mSendThread;
    private final Handler mSendHandler;

    public ClientManager(String ip, int port) {
        this.ip = ip;
        this.port = port;
        connectServer(ip, port);
        mSendThread = new HandlerThread("sendThread");
        mSendThread.start();
        mSendHandler = new Handler(mSendThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                client.sendMsg((byte[]) msg.obj, (short) 0);
            }
        };
    }

    private void connectServer(String ip, int port) {
        client = ClientCore.startClient(ip, port, this); //连接服务器
    }

    public void send(final byte[] bytes) {
        mSendHandler.obtainMessage(0, bytes).sendToTarget();
    }


    @Override
    public void onIntercept() {
        status = STATUS_OFFLINE;
        reConnect();
        for (final ClientListener listener : mListeners) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onIntercept();
                }
            });
        }
    }

    @Override
    public void onDataArrived(final byte[] data) {
        status = STATUS_ONLINE;
        for (final ClientListener listener : mListeners) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onDataArrived(data);
                }
            });
        }
    }

    @Override
    public void onConnectSuccess() {
        status = STATUS_ONLINE;
        for (final ClientListener listener : mListeners) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onConnectSuccess();
                }
            });
        }
    }

    @Override
    public void onConnectFailed() {
        status = STATUS_OFFLINE;
        reConnect();
    }

    private void reConnect() {
        mHandler.removeCallbacks(reConnect);
        mHandler.postDelayed(reConnect, 1000);
        for (final ClientListener listener : mListeners) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onConnectFailed();
                }
            });
        }
    }

    private Runnable reConnect = new Runnable() {
        @Override
        public void run() {
            connectServer(ip, port);
        }
    };

    public int getStatus() {
        return status;
    }

    public void addListener(ClientListener listener) {
        if (!mListeners.contains(listener))
            mListeners.add(listener);
    }

    //观察者模式，一定要移除对象，可能会造成内存泄漏
    public void removeListener(ClientListener listener) {
        mListeners.remove(listener);
    }

}
