package com.mmnn.bonn036.zoo.net;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public final class ExtendedAuthToken {
    private static final String TAG = "ExtendedAuthToken";

    private static final String JSON_KEY_AUTH_TOKEN = "authToken";
    private static final String JSON_KEY_SECURITY = "security";
    private static final String JSON_KEY_EXPIRED_TIME = "expiredTime";

    private static final String SP = ",";

    public final String authToken;

    public final String security;

    public final String seperator;

    private long expiredTime = Long.MAX_VALUE;

    private ExtendedAuthToken(String authToken, String security) {
        this(authToken, security, SP);
    }

    private ExtendedAuthToken(String authToken, String security, String seperator) {
        this.authToken = authToken;
        this.security = security;
        this.seperator = seperator;
    }

    public static ExtendedAuthToken build(String authToken, String security) {
        return new ExtendedAuthToken(authToken, security);
    }

    public static ExtendedAuthToken build(String authToken, String security, String seperator) {
        return new ExtendedAuthToken(authToken, security, seperator);
    }

    public static ExtendedAuthToken parse(String plain) {
        return parse(plain, SP);
    }

    public static ExtendedAuthToken parse(String plain, String seperator) {
        Log.i(TAG, "plain: " + plain);
        if (TextUtils.isEmpty(plain)) {
            return null;
        }
        String[] parts = plain.split(seperator);
        if (parts.length != 2) {
            return null;
        }
        return new ExtendedAuthToken(parts[0], parts[1], seperator);
    }

    public static ExtendedAuthToken from(JSONObject object) {
        if (object == null) {
            Log.w(TAG, "object is null");
            return null;
        }
        try {
            final String authToken = object.getString(JSON_KEY_AUTH_TOKEN);
            final String security = object.getString(JSON_KEY_SECURITY);
            final long timeout;
            if (!object.isNull(JSON_KEY_EXPIRED_TIME)) {
                timeout = object.getLong(JSON_KEY_EXPIRED_TIME);
            } else {
                timeout = Long.MAX_VALUE;
            }
            final ExtendedAuthToken auth = new ExtendedAuthToken(authToken, security);
            auth.setExpiredTime(timeout);
            return auth;
        } catch (JSONException e) {
            Log.w(TAG, "parse json exception");
            e.printStackTrace();
        }
        return null;
    }

    public static ExtendedAuthToken from(String json) {
        if (json == null) {
            Log.w(TAG, "json string is null");
            return null;
        }
        try {
            JSONObject object = new JSONObject(json);
            return from(object);
        } catch (JSONException e) {
            Log.w(TAG, "json string to json object exception");
            e.printStackTrace();
        }
        return null;
    }

    public long getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(long time) {
        expiredTime = time;
    }

    public boolean isValid() {
        return expiredTime > System.currentTimeMillis();
    }

    public String toPlain() {
        return authToken + seperator + security;
    }

    public JSONObject toJSONObject() {
        JSONObject result = new JSONObject();
        try {
            result.put(JSON_KEY_AUTH_TOKEN, authToken);
            result.put(JSON_KEY_SECURITY, security);
            result.put(JSON_KEY_EXPIRED_TIME, expiredTime);
        } catch (JSONException e) {
            Log.w(TAG, "build json failed");
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String toString() {
        return toJSONObject().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExtendedAuthToken)) {
            return false;
        }

        ExtendedAuthToken that = (ExtendedAuthToken) o;
        if (expiredTime != that.expiredTime) {
            return false;
        }
        if (authToken != null ? !authToken.equals(that.authToken)
                : that.authToken != null) {
            return false;
        }
        return !(security != null ? !security.equals(that.security)
                : that.security != null);

    }

    @Override
    public int hashCode() {
        int result = authToken != null ? authToken.hashCode() : 0;
        result = 31 * result + (security != null ? security.hashCode() : 0);
        result = 31 * result + (int) (expiredTime ^ (expiredTime >>> 32));
        return result;
    }
}
