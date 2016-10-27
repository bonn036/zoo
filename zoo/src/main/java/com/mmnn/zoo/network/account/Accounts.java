package com.mmnn.zoo.network.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.mmnn.zoo.network.JSONs;
import com.mmnn.zoo.network.account.LoginInfo.UserInfo;
import com.mmnn.zoo.utils.SignatureUtil;

public final class Accounts {

    private static final String PREF_NAME = "account";
    private static final String KEY_LOGIN_INFO = "key_login_info";
    private static Context sContext;
    private static UserInfo sUserInfo;
    private static UserInfo sTmpUserInfo;

    public static void init(Context context) {
        if (sContext == null) {
            sContext = context.getApplicationContext();
        }
        getUserInfo();
    }

    public static void login(UserInfo longin) {
        save(longin, KEY_LOGIN_INFO);
        sUserInfo = longin;
    }

    public static void setTmpUserInfo(UserInfo longin) {
        sTmpUserInfo = longin;
    }

    public static void saveTmpUserInfo() {
        login(sTmpUserInfo);
    }

    public static UserInfo getUserInfo() {
        if (sUserInfo == null) {
            sUserInfo = get(KEY_LOGIN_INFO, UserInfo.class);
        }
        return sUserInfo;
    }

    public static String getUserId() {
        UserInfo login = getUserInfo();
        if (login != null) {
            return login.id;
        }
        if (sTmpUserInfo != null) {
            return sTmpUserInfo.id;
        }
        return "";
    }

    public static String getUserName() {
        UserInfo login = getUserInfo();
        if (login != null) {
            return login.nickName;
        }
        if (sTmpUserInfo != null) {
            return sTmpUserInfo.nickName;
        }
        return "";
    }

    public static String getToken() {
        UserInfo login = getUserInfo();
        if (login != null) {
            return login.token;
        }
        if (sTmpUserInfo != null) {
            return sTmpUserInfo.token;
        }
        return "";
    }


    public static boolean isLogin() {
        UserInfo login = getUserInfo();
        return login != null && !TextUtils.isEmpty(login.id);
    }

    public static void logout() {
        SharedPreferences prefes = sContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefes.edit().putString(KEY_LOGIN_INFO, "").apply();
        sUserInfo = null;
    }

    private static <T> void save(T t, String key) {
        SharedPreferences prefes = sContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String value = JSONs.toJSONString(t);
        try {
            value = SignatureUtil.encryptDES(value, SignatureUtil.key);
            prefes.edit().putString(key, value).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static <T> T get(String key, Class<T> clazz) {
        SharedPreferences prefes = sContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String value = prefes.getString(key, "");
        if (TextUtils.isEmpty(value)) {
            return null;
        }
        try {
            value = SignatureUtil.decryptDES(value, SignatureUtil.key);
            return JSONs.parseObject(value, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
