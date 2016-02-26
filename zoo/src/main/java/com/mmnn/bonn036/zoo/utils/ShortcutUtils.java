package com.mmnn.bonn036.zoo.utils;

import android.content.ComponentName;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;

import com.mmnn.bonn036.zoo.MyApp;
import com.mmnn.bonn036.zoo.R;


public class ShortcutUtils {

    public static final String EXTRA_SHORTCUT = "shortcut";
    public static final String EXTRA_MICLOUDAPP_PROVISONED = "extra_micloudapp_provisioned";
    public static final String ACTION_TOGGLE_SHORTCUT = "com.miui.action.TOGGLE_SHURTCUT";
    public static final String ACTION_SETTINGS_SHORTCUT = "com.miui.action.SETTINGS_SHURTCUT";
    public static final String ACTION_DOWNLOADING_APP = "com.miui.action.DOWNLOADING_APP";
    public static final String ACTION_ICON_UPDATE = "com.xiaomi.market.ACTION_HD_ICON_UPDATE";
    public static final String ACTION_MOVE_TO_DESKTOP = "com.miui.home.ACTION_MOVE_TO_DESKTOP";
    public static final String EXTRA_PACKAGE_NAME = "packageName";
    public static final String EXTRA_COMPONENT_NAME = "componentName";
    private static final String AUTHORITY = "com.miui.home.launcher.settings";
    private static final String TABLE_FAVORITES = "favorites";
    private static final String DEFAULT_TOOL_FOLDER_TITLE = "com.miui.home:string/default_folder_title_tools";
    private static final String XMRC_TITLE = "com.duokan.phone.remotecontroller:string/app_name";
    private static String METHOD_CALL_IS_IN_RECOMMEND_FOLDER = "isInRecommendFolder";
    private static String METHOD_CALL_IS_IN_SYSTOOL_FOLDER = "isInSysToolFolder";
    private static String METHOD_RESULT_BOOLEAN = "result_boolean";

    private static boolean isInRecommendFolder(Context context) {
        String packageName = context.getPackageName();
        Uri uri = Uri.parse("content://" + AUTHORITY + "/" + TABLE_FAVORITES);
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, new String[]{"_id"},
                    "title=?", new String[]{DEFAULT_TOOL_FOLDER_TITLE}, null);
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                long id = cursor.getInt(0);
                if (id != -1) {
                    cursor.close();
                    cursor = context.getContentResolver().query(uri, new String[]{"_id"},
                            "iconPackage=? and container=?", new String[]{packageName, String.valueOf(id)}, null);
                    return cursor != null && cursor.getCount() > 0;
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
    }

    public static boolean isInSysToolFolder() {
        Context context = MyApp.getInstance().getApplicationContext();
        String packageName = context.getPackageName();
        ContentProviderClient client = context.getContentResolver().acquireContentProviderClient(Uri.parse("content://" + AUTHORITY));
        if (client != null) {
//        IContentProvider provider = context.getContentResolver().acquireProvider(Uri.parse("content://" + AUTHORITY));
            Bundle args = new Bundle();
            String cn = (new ComponentName(packageName, "com.xiaomi.mitv.phone.remotecontroller.HoriWidgetMainActivityV2")).flattenToShortString();
            args.putString("componentName", cn);
            try {
//            result = provider.call(packageName, METHOD_CALL_IS_IN_SYSTOOL_FOLDER, null, args);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    Bundle bundle = client.call(METHOD_CALL_IS_IN_SYSTOOL_FOLDER, null, args);
                    if (bundle != null) {
                        return bundle.getBoolean(METHOD_RESULT_BOOLEAN);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                client.release();
            }
        }
        return false;
    }

    public static void moveToDesktop() {
        Context context = MyApp.getInstance().getApplicationContext();
        String cn = (new ComponentName(context.getPackageName(),
                "com.xiaomi.mitv.phone.remotecontroller.HoriWidgetMainActivityV2")).flattenToShortString();
        String ACTION_MOVE_TO_DESKTOP = "com.miui.home.ACTION_MOVE_TO_DESKTOP";
        String EXTRA_COMPONENT_NAME = "componentName";
        Intent intent = new Intent(ACTION_MOVE_TO_DESKTOP);
        intent.putExtra(EXTRA_COMPONENT_NAME, cn);
        context.sendBroadcast(intent);
    }

    private static boolean isInFolder(Context context) {
        String packageName = context.getPackageName();
        Uri uri = Uri.parse("content://" + AUTHORITY + "/" + TABLE_FAVORITES);
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, new String[]{"_id"},
                    "iconPackage=? and container > 0 and title <>?", new String[]{packageName, XMRC_TITLE}, null);
            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static boolean hasShortcutInLauncher(Context context) {
        String packageName = context.getPackageName();
        Uri uri = Uri.parse("content://" + AUTHORITY + "/" + TABLE_FAVORITES);
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, new String[]{"_id"},
                    "iconPackage=? and title =? and container < 0", new String[]{packageName, XMRC_TITLE}, null);
            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static int getShortcutCount(Context context) {
        String packageName = context.getPackageName();
        Uri uri = Uri.parse("content://" + AUTHORITY + "/" + TABLE_FAVORITES);
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, new String[]{"_id"},
                    "iconPackage=? and title =?", new String[]{packageName, XMRC_TITLE}, null);
            if (cursor != null) {
                return cursor.getCount();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }

    public static void createShortcut(Context context, String name, int iconResId, Intent intent) {
        if (TextUtils.isEmpty(name) || intent == null) {
            return;
        }
        Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcutIntent.putExtra("duplicate", false);

//        if (TextUtils.isEmpty(name)) {
//            name = "com.duokan.phone.remotecontroller:string/app_name";
//        }
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);

        if (iconResId <= 0) {
            iconResId = R.mipmap.ic_launcher;
        }
        Parcelable icon = Intent.ShortcutIconResource.fromContext(context, iconResId);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);

        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        context.sendBroadcast(shortcutIntent);
    }

}
