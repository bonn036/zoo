package com.mmnn.zoo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.mmnn.zoo.service.model.MyGift;

/**
 * Created by dz on 2016/10/26.
 */
public class MyService extends Service {

    private MyBinder mBinder = new MyBinder();

    private IMyDo.Stub mMyDoBinder = new IMyDo.Stub() {
        @Override
        public MyGift onRead() throws RemoteException {
            return new MyGift(Process.myPid(), getApplication().getPackageName());
        }

        @Override
        public boolean onWrite(MyGift myGift) throws RemoteException {
            if (myGift != null) {
                Log.e("MMNNService", "write from: " + myGift.getPid() + " " + myGift.getName());
                return true;
            }
            return false;
        }
    };

    private Messenger mMessenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 9036: {
                    Log.e("MMNNService", "message from: " + msg.arg1);
                    Message reply = Message.obtain(null, 9036);
                    reply.arg1 = Process.myPid();
                    try {
                        msg.replyTo.send(reply);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                default:
                    super.handleMessage(msg);
            }
        }
    });

    @Override
    public IBinder onBind(Intent intent) {
//        return mBinder;
//        return mMessenger.getBinder();
        return mMyDoBinder;
    }

    public void doSomeThing() {
        Toast.makeText(this, "hello by my service", Toast.LENGTH_SHORT).show();
    }

    public class MyBinder extends Binder {

        public MyService getService() {
            return MyService.this;
        }

    }
}
