package com.mmnn.bonn036.zoo.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.mmnn.bonn036.zoo.exception.CipherException;
import com.mmnn.bonn036.zoo.exception.InvalidResponseException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Proxy.Type;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class NetworkUtil {

    public static final int CONNECT_TIMEOUT = 30 * 1000;
    public static final int READ_TIMEOUT = 15 * 1000;
    public static final String CMWAP_GATEWAY = "10.0.0.172";
    public static final String CMWAP_HEADER_HOST_KEY = "X-Online-Host";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_DELETE = "DELETE";
    private static final String TAG = "NetworkUtil";

    /**
     * 向服务端提交HttpPost请求 设置为30秒钟连接超时，发送数据不超时；
     *
     * @param url : HTTP post的URL地址
     *            : HTTP post参数
     * @throws IOException : 调用过程中可能抛出到exception
     */

    public static String doHttpPost(Context context, String url,
                                    String strParams) throws IOException {
        if (TextUtils.isEmpty(url))
            throw new IllegalArgumentException("url");

        HttpURLConnection conn = getHttpUrlConnection(context, new URL(url));
        Log.i(TAG, "Http con: " + conn);
        conn.setConnectTimeout(CONNECT_TIMEOUT);
        conn.setReadTimeout(READ_TIMEOUT);
        conn.setRequestMethod("POST");

        if (null == strParams) {
            throw new IllegalArgumentException("nameValuePairs");
        }
        /*Log.i(TAG, "URL: " + url);
        Log.i(TAG, "Params: " + strParams);*/
        conn.setDoOutput(true);
        byte[] b = strParams.getBytes();
        conn.getOutputStream().write(b, 0, b.length);
        Log.d(TAG, "conn write");
        conn.getOutputStream().flush();
        Log.d(TAG, "conn flush");
        conn.getOutputStream().close();
        int statusCode = conn.getResponseCode();
        Log.d(TAG, "Http POST Response Code: " + statusCode);

        BufferedReader rd = new BufferedReader(new InputStreamReader(
                new DoneHandlerInputStream(conn.getInputStream())));
        String tempLine = rd.readLine();
        StringBuilder tempStr = new StringBuilder();
        String crlf = System.getProperty("line.separator");
        while (tempLine != null) {
            tempStr.append(tempLine);
            tempStr.append(crlf);
            tempLine = rd.readLine();
        }
        String responseContent = tempStr.toString();
        rd.close();

        return responseContent;
    }

    public static HttpURLConnection getHttpUrlConnection(Context context,
                                                         URL url) throws IOException {
        if (isCtwap(context)) {
            java.net.Proxy proxy = new java.net.Proxy(Type.HTTP,
                    new InetSocketAddress("10.0.0.200", 80));
            return (HttpURLConnection) url.openConnection(proxy);
        }
        if (!isCmwap(context)) {
            return (HttpURLConnection) url.openConnection();
        } else {
            String host = url.getHost();
            String cmwapUrl = getCMWapUrl(url);
            URL gatewayUrl = new URL(cmwapUrl);
            HttpURLConnection conn = (HttpURLConnection) gatewayUrl
                    .openConnection();
            conn.addRequestProperty(CMWAP_HEADER_HOST_KEY, host);
            return conn;
        }
    }

    public static String getCMWapUrl(URL oriUrl) {
        StringBuilder gatewayBuilder = new StringBuilder();
        gatewayBuilder.append(oriUrl.getProtocol()).append("://")
                .append(CMWAP_GATEWAY).append(oriUrl.getPath());
        if (!TextUtils.isEmpty(oriUrl.getQuery())) {
            gatewayBuilder.append("?").append(oriUrl.getQuery());
        }
        return gatewayBuilder.toString();
    }

    public static boolean isCmwap(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String countryISO = tm.getSimCountryIso();
        if (!"CN".equalsIgnoreCase(countryISO)) {
            return false;
        }
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
            return false;
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null)
            return false;
        String extraInfo = info.getExtraInfo();
        if (TextUtils.isEmpty(extraInfo) || (extraInfo.length() < 3))
            return false;
        return !extraInfo.contains("ctwap") && extraInfo.regionMatches(true, extraInfo.length() - 3, "wap", 0, 3);
    }

    public static boolean isCtwap(Context context) {
        // 如果不是中国sim卡，直接返回否
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String countryISO = tm.getSimCountryIso();
        if (!"CN".equalsIgnoreCase(countryISO)) {
            return false;
        }
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
            return false;
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null)
            return false;
        String extraInfo = info.getExtraInfo();
        return !(TextUtils.isEmpty(extraInfo) || (extraInfo.length() < 3)) && extraInfo.contains("ctwap");

    }

    public static String fromParamListToString(
            List<NameValuePair> nameValuePairs) {
        StringBuffer params = new StringBuffer();
        for (NameValuePair pair : nameValuePairs) {
            try {
                if (pair.getValue() == null)
                    continue;
                params.append(URLEncoder.encode(pair.getName(),
                        IOUtil.CHARSET_NAME_UTF_8));
                params.append("=");
                params.append(URLEncoder.encode(pair.getValue(),
                        IOUtil.CHARSET_NAME_UTF_8));
                params.append("&");
            } catch (UnsupportedEncodingException e) {
                Log.d(TAG, "Failed to convert from param list to string: "
                        + e.toString());
                Log.d(TAG, "pair: " + pair.toString());
                return null;
            }
        }
        if (params.length() > 0) {
            params = params.deleteCharAt(params.length() - 1);
        }
        return params.toString();
    }

    public static String doHttpGetStr(String urlString, String cookie) {
        return doHttpOperation(urlString, null, cookie, "GET");
    }

//	public static JSONObject doHttpGet(String urlString, String cookie) {
//		JSONObject response = null;
//		String resultString = doHttpGetStr(urlString, cookie);
//		if (resultString != null) {
//			try {
//				response = new JSONObject(resultString);
//				Log.i(TAG, "result : " + response.toString());
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		}
//		return response;
//
//	}

    public static JSONObject doHttpGetEncrypt(String serverUrl,
                                              List<NameValuePair> params, String cookie, String security) {

        List<NameValuePair> encrytParams = null;
        try {
            encrytParams = NetworkUtil.encryptParams(params, security);
        } catch (CipherException e1) {
            e1.printStackTrace();
        }
/*		if(encrytParams == null){
            return null;
		}*/
        Map<String, String> map = IOUtil.list2Map(encrytParams);
        if (map == null) {
            Log.i(TAG, "list to map error .");
            return null;
        }
        String signature = CloudCoder.generateSignature("GET", serverUrl, map,
                security);
        if (signature == null || signature.isEmpty()) {
            Log.i(TAG, "signature is null .");
            return null;
        }
        encrytParams.add(new BasicNameValuePair("signature", signature));

        String urlString = fromParamListToString(encrytParams);
        if (urlString != null) {
            serverUrl += "?" + urlString;
        }

        String result = doHttpGetStr(serverUrl, cookie);

        JSONObject response = null;
        String decryptedResult = null;
        try {
            decryptedResult = decryptResponse(result, security);
        } catch (CipherException e) {
            Log.i(TAG, "decrypt error : " + e.getMessage());
        } catch (InvalidResponseException e) {
            Log.i(TAG, "decrypt error : " + e.getMessage());
        }
        if (decryptedResult != null) {
            try {
                response = new JSONObject(decryptedResult);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

//	 public static JSONObject doHttpGet(String urlString) {
//	 return doHttpGet(urlString, null);
//	 }

    @SuppressLint("DefaultLocale")
    public static String getMacAddress(String name, boolean withColon) {
        try {
            Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces();
            Log.e(TAG, "get NetworkInterface : " + en);
            while (en.hasMoreElements()) {
                Log.e(TAG, "en hasMoreElements ");
                NetworkInterface intf = en.nextElement();
                Log.d(TAG, intf.toString());

                if (intf.getName().toLowerCase().contains(name)) {
                    byte[] ha = intf.getHardwareAddress();
                    if (ha != null) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < ha.length; ++i) {
                            if (withColon && (i > 0)) {
                                sb.append(":");
                            }
                            sb.append(String.format("%1$02x", ha[i]));
                        }
                        return sb.toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(TAG, "get mac error : " + ex.getMessage());
        }

        return "";
    }

    public static boolean isWifiUsed(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI);
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public static List<NameValuePair> encryptParams(List<NameValuePair> params,
                                                    String aesKey) throws CipherException {
        Cipher cipher = CloudCoder.newAESCipher(aesKey, Cipher.ENCRYPT_MODE);
        if (cipher == null) {
            throw new CipherException("failed to init cipher");
        }
        List<NameValuePair> requestParams = new ArrayList<>();
        if (params != null && !(params.isEmpty())) {
            for (NameValuePair nameValuePair : params) {
                String name = nameValuePair.getName();
                String value = nameValuePair.getValue();
                if (name != null && value != null) {
                    // do NOT encrypt params whose name starts with underscore
                    // (_)
                    if (!name.startsWith("_")) {
                        try {
                            value = Base64.encodeToString(
                                    cipher.doFinal(value.getBytes("utf-8")),
                                    Base64.NO_WRAP);
                        } catch (Exception e) {
                            throw new CipherException(
                                    "failed to encrypt request params", e);
                        }
                    }
                    requestParams.add(new BasicNameValuePair(name, value));
                }
            }
        }
        return requestParams;
    }

    public static String decryptResponse(String body, String security)
            throws CipherException, InvalidResponseException {
        Cipher cipher = CloudCoder.newAESCipher(security, Cipher.DECRYPT_MODE);
        if (cipher == null) {
            throw new CipherException("failed to init cipher");
        }
        String responseData = null;
        try {
            byte[] bytes = cipher.doFinal(Base64.decode(body, Base64.NO_WRAP));
            responseData = new String(bytes, "utf-8");
        } catch (Exception e) {
            // ignore
        }
        if (responseData == null) {
            throw new InvalidResponseException("failed to decrypt response");
        }
        return responseData;
    }

    /**
     * no trustmanager for now
     *
     * @param urlString
     * @param cookie
     * @param requestMethod
     * @param verifier
     * @return
     */
    public static String doHttpsOperation(String urlString, String body, String cookie,
                                          String requestMethod, TrustManager[] trustManagers, HostnameVerifier verifier) {
        InputStream is = null;
        String response = null;
        try {
            URL url = new URL(urlString);
            Log.i(TAG, "url: " + url);
            HttpsURLConnection httpsUrlConnection = (HttpsURLConnection) url
                    .openConnection();
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, new SecureRandom());
            httpsUrlConnection.setSSLSocketFactory(sslContext
                    .getSocketFactory());
            httpsUrlConnection.setHostnameVerifier(verifier);

            httpsUrlConnection.setReadTimeout(READ_TIMEOUT);
            httpsUrlConnection.setConnectTimeout(CONNECT_TIMEOUT);
            if (cookie != null && !cookie.isEmpty()) {
                httpsUrlConnection.setRequestProperty("Cookie", cookie);
            }

            httpsUrlConnection.setRequestMethod(requestMethod);
            if (METHOD_GET.equalsIgnoreCase(requestMethod)) {
                httpsUrlConnection.setDoInput(true);
                httpsUrlConnection.setDoOutput(false);
            } else if (METHOD_POST.equalsIgnoreCase(requestMethod)) {
                httpsUrlConnection.setDoOutput(true);
                Log.i(TAG, "body: " + body);
                if (body != null && !body.isEmpty()) {
                    httpsUrlConnection.setRequestProperty("Content-Type", "application/json");
                    httpsUrlConnection.setUseCaches(false);
                    byte[] data = body.getBytes(IOUtil.CHARSET_NAME_UTF_8);
                    httpsUrlConnection.setFixedLengthStreamingMode(data.length);
                    OutputStream out = new BufferedOutputStream(httpsUrlConnection.getOutputStream());
                    out.write(data);
                    out.flush();
                    out.close();
//					String param = "data=" + body;
//					byte[] paramBytes = param.getBytes(IOUtil.CHARSET_NAME_UTF_8);
//					httpsUrlConnection.setUseCaches(false);
//					httpsUrlConnection.setFixedLengthStreamingMode(paramBytes.length);
//					OutputStream out = new BufferedOutputStream(httpsUrlConnection.getOutputStream());
//					out.write(paramBytes);
//					out.flush();
//					out.close();
                }
            } else if (METHOD_DELETE.equalsIgnoreCase(requestMethod)) {
                httpsUrlConnection.setDoInput(true);
            }
            httpsUrlConnection.connect();

            final int code = httpsUrlConnection.getResponseCode();
            Log.d(TAG, "response code: " + code);
            if (code == HttpURLConnection.HTTP_OK) {
                is = httpsUrlConnection.getInputStream();
                response = IOUtil.inputStream2String(is,
                        IOUtil.CHARSET_NAME_UTF_8);
                Log.d(TAG, "result: " + response);
            }
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }

    /**
     * send string https request and get string response from assigned server
     * list.
     *
     * @param urlString
     * @param requestMethod
     * @return
     */
    public static String doHttpsOperation(String urlString, String body, String cookie,
                                          String requestMethod, final List<String> hostNames) {
        HostnameVerifier verifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                for (String host : hostNames) {
                    if (host.equalsIgnoreCase(hostname)) {
                        return true;
                    }
                }
                return false;
            }
        };
        TrustManager manager = new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        };
        return doHttpsOperation(urlString, body, cookie, requestMethod,
                new TrustManager[]{manager}, verifier);
    }

    public static String doHttpsOperation(String urlString, String requestMethod, final List<String> hostNames) {
        return doHttpsOperation(urlString, null, null, requestMethod, hostNames);
    }

    public static JSONObject doHttps(String urlString, String body, String cookie, String requestMethod, final List<String> hostNames) {
        String result = doHttpsOperation(urlString, body, cookie, requestMethod, hostNames);
        JSONObject object = null;
        if (result != null) {
            try {
                object = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return object;
    }

    public static JSONObject doHttps(String urlString, String requestMethod, final List<String> hostNames) {
        return doHttps(urlString, null, null, requestMethod, hostNames);
    }

    public static String doHttpOperation(String urlString, String body, String cookie, String requestMethod) {
        BufferedReader is = null;
        String response = null;
        HttpURLConnection connection = null;
        try {
            Log.i(TAG, "method: " + requestMethod);
            URL url = new URL(urlString);
            Log.i(TAG, url.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            if (cookie != null && !cookie.isEmpty()) {
                connection.setRequestProperty("Cookie", cookie);
            }
            connection.setRequestMethod(requestMethod);
            if (METHOD_GET.equalsIgnoreCase(requestMethod)) {
                connection.setDoInput(true);
                connection.setDoOutput(false);
            } else if (METHOD_POST.equalsIgnoreCase(requestMethod)) {
                connection.setDoOutput(true);
                Log.i(TAG, "body: " + body);
                if (body != null && !body.isEmpty()) {
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setUseCaches(false);
                    byte[] data = body.getBytes(IOUtil.CHARSET_NAME_UTF_8);
                    connection.setFixedLengthStreamingMode(data.length);
                    OutputStream out = new BufferedOutputStream(connection.getOutputStream());
                    out.write(data);
                    out.flush();
                    out.close();
//					String param = "data=" + body;
//					byte[] paramBytes = param.getBytes(IOUtil.CHARSET_NAME_UTF_8);
//					connection.setUseCaches(false);
//					connection.setFixedLengthStreamingMode(paramBytes.length);
//					OutputStream out = new BufferedOutputStream(connection.getOutputStream());
//					out.write(paramBytes);
//					out.flush();
//					out.close();
                }
            } else if (METHOD_DELETE.equalsIgnoreCase(requestMethod)) {
                connection.setDoInput(true);
            }
            connection.connect();

            final int code = connection.getResponseCode();
            Log.i(TAG, "response code: " + code);
            if (code == HttpURLConnection.HTTP_OK) {
                is = new BufferedReader(new InputStreamReader(
                        new DoneHandlerInputStream(connection.getInputStream())));
                String tempLine = is.readLine();
                StringBuilder tempStr = new StringBuilder();
                String crlf = System.getProperty("line.separator");
                while (tempLine != null) {
                    tempStr.append(tempLine);
                    tempStr.append(crlf);
//					Log.d(TAG, "tempLine: " + tempLine);
                    tempLine = is.readLine();
                }
                response = tempStr.toString();
                //	is = connection.getInputStream();
                //	response = IOUtil.inputStream2String(is,
                //			IOUtil.CHARSET_NAME_UTF_8);
                Log.d(TAG, "result: " + response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return response;
    }

    public static String doHttpOperation(String urlString, String requestMethod) {
        return doHttpOperation(urlString, null, null, requestMethod);
    }

//	public static String doHttpOperation(String urlString, String cookie, String requestMethod) {
//		return doHttpOperation(urlString, null, cookie, requestMethod);
//	}

    public static JSONObject doHttp(String urlString, String body, String cookie, String requestMethod) {
        String result = doHttpOperation(urlString, body, cookie, requestMethod);
        JSONObject object = null;
        if (result != null) {
            try {
                object = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return object;
    }

    public static JSONObject doHttp(String urlString, String requestMethod) {
        return doHttp(urlString, null, null, requestMethod);
    }

    /**
     * This input stream won't read() after the underlying stream is exhausted.
     * http://code.google.com/p/android/issues/detail?id=14562
     */
    public final static class DoneHandlerInputStream extends FilterInputStream {
        private boolean done;

        public DoneHandlerInputStream(InputStream stream) {
            super(stream);
        }

        @Override
        public int read(byte[] bytes, int offset, int count) throws IOException {
            if (!done) {
                int result = super.read(bytes, offset, count);
                if (result != -1) {
                    return result;
                }
            }
            done = true;
            return -1;
        }
    }

}
