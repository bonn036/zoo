package com.mmnn.zoo.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.util.DisplayMetrics;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class DeviceUtils {
    private static final String PERSIST_RADIO_MEID = "persist.radio.meid";
    private static final String PERSIST_RADIO_IMEI2 = "persist.radio.imei2";
    private static final String PERSIST_RADIO_IMEI1 = "persist.radio.imei1";
    private static final String PERSIST_RADIO_IMEI = "persist.radio.imei";

    public static List<String> getAccountNames(Context context) {
        List<String> results = new ArrayList<>();
        Account[] accounts = AccountManager.get(context).getAccounts();
        for (Account account : accounts) {
            results.add(account.name);
        }
        return results;
    }

//    public static List<String> getSimNumbers(Context context) {
//        List<String> results = new ArrayList<String>();
//        int simCount = TelephonyManager.getDefault().getIccCardCount();
//        for (int i = 0; i < simCount; i++) {
//            SimInfoRecord simInfo = SimInfoManager.getSimInfoBySlotId(context, i);
//            if (simInfo != null) {
//                results.add(simInfo.mNumber);
//            }
//        }
//        return results;
//    }

    public static Collection<String> getIMEI(Context context) {
        HashSet<String> results = new HashSet<>();
        try {
            Method get = Class.forName("android.os.SystemProperties").
                    getDeclaredMethod("get", String.class);
            get.invoke(null, "ro.product.mod_device", "");
            if (get != null) {
                String property;
                if ((property = (String) get.invoke(null, PERSIST_RADIO_MEID)) != null) {
                    results.add(property);
                }
                if ((property = (String) get.invoke(null, PERSIST_RADIO_IMEI2)) != null) {
                    results.add(property);
                }
                if ((property = (String) get.invoke(null, PERSIST_RADIO_IMEI1)) != null) {
                    results.add(property);
                }
                if ((property = (String) get.invoke(null, PERSIST_RADIO_IMEI)) != null) {
                    results.add(property);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    public static Rect getScreenSize(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        Configuration config = activity.getResources().getConfiguration();
        return new Rect(0, 0, dm.widthPixels, dm.heightPixels);
    }
}