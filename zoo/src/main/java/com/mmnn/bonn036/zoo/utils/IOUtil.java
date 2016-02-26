/**
 * IOUtil.java
 *
 * @author zzc(zhangchao@xiaomi.com)
 */

package com.mmnn.bonn036.zoo.utils;

import android.util.Base64;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class IOUtil {
    public static final String TAG = IOUtil.class.getSimpleName();

    // all java platforms support these charsets
    public static final String CHARSET_NAME_US_ASCII = "US-ASCII";
    public static final String CHARSET_NAME_UTF_8 = "UTF-8";
    public static final String CHARSET_NAME_UTF_16 = "UTF-16";
    public static final String CHARSET_NAME_UTF_16LE = "UTF-16LE";
    public static final String CHARSET_NAME_UTF_16BE = "UTF-16BE";
    public static final String CHARSET_NAME_ISO_8859_1 = "ISO-8859-1";

//	public static byte[] inputStream2ByteArray(InputStream is)
//			throws IOException {
//		if (null == is) {
//			return null;
//		}
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		int out = 0;
//		while ((out = is.read()) != -1) {
//			baos.write(out);
//		}
//		baos.close();
//		is.close();
//		return baos.toByteArray();
//	}

    public static byte[] inputStream2ByteArray(InputStream is)
            throws IOException {
        return inputStream2ByteArray(is, 1024);
    }

    public static byte[] inputStream2ByteArray(InputStream is,
                                               int bufferSize) throws IOException {
        if (null == is) {
            return null;
        }
        if (bufferSize < 1) {
            bufferSize = 1;
        }
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the
        // byteBuffer
        int len = 0;
        while ((len = is.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        byteBuffer.close();
        is.close();

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }

    public static String inputStream2String(InputStream is, String charsetName)
            throws IOException, IllegalCharsetNameException {
        byte[] data = inputStream2ByteArray(is);
        if (null == data) {
            return "";
        }
        if (null == charsetName || !Charset.isSupported(charsetName)) {
            return new String(data, CHARSET_NAME_UTF_8);
        } else {
            return new String(data, charsetName);
        }
    }

    public static String byteArray2HexString(byte[] bytes) {
        if (null == bytes) {
            return "";
        }
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String str = Integer.toHexString(0xFF & b);
            while (str.length() < 2) {
                str = "0" + str;
            }
            hexString.append(str);
        }
        return hexString.toString();
    }

    public static Map<String, String> list2Map(List<NameValuePair> pairs) {
        if (pairs == null)
            return null;
        Map<String, String> map = new HashMap<String, String>();
        for (NameValuePair pair : pairs) {
            final String name = pair.getName();
            final String value = pair.getValue();
            map.put(name, value);
        }
        return map;
    }

    /**
     * <p>
     * Flatten json object into Map. the data type in the json object will be
     * retained. The parameter type of the returned map is Object, so it can
     * contain arbitrary data types. The four basic json types corresponds to
     * primitive type or String or null in Java, eg.:
     * <ul>
     * <li>json number => int (or long)</li>
     * <li>json bool (true, false) => boolean</li>
     * <li>json string => String</li>
     * <li>json null => null</li>
     * </ul>
     * JSON array will be converted to List, and JSON Object will recursively be
     * converted to Object, eg.:
     * <ul>
     * <li>json array => List</li>
     * <li>json object => Object</li>
     * </ul>
     * Here are some examples:
     * </p>
     * <ul>
     * <li>{"name":"Lin"} => map.put("name", "Lin")</li>
     * <li>{"name", {"first":"Jun", "last":"Lin"}} => nameMap.put("first",
     * "Jun"); nameMap.put("last", "Lin"); map.put("name", nameMap);</li>
     * </ul>
     *
     * @param jsonObj json object to be converted
     * @return map object or null if jsonObj is null
     */
    public static Map<String, Object> jsonToMap(JSONObject jsonObj) {
        if (jsonObj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        Iterator iter = jsonObj.keys();
        while (iter.hasNext()) {
            final String key = (String) iter.next();
            final Object value = jsonObj.opt(key);
            map.put(key, convertObj(value));
        }
        return map;
    }

    public static int[] jsonArrayToInts(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }
        int data[] = null;
        int count = jsonArray.length();
        if (count > 0) {
            data = new int[count];
            try {
                for (int i = 0; i < count; i++) {
                    data[i] = jsonArray.getInt(i);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return data;
    }

    private static Object convertObj(Object obj) {
        if (obj instanceof JSONObject) {
            return jsonToMap((JSONObject) obj);
        } else if (obj instanceof JSONArray) {
            JSONArray array = (JSONArray) obj;
            final int size = array.length();
            List<Object> list = new ArrayList<Object>();
            for (int i = 0; i < size; i++) {
                list.add(convertObj(array.opt(i)));
            }
            return list;
        } else if (obj == JSONObject.NULL) {
            return null;
        }
        return obj;
    }

    public static String gzipCompress(String input) throws IOException {
        Log.i(TAG, "gzipCompress");
        if (input == null) {
            return null;
        }
//		Log.i(TAG, "input string length: " + input.length() + ", data: " + input);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gos = new GZIPOutputStream(baos);
        gos.write(input.getBytes());
        // we should close gos before get output byte array. Close gos also
        // means close baos.
        gos.close();
        byte[] base64Bytes = Base64.encode(baos.toByteArray(), Base64.DEFAULT);
//		Log.i(TAG, "output base64 byte array length: " + base64Bytes.length);
        if (base64Bytes == null) {
            return null;
        }
        String output = new String(base64Bytes, CHARSET_NAME_UTF_8);
//		Log.i(TAG, "output string length: " + output.length() + ", data: " + output);
        return output;
    }

    public static String gzipDecompress(String input) throws IOException, IllegalArgumentException {
        Log.i(TAG, "gzipDecompress");
        if (input == null) {
            return null;
        }
//		Log.i(TAG, "input string length: " + input.length() + ", data: " + input);
        byte[] base64Bytes = input.getBytes(CHARSET_NAME_UTF_8);
        if (base64Bytes == null) {
            return null;
        }
//		Log.i(TAG, "input base64 byte array length: " + base64Bytes.length);
        byte[] rawBytes = Base64.decode(base64Bytes, Base64.DEFAULT);
        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(rawBytes));
        BufferedReader bf = new BufferedReader(new InputStreamReader(gis));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = bf.readLine()) != null) {
            output.append(line);
        }
//		Log.i(TAG, "output string length: " + output.length() + ", data: " + output.toString());
        bf.close();
        return output.toString();
    }

}
