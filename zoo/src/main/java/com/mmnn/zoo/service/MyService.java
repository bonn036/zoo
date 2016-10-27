package com.mmnn.zoo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.mmnn.zoo.service.model.MyGift;

/**
 * Created by dz on 2016/10/26.
 */
public class MyService extends Service {

    //        private MyBinder mBinder = new MyBinder();
    private IMyDo.Stub mBinder = new IMyDo.Stub() {
        @Override
        public MyGift onRead() throws RemoteException {
            return new MyGift(Process.myPid(), getApplication().getPackageName());
        }

        @Override
        public boolean onWrite(MyGift myGift) throws RemoteException {
            if (myGift != null) {
                Log.e("MMNNService", "write from: " + myGift.getPid() + " " + myGift.getmName());
                return true;
            }
            return false;
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void doSomeThing() {
        Toast.makeText(this, "hello by my service", Toast.LENGTH_SHORT).show();
    }

//    public class MyBinder extends Binder {
//
//        public MyService getService() {
//            return MyService.this;
//        }
//    }
}
