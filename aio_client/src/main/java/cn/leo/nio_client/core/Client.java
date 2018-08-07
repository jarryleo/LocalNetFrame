package cn.leo.nio_client.core;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.List;


/**
 * NIO安卓客户端
 * Created by Leo on 2017/9/14.
 */

public class Client implements Application.ActivityLifecycleCallbacks {
    @SuppressLint("StaticFieldLeak")
    private static Client mClient;
    private static String mIp;
    private static int mPort;
    private Application mApplication;
    private Intent mService;
    private MyConnection mConn;
    private ClientBinder mBinder;
    private List<ClientListener> tempListener = new ArrayList<>();

    private Client(Application application) {
        mApplication = application;
        mService = new Intent(application, ClientService.class);
        //Intent job = new Intent(application, ClientJob.class);
        mConn = new MyConnection();
        application.startService(mService);//开启即时通讯服务
        //application.startService(job);//开启定时唤醒服务
        application.bindService(mService, mConn, Context.BIND_ABOVE_CLIENT);
        application.registerActivityLifecycleCallbacks(this);
    }

    /**
     * 开启即时通信服务
     *
     * @param application app全局
     * @param ip          ip地址
     * @param port        端口号
     */
    public static void init(Application application, String ip, int port) {
        if (mClient == null) {
            mIp = ip;
            mPort = port;
            mClient = new Client(application);
        }
    }

    static String getIp() {
        return mIp;
    }

    static int getPort() {
        return mPort;
    }

    /**
     * 停止即时通信服务
     */
    public static void release() {
        mClient.destroy();
    }

    /**
     * 发送消息
     *
     * @param bytes 发送消息字节
     */
    public static void sendMsg(byte[] bytes) {
        mClient.send(bytes);
    }

    private void send(byte[] bytes) {
        if (mBinder != null)
            mBinder.sendMsg(bytes);
    }

    /**
     * 获取连接状态
     *
     * @return 返回连接状态 0 未连接 1 连接
     */
    public static int getConnectStatus() {
        return mClient.getStatus();
    }

    private int getStatus() {
        if (mBinder != null)
            return mBinder.getConnectStatus();
        return ClientManager.STATUS_OFFLINE;
    }


    private void destroy() {
        mApplication.unbindService(mConn);
        mApplication.stopService(mService);
        mService = null;
        mConn = null;
        mBinder = null;
        mApplication = null;
    }

    /**
     * 订阅消息监听
     *
     * @param listener 监听器
     */
    public static void subscribe(ClientListener listener) {
        mClient.regListener(listener);
    }

    private void regListener(ClientListener listener) {
        if (mBinder == null) {
            tempListener.add(listener);
        } else {
            mBinder.addListener(listener);
        }

    }

    /**
     * 取消订阅消息监听
     *
     * @param listener 监听器
     */
    public static void unsubscribe(ClientListener listener) {
        mClient.unRegListener(listener);
    }

    private void unRegListener(ClientListener listener) {
        if (mBinder == null) {
            tempListener.remove(listener);
        } else {
            mBinder.removeListener(listener);
        }
    }


    private class MyConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (ClientBinder) service;
            for (int i = 0; i < tempListener.size(); i++) {
                mBinder.addListener(tempListener.remove(i));
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (activity instanceof ClientListener) {
            ClientListener clientListener = (ClientListener) activity;
            if (mBinder == null) {
                tempListener.add(clientListener);
            } else {
                mBinder.addListener(clientListener);
            }
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (activity instanceof ClientListener) {
            ClientListener clientListener = (ClientListener) activity;
            if (mBinder == null) {
                tempListener.remove(clientListener);
            } else {
                mBinder.removeListener(clientListener);
            }
        }
    }

}
