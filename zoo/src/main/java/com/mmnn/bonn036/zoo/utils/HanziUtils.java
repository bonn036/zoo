package com.mmnn.bonn036.zoo.utils;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Administrator on 2016/2/24.
 */
public class HanziUtils {

    public static String hanZiToPhoneticize(String hanZi) {
        String result = null;
        if (hanZi == null) {
            return null;
        }
        ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance().get(hanZi);
        if (tokens != null) {
            StringBuilder sb = new StringBuilder();
            for (HanziToPinyin.Token token : tokens) {
                if (token != null) {
                    if (HanziToPinyin.Token.PINYIN == token.type && token.target != null && !token.target.isEmpty()) {
                        sb.append(token.target.toUpperCase(Locale.getDefault()));
                    } else {
                        sb.append(token.source);
                    }
                }
                sb.append(" ");
            }
            result = sb.toString().trim();
        }
        return result;
    }

    public static String hanZiToAlphaPhoneticize(String hanZi) {
        String result = null;
        if (hanZi == null) {
            return null;
        }
        ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance().get(hanZi);
        if (tokens != null) {
            StringBuilder sb = new StringBuilder();
            for (HanziToPinyin.Token token : tokens) {
                if (token != null) {
                    if (HanziToPinyin.Token.PINYIN == token.type && token.target != null && !token.target.isEmpty()) {
                        sb.append(token.target.toUpperCase(Locale.getDefault()).charAt(0));
                    }
                    sb.append(token.source);
                }
            }
            result = sb.toString().trim();
        }
        return result;
    }
}
