package com.mmnn.bonn036.zoo.utils;

import android.content.Intent;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MyTextUtils {

    public static boolean isLetterDigitOrChinese(String str) {
        String regex = "^[a-z0-9A-Z\u4e00-\u9fa5]+$";
        return str.matches(regex);
    }

    /*
     * 
     * @param str
     * 
     * @return
     */
    public static boolean isDigit(String str) {
        String regex = "^[0-9]+$";
        return str.matches(regex);
    }

    public static String getString(Intent intent, String name) {
        String text = intent.getStringExtra(name);
        return TextUtils.isEmpty(text) ? "" : text.trim();
    }

    /**
     * 判断手机号码
     */
    public static boolean isMobileNo(String mobiles) {
        boolean flag;
        try {
            Pattern p = Pattern.compile("^1\\d{10}$");
            Matcher m = p.matcher(mobiles);
            flag = m.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    public static String[] split(String src, String delimiter) {
        if (TextUtils.isEmpty(src)) {
            return null;
        }
        String[] res = src.trim().split(delimiter);
        return res;
    }

    public static boolean toBool(String bool) {
        return "1".equals(bool);
    }
}
