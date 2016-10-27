package com.mmnn.zoo.network;

import android.os.Build;
import android.text.TextUtils;

import com.mmnn.zoo.network.account.Accounts;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class RequestParams implements Serializable {

    public static final int VERSION_CODE = 10000;
    private int mVersionCode = VERSION_CODE;
    private String mApiType;
    private Map<String, String> mParams = new HashMap<>();

    public RequestParams(String apiType) {
        if (TextUtils.isEmpty(apiType)) {
            throw new IllegalArgumentException("Api type can not be null.");
        }
        mApiType = apiType;
    }

    public String getApiType() {
        return mApiType;
    }

    public void putParams(String key, String value) {
        try {
            if (!TextUtils.isEmpty(key)) {
                key = URLEncoder.encode(key, "utf-8");
            }
            if (!TextUtils.isEmpty(value)) {
                value = URLEncoder.encode(value, "utf-8");
            }
            mParams.put(key, value);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: utf-8 ", uee);
        }
    }

    public void putParams(RequestParams params) {
        mParams.putAll(params.mParams);
    }

    @Override
    public String toString() {
        Map<String, String> params = new HashMap<>();
        // Base parameters
        params.put("oper", mApiType);
        params.put("deviceModel", Build.MODEL + "/" + Build.VERSION.RELEASE);
        params.put("userId", Accounts.getUserId());
        params.put("time", Long.toString(System.currentTimeMillis()));
        params.put("token", Accounts.getToken());

        // Business parameters
        params.put("data", JSONs.toJSONString(mParams));

        return "versionCode=" + mVersionCode + "&data=" + JSONs.toJSONString(params);
    }
}
