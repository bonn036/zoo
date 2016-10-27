package com.mmnn.zoo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.mmnn.zoo.service.IMyDo;
import com.mmnn.zoo.service.MyService;
import com.mmnn.zoo.service.model.MyGift;

public class DrawerActivity extends Activity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private IMyDo mMyDo;
    private Messenger mMessenger;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMyDo = IMyDo.Stub.asInterface(service);
//            mMessenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_drawer);

        View fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        DrawerLayout.DrawerListener drawerListener = new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        };
        mDrawerLayout.addDrawerListener(drawerListener);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MyService.class);
        intent.setAction(IMyDo.class.getName());
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mMyDo != null || mMessenger != null) {
            unbindService(mConnection);
        }
    }

    private void toggle() {
//        int drawerLockMode = mDrawerLayout.getDrawerLockMode(GravityCompat.START);
//        if (mDrawerLayout.isDrawerVisible(GravityCompat.START)
//                && (drawerLockMode != DrawerLayout.LOCK_MODE_LOCKED_OPEN)) {
//            mDrawerLayout.closeDrawer(GravityCompat.START);
//        } else if (drawerLockMode != DrawerLayout.LOCK_MODE_LOCKED_CLOSED) {
//            mDrawerLayout.openDrawer(GravityCompat.START);
//        }

        if (mMyDo != null) {
            try {
                MyGift gift = mMyDo.onRead();
                Log.e("MMNNService", "read from: " + gift.getPid() + " " + gift.getmName());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        if (mMessenger != null) {
            try {
                Message msg = Message.obtain(null, 9036);
                msg.arg1 = Process.myPid();
                mMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}

