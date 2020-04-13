package com.pgc.testaidldemo1;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by PengGuiChu on 2020/4/11.
 */
@SuppressLint("Registered")
public class AidlService extends Service {
    private RemoteCallbackList<IAidlCallBack> callbackList= new RemoteCallbackList<>();
    private List<String> messages=new ArrayList<>();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private final IAidlInterface.Stub binder=new IAidlInterface.Stub() {
        @Override
        public void registerCallBack(IAidlCallBack iAidlCallBack) throws RemoteException {
            callbackList.register(iAidlCallBack);
        }

        @Override
        public void unregisterCallBack(IAidlCallBack iAidlCallBack) throws RemoteException {
            callbackList.unregister(iAidlCallBack);
        }

        @Override
        public void sendMessage(String message) throws RemoteException {
            messages.add(message);
            final int num=callbackList.beginBroadcast();
            for (int i=0;i<num;i++){
                IAidlCallBack iAidlCallBack=callbackList.getBroadcastItem(i);
                iAidlCallBack.onMessageSuccess(message);
            }
            callbackList.finishBroadcast();
        }

        @Override
        public List<String> getMessages() throws RemoteException {
            return messages;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
