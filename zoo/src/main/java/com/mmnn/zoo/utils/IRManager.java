package com.mmnn.zoo.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;

import org.json.JSONArray;

//import android.content.Context;
//import android.hardware.ConsumerIrManager;

@SuppressLint("NewApi")
public class IRManager {
    private static final String TAG = "IRManager";
    private static final int SUPPORT_MIN_SDK_VERSION = 19;
    private static final int VIBRATOR_DURATION = 20;
    private Context mContext;
    private ConsumerIrManager mCIR;
    private Handler mWorkHandler;
    private Vibrator mVibrator;

    public IRManager(Context context) {
        mContext = context;
//		mVibrator = (Vibrator) mContext.getSystemService(android.content.Context.VIBRATOR_SERVICE);
        Log.i(TAG, "sdk version: " + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= SUPPORT_MIN_SDK_VERSION) {
            mCIR = (ConsumerIrManager) mContext.getSystemService(Context.CONSUMER_IR_SERVICE);
            Log.i(TAG, "mCIR: " + mCIR + "mCIR has emmiter: " + mCIR.hasIrEmitter());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    mWorkHandler = new Handler();
                    Looper.loop();
                }
            }).start();
        } else {
            Log.e(TAG, "Do not support this sdk version: " + Build.VERSION.SDK_INT);
        }
    }

    public boolean hasIREmitter() {
        return mCIR != null && mCIR.hasIrEmitter();
    }

    private void showTestToast(String text) {
//		Toast.makeText(mContext,  text, Toast.LENGTH_LONG).show();
    }

    public void sendIR(final int carrierFrequency, final String pattern, boolean isZip) {
        //
        Log.i(TAG, "send ir: " + carrierFrequency + " " + pattern);
        try {
            byte[] b = EncryptUtil.decrypt(pattern, EncryptUtil.DESCRYPTED_KEY);
            if (isZip) {
                b = EncryptUtil.unzip(b);
            }
            String s = new String(b, IOUtil.CHARSET_NAME_UTF_8).trim();
            Log.d(TAG, "send ir: " + s);
            JSONArray a = new JSONArray(s);
            Log.d(TAG, a.toString());
            int[] realPattern = IOUtil.jsonArrayToInts(a);
            Utils.printArray(realPattern);
            sendIR(carrierFrequency, realPattern);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public void sendIR(final int carrierFrequency, final int[] pattern) {
        Log.d(TAG, "sendIR===========");
//		mVibrator.vibrate(VIBRATOR_DURATION);
        if (pattern == null || pattern.length == 0) {
            return;
        }
        mWorkHandler.post(new Runnable() {
            @Override
            public void run() {
                boolean hasIr = mCIR.hasIrEmitter();
                Log.d(TAG, "hasIr: " + hasIr);
                if (hasIr) {
                    Log.d(TAG, "freq: " + carrierFrequency);
//					Utils.printArray(pattern);
                    mCIR.transmit(carrierFrequency, pattern);
                    showTestToast("发射成功");
                } else {
                    showTestToast("没有红外发射器");
                }
            }
        });
    }
}
