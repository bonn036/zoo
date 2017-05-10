package com.mmnn.zoo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by dz on 2016/11/9.
 */

public class NativeActivity extends Activity {

    static {
        System.loadLibrary("zoo");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String mm = stringFromJNI();
        int nn = intFromJNI();
        Toast.makeText(this, mm + nn, Toast.LENGTH_SHORT).show();
    }

    native String stringFromJNI();

    native int intFromJNI();
}
