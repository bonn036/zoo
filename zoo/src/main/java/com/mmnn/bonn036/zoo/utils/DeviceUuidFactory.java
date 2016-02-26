package com.mmnn.bonn036.zoo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class DeviceUuidFactory {
    private static final String TAG = "DeviceUuidFactory";
    private static final String PREFS_FILE = "device_id";
    private static final String PREFS_DEVICE_ID = "device_id";
    private static UUID sUuid;

    public DeviceUuidFactory(Context context) {
        if (sUuid == null) {
            synchronized (DeviceUuidFactory.class) {
                if (sUuid == null) {
                    final SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
                    final String id = prefs.getString(PREFS_DEVICE_ID, null);

                    if (id != null) {
                        sUuid = UUID.fromString(id);
                    } else {
                        final String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
                        try {
                            //android设备的一个bug，每个设备会产生相同的ANDROID_ID:9774d56d682e549c
                            if (!androidId.equals("9774d56d682e549c")) {
                                sUuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
                            } else {
                                final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                                sUuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
                            }
                        } catch (UnsupportedEncodingException e) {
                            Log.e(TAG, "Get uuid error:" + e.getMessage());
                        }

                        prefs.edit().putString(PREFS_DEVICE_ID, sUuid.toString()).commit();
                    }
                }

            }
        }
    }

    public String getUuid() {
        return sUuid.toString();
    }
}
