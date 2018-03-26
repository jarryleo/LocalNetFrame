package cn.leo.nio_client.core;

import android.os.Binder;

/**
 * Created by Leo on 2017/9/14.
 */

public abstract class ClientBinder extends Binder {
    public abstract void sendMsg(byte[] bytes);

    public abstract void addListener(ClientListener listener);

    public abstract void removeListener(ClientListener listener);

    public abstract int getConnectStatus();
}
