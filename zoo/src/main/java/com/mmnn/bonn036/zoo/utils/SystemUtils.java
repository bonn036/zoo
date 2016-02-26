package com.mmnn.bonn036.zoo.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.mmnn.bonn036.zoo.MyApp;

import java.util.Locale;

public class SystemUtils {

    public static boolean IS_MIUI_V6 = isExistClass("miui.app.Activity");
    public static boolean IS_MIUI = isExistClass("miui.app.Activity");
    public static boolean IS_X1 = Build.MODEL.contains("MI-ONE") || Build.MODEL.contains("mi-one");

    private static SystemUtils sInstance;
    private int mScreenWidth;
    private int mScreenHeight;
    private String mImei;
    private String mEncryptImei;
    private String mNetworkCountryIso;
    private String mSimCountryIso;
    private String mModelDevice;
    private int mAppVersion = 0;
    private boolean mIsInChina = false;
    private boolean mIsInChinaMainland = false;

    public static SystemUtils getInstance() {
        if (sInstance == null) {
            sInstance = new SystemUtils();
            sInstance.init();
        }
        return sInstance;
    }

    private void init() {
        Context appContext = MyApp.getInstance().getApplicationContext();

        WindowManager wm = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;

        TelephonyManager telephonyManager = (TelephonyManager) appContext.getSystemService(Context.TELEPHONY_SERVICE);
        mImei = telephonyManager.getDeviceId();
        mEncryptImei = computeEncryptImei();
        mNetworkCountryIso = telephonyManager.getNetworkCountryIso();
        mSimCountryIso = telephonyManager.getSimCountryIso();

        try {
            PackageInfo info = appContext.getPackageManager().getPackageInfo(appContext.getPackageName(), 0);
            mAppVersion = info.versionCode;
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        try {
            mModelDevice = (String) Class.forName("android.os.SystemProperties").
                    getDeclaredMethod("get", new Class[]{String.class, String.class}).
                    invoke(null, "ro.product.mod_device", "");
//			android.os.SystemProperties.get("ro.product.mod_device", "").endsWith("_global");
        } catch (Exception e) {
            e.printStackTrace();
        }

        String country = getUseCountry();
        //				|| country.equalsIgnoreCase("tw")
        mIsInChina = country.equalsIgnoreCase("zh")
                || country.equalsIgnoreCase("hk")
                || country.equalsIgnoreCase("tw")
                || country.equalsIgnoreCase("mo")
                || country.equalsIgnoreCase("cn");

        mIsInChinaMainland = country.equalsIgnoreCase("zh")
                || country.equalsIgnoreCase("cn");

    }

    public int getAppVersion() {
        return mAppVersion;
    }

    public String getImei() {
        return mImei;
    }

    public String getEncryptImei() {
        return mEncryptImei;
    }

    private String computeEncryptImei() {
//        String keyString = "asdfghjkl;mnbvcx";
//        String key = keyString;
//        try {
//            key = new String(Base64.encode(keyString.getBytes("UTF-8"), Base64.DEFAULT));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        String imei = SystemUtils.getInstance().getImei();
//        if (imei == null) {
//            imei = "0";
//        }
//        String encryptedIMEI = imei;
//        try {
//            encryptedIMEI = SignatureUtil.encrypt(imei, key);
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }
//        String encodeEncryptIMEI = encryptedIMEI;
//        try {
//            encodeEncryptIMEI = URLEncoder.encode(encryptedIMEI, HTTP.UTF_8);
//        } catch (UnsupportedEncodingException e2) {
//            e2.printStackTrace();
//        }
//
//        if (encodeEncryptIMEI == null) {
//            encodeEncryptIMEI = "0";
//        }
//        return encodeEncryptIMEI;
        return "";
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public String getUseCountry() {
        if (mNetworkCountryIso != null && mNetworkCountryIso.length() > 0) {
            return mNetworkCountryIso;
        }
        if (mSimCountryIso != null && mSimCountryIso.length() > 0) {
            return mSimCountryIso;
        }
        return Locale.getDefault().getCountry();
    }

    public boolean isInChina() {
        return mIsInChina;
    }

    public boolean isInChinaMainland() {
//		mIsInChinaMainland = false;
        return mIsInChinaMainland;
    }

    public int getScreenWidth() {
        return mScreenWidth;
    }

    public int getScreenHeight() {
        return mScreenHeight;
    }

    public static boolean isExistClass(String className) {
        Class result;
        try {
            result = Class.forName(className);
        } catch (ClassNotFoundException e) {
//			e.printStackTrace();
            return false;
        }
        return result != null;
    }
}
