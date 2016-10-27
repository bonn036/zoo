package com.mmnn.zoo.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class SystemInfoUtil {
	
	private static final String BUILD_DEVICE;
	private static final String PLATFORM = "platform_id";
	
	static {
		BUILD_DEVICE = Build.DEVICE;
	}

	public static int getTVBoxPlatformID(ContentResolver resolver) {
		try {
			return Settings.System.getInt(resolver, PLATFORM);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static int getTVBoxPlatformID() {
		if (BUILD_DEVICE == null) {
			return 0;
		}
        if (BUILD_DEVICE.startsWith("augustrush")) {
        	return 204;
        } else if (BUILD_DEVICE.startsWith("casablanca")) {
        	return 205;
        } else if (BUILD_DEVICE.startsWith("braveheart")) {
        	return 601;
        } else if (BUILD_DEVICE.startsWith("dredd")) {
			return 206;
		} else if (BUILD_DEVICE.startsWith("entrapment")) {
			return 602;
		} else {
			return 0;
		}
	}
	
	public static int getPhonePlatformID(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		if (dm.widthPixels < 720) {
			return 301;
		} else {
			return 302;
		}
	}
	
	public static int getPlatformID(Context context) {
		final int platform = getTVBoxPlatformID();
		if (0 == platform) {
			return getPhonePlatformID(context);
		}
		return platform;
	}
	
	public static boolean isSystemApp(Context context, String packageName) {
		PackageManager pm = context.getPackageManager();
		try {
			ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
			return info != null && (info.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

}
