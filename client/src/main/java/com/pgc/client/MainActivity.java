package com.pgc.client;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.pgc.testaidldemo1.IAidlCallBack;
import com.pgc.testaidldemo1.IAidlInterface;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.list_view)
    ListView listView;
    private IAidlInterface iAidlInterface;
    private int num;
    private List<String> messages=new ArrayList<>();
    private ArrayAdapter arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Intent intent=new Intent();
        String ACTION = "AIDL.service";
        intent.setAction(ACTION);
        intent.setPackage("com.pgc.testaidldemo1");
        bindService(intent,serviceConnection,BIND_AUTO_CREATE);
    }


    @OnClick(R.id.send_message)
    public void onViewClicked(View view) {
        if (iAidlInterface!=null){
            try {
                iAidlInterface.sendMessage("客户端消息"+num);
                num++;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Toast.makeText(getApplicationContext(),"已连接服务器",Toast.LENGTH_LONG).show();
            iAidlInterface=IAidlInterface.Stub.asInterface(iBinder);
            try {
                iAidlInterface.asBinder().linkToDeath(mDeathRecipient, 0);
                iAidlInterface.registerCallBack(iAidlCallBack);
                messages.addAll(iAidlInterface.getMessages());
                listView.setAdapter(arrayAdapter=new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,messages));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        //当承载IBinder的进程消失时接收回调的接口
        @Override
        public void binderDied() {
            if (null == iAidlInterface) {
                return;
            }
            iAidlInterface.asBinder().unlinkToDeath(mDeathRecipient, 0);
            iAidlInterface = null;
            //断线重来逻辑
        }
    };

    private IAidlCallBack iAidlCallBack=new IAidlCallBack.Stub() {
        @Override
        public void onMessageSuccess(String message) {
            if (messages!=null&&arrayAdapter!=null){
                messages.add(message);
                handler.sendEmptyMessage(1);
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            arrayAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onDestroy() {
        //解除注册
        if (null != iAidlInterface && iAidlInterface.asBinder().isBinderAlive()) {
            try {
                iAidlInterface.unregisterCallBack(iAidlCallBack);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        //解除绑定服务
        unbindService(serviceConnection);
        super.onDestroy();
    }
}
