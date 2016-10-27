package com.mmnn.zoo.net;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class NetResponse {
    private static final String TAG = "NetResponse";

    private static final String JSON_KEY_STATUS = "status";
    private static final String JSON_KEY_DATA = "data";

    private StatusType mStatus;
    private JSONObject mData;// 服务器返回的全部内容

    public NetResponse(StatusType status) {
        this(status, null);
    }

    public NetResponse(StatusType status, JSONObject data) {
        mStatus = status;
        mData = data;
    }

    public StatusType getStatus() {
        return mStatus;
    }

    public void setStatus(StatusType status) {
        mStatus = status;
    }

    public JSONObject getData() {
        return mData;
    }

    public void setData(JSONObject data) {
        this.mData = data;
    }

    public JSONObject toJSONObject() {
        JSONObject result = new JSONObject();
        try {
            result.put(JSON_KEY_STATUS, getStatus());
            result.put(JSON_KEY_DATA, getData());
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

    public enum StatusType {
        OK(0), SERVER_ERROR(1), URL_ERROR(2), NETWORK_ERROR(3), TOKEN_ERROR(4), RESULT_ERROR(5), DECRYPT_ERROR(6), UNKNOWN_ERROR(7);
        private int _value;

        StatusType(int value) {
            _value = value;
        }

        public int getValue() {
            return _value;
        }
    }
}
