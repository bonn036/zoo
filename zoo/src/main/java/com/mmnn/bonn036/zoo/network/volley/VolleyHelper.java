package com.mmnn.bonn036.zoo.network.volley;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

public class VolleyHelper {

    private static VolleyHelper sInstance;
    private static RequestQueue sRequestQueue;

    private VolleyHelper() {
    }

    public static synchronized VolleyHelper getInstance() {
        if (sInstance == null) {
            sInstance = new VolleyHelper();
        }
        return sInstance;
    }

    public static void initRequestQueue(Context context) {
        sRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static RequestQueue getRequestQueue() {
        return sRequestQueue;
    }

    public Request doRequest(String url, String requestBody, Response.Listener<byte[]> listener,
                             Response.ErrorListener errorListener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener must not be null.");
        }
        if (errorListener == null) {
            throw new IllegalArgumentException("Error listener must not be null.");
        }
        JsonByteRequest request = new JsonByteRequest(Request.Method.POST, url, requestBody, listener,
                errorListener);
        return getRequestQueue().add(request);
    }

    public void cancelRequest(Object tag) {
        sRequestQueue.cancelAll(tag);
    }

    public static class SimpleListener implements Response.Listener<byte[]> {
        @Override
        public void onResponse(byte[] response) {
        }
    }

    public static class SimpleErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            StringBuilder sb = new StringBuilder();
            if (error.getCause() != null) {
                sb.append(error.getCause().getLocalizedMessage()).append("\n");
            } else {
                sb.append(error.getMessage()).append("\n");
            }
            if (error.networkResponse != null) {
                sb.append("data: ").append(error.networkResponse.data).append("\n");
                sb.append("notModified: ").append(error.networkResponse.notModified).append("\n");
                sb.append("statusCode: ").append(error.networkResponse.statusCode).append("\n");
                sb.append("headers: ").append(error.networkResponse.headers);
            }
        }
    }

}
