package com.mmnn.zoo.utils;

import android.content.ComponentName;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;

import com.mmnn.zoo.MyApp;
import com.mmnn.zoo.R;

import java.util.List;

/**
 * Created by dz on 2015/11/20.
 */
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
    //    private static final String AUTHORITY = "com.miui.home.launcher.settings";
    private static final String TABLE_FAVORITES = "favorites";
    private static final String DEFAULT_TOOL_FOLDER_TITLE = "com.miui.home:string/default_folder_title_tools";
    private static final String XMRC_TITLE = "com.duokan.phone.remotecontroller:string/app_name";
    private static final String ACTION_MIUI_UNINSTALL_SHORTCUT = "com.miui.home.launcher.action.UNINSTALL_SHORTCUT";
    private static final String ACTION_INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
    private static final String ACTION_UNINSTALL_SHORTCUT = "com.android.launcher.action.UNINSTALL_SHORTCUT";
    private static String METHOD_CALL_IS_IN_RECOMMEND_FOLDER = "isInRecommendFolder";
    private static String METHOD_CALL_IS_IN_SYSTOOL_FOLDER = "isInSysToolFolder";
    private static String METHOD_RESULT_BOOLEAN = "result_boolean";
    private static String AUTHORITY = null;

    public static void createShortcut(Context context, String name, int iconResId, Intent shortcutIntent) {
        if (TextUtils.isEmpty(name) || shortcutIntent == null) {
            return;
        }

        shortcutIntent.setAction(Intent.ACTION_MAIN);
        shortcutIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        Intent intent = new Intent(ACTION_INSTALL_SHORTCUT);

        intent.putExtra("duplicate", false);
//        if (TextUtils.isEmpty(name)) {
//            name = "com.duokan.phone.remotecontroller:string/app_name";
//        }
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
        if (iconResId <= 0) {
            iconResId = R.mipmap.ic_launcher;
        }
        Parcelable icon = Intent.ShortcutIconResource.fromContext(context, iconResId);
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        try {
            context.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteShortCut(Context context, String shortcutName, Intent shortcutIntent) {
        Intent intent;
        if (SystemUtils.IS_MIUI) {
            intent = new Intent(ACTION_MIUI_UNINSTALL_SHORTCUT);
        } else {
            intent = new Intent(ACTION_UNINSTALL_SHORTCUT);
        }
//        Intent intent = new Intent();
//        intent.setClass(context, context.getClass());
//        intent.setAction("android.intent.action.MAIN");
        shortcutIntent.setAction(Intent.ACTION_MAIN);
        shortcutIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutName);
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        try {
            context.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    private static boolean hasShortcutInLauncher(Context context, String title) {
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

    public static boolean isShortCutExist(String title) {
        Context context = MyApp.getInstance().getApplicationContext();
        boolean isInstallShortcut = false;

        if (null == context || TextUtils.isEmpty(title)) {
            return isInstallShortcut;
        }

        if (TextUtils.isEmpty(AUTHORITY)) {
            AUTHORITY = getAuthorityFromPermission(context);
        }

        if (!TextUtils.isEmpty(AUTHORITY)) {
            try {
                final Uri CONTENT_URI = Uri.parse(AUTHORITY);
                final ContentResolver cr = context.getContentResolver();
                Cursor c = cr.query(CONTENT_URI, new String[]{"title",
                        "iconResource"}, "title=?", new String[]{title}, null);
                if (c != null && c.getCount() > 0) {
                    isInstallShortcut = true;
                }
                if (null != c && !c.isClosed()) {
                    c.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return isInstallShortcut;
    }

    public static String getAuthorityFromPermission(Context context) {
        // 获取默认
        String authority = getAuthorityFromPermissionDefault(context);
        // 获取特殊第三方
        if (authority == null || authority.trim().equals("")) {
            String packageName = getCurrentLauncherPackageName(context);
            packageName += ".permission.READ_SETTINGS";
            authority = getThirdAuthorityFromPermission(context, packageName);
        }

        if (TextUtils.isEmpty(authority)) {
            int sdkInt = android.os.Build.VERSION.SDK_INT;
            if (sdkInt < 8) { // Android 2.1.x(API 7)以及以下的
                authority = "com.android.launcher.settings";
            } else if (sdkInt < 19) {// Android 4.4以下
                authority = "com.android.launcher2.settings";
            } else {// 4.4以及以上
                authority = "com.android.launcher3.settings";
            }
            authority = "com.miui.home.launcher.settings";
        }
        authority = "content://" + authority + "/favorites?notify=true";
        return authority;
    }

    public static String getAuthorityFromPermissionDefault(Context context) {
        return getThirdAuthorityFromPermission(context,
                "com.android.launcher.permission.READ_SETTINGS");
    }

    public static String getThirdAuthorityFromPermission(Context context, String permission) {
        if (TextUtils.isEmpty(permission)) {
            return "";
        }
        try {
            List<PackageInfo> packs = context.getPackageManager()
                    .getInstalledPackages(PackageManager.GET_PROVIDERS);
            if (packs == null) {
                return "";
            }
            for (PackageInfo pack : packs) {
                ProviderInfo[] providers = pack.providers;
                if (providers != null) {
                    for (ProviderInfo provider : providers) {
                        if (permission.equals(provider.readPermission)
                                || permission.equals(provider.writePermission)) {
                            String authority = provider.authority;
                            if (!TextUtils.isEmpty(authority)
                                    && (authority
                                    .contains(".launcher.settings")
                                    || authority
                                    .contains(".twlauncher.settings") || authority
                                    .contains(".launcher2.settings")))
                                return authority;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getCurrentLauncherPackageName(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo res = context.getPackageManager().resolveActivity(intent, 0);
        if (res == null || res.activityInfo == null) {
            return "";
        }
        if (res.activityInfo.packageName.equals("android")) {
            return "";
        } else {
            return res.activityInfo.packageName;
        }
    }
}
