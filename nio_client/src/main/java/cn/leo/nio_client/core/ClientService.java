package cn.leo.nio_client.core;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


/**
 * Created by Leo on 2017/9/14.
 */

public class ClientService extends Service {
    ClientManager mClientManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    class MyBinder extends ClientBinder {
        @Override
        public void sendMsg(byte[] bytes) {
            mClientManager.send(bytes);
        }

        @Override
        public void addListener(ClientListener listener) {
            mClientManager.addListener(listener);
        }

        @Override
        public void removeListener(ClientListener listener) {
            mClientManager.removeListener(listener);
        }

        @Override
        public int getConnectStatus() {
            return mClientManager.getStatus();
        }

    }

    @Override
    public void onCreate() {
        mClientManager = new ClientManager(Client.getIp(), Client.getPort());
        Log.e("即时通讯服务", "onCreate: 服务开启！");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("即时通讯服务", "onDestroy: 服务被结束了！");
    }
}
