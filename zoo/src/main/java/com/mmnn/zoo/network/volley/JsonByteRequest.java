package com.mmnn.zoo.network.volley;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

public class JsonByteRequest extends JsonRequest<byte[]> {

    public JsonByteRequest(int method, String url, String requestBody,
                           Listener<byte[]> listener, ErrorListener errorListener) {
        super(method, url, requestBody, listener, errorListener);
        setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1));
    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        if (null != response.data && response.data.length > 0) {
            return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
        }
        return null;
    }

    public String getBodyContentType() {
        return "application/x-www-form-urlencoded";
    }
}
