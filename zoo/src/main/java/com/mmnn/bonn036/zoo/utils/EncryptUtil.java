package com.mmnn.bonn036.zoo.utils;

import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class EncryptUtil {
    public final static String DESCRYPTED_KEY = "fd7e915003168929c1a9b0ec32a60788";
    private final static String TAG = EncryptUtil.class.getSimpleName();

    public static byte[] decrypt(String encrypted, String key)
            throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, UnsupportedEncodingException,
            InvalidAlgorithmParameterException {
        if (null == encrypted || null == key) {
            return null;
        }
        Log.d(TAG, "encrypted: " + encrypted);
        byte[] data = null;
        try {
            data = decrypt(encrypted.getBytes(IOUtil.CHARSET_NAME_UTF_8),
                    key.getBytes(IOUtil.CHARSET_NAME_UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("data size: ", data.length + " ");
        return data;
//		return new String(data, IOUtil.CHARSET_NAME_UTF_8).trim();
    }

    public static byte[] decrypt(byte[] base64Data, byte[] rawKey)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, InvalidAlgorithmParameterException, IOException {
        if (null == base64Data || null == rawKey) {
            return null;
        }
//		Log.d(TAG, "decrypt key lenght: " + rawKey.length);
//		Log.d(TAG, "decrypt data lenght: " + base64Data.length);
//		Log.d(TAG, "base64 origin: " + new String(base64Data, "UTF-8").trim());
        Key k = new SecretKeySpec(rawKey, "AES");
        Cipher c = Cipher.getInstance("AES/ECB/NoPadding");
        c.init(Cipher.DECRYPT_MODE, k);

        byte[] decodedValue = Base64.decode(base64Data, Base64.DEFAULT);
        return c.doFinal(decodedValue);
    }

    private static String hexStr2Str(String hexStr) throws UnsupportedEncodingException {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes, "UTF-8");
    }

    public static String uncompress(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return str;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes("UTF-8"));
        GZIPInputStream gunzip = new GZIPInputStream(in);
        byte[] buffer = new byte[256];
        int n;
        while ((n = gunzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        return out.toString();
    }

    public static String compress(String str) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(str.getBytes("utf-8"));
        gzip.close();
        return out.toString("utf-8");
    }

    public static byte[] compressBytes(String str) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(str.getBytes("utf-8"));
        gzip.close();
        return out.toByteArray();
    }

    public static String uncompress(InputStream is) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
//	    GZIPInputStream gunzip = new GZIPInputStream(is);    
//	    
//	    BufferedReader bir = new BufferedReader(new InputStreamReader(gunzip, "UTF-8"));
//	    String line = null;
//	    while((line = bir.readLine()) != null) {
//	    	Log.d("##########: ", "line:" + line);
//	    }
        byte[] buffer = new byte[256 * 4];
        int n;

        while ((n = is.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        is.close();
//	    while ((n = gunzip.read(buffer)) >= 0) {    
//	       out.write(buffer, 0, n);    
//	    }       
//	    gunzip.close();
        byte[] bytes = out.toByteArray();
//	    out.close();
//	    for (byte b : bytes) {
//	    	Log.d("#########b: ", b + "");
//	    }
////	    return new String(bytes, "UTF-8");
//	    return new String(bytes, "utf-8");
//	    return new String(bytes, "ISO-8859-1");
        return uncompress(bytes);
//	    return out.toString("UTF-8");    
    }

    public static String uncompress(byte[] bytes) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        GZIPInputStream gunzip = new GZIPInputStream(in);
        byte[] buffer = new byte[256];
        int n;
        while ((n = gunzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        gunzip.close();
        byte[] rbytes = out.toByteArray();
        return new String(rbytes);
    }


    public static byte[] unzip(byte[] srcData) throws IOException {
        InputStream inputStream = new GZIPInputStream(new ByteArrayInputStream(
                srcData));
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        byte[] temp = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(temp, 0, 1024)) != -1) {
            arrayOutputStream.write(temp, 0, len);
        }
        arrayOutputStream.close();
        inputStream.close();

        return arrayOutputStream.toByteArray();
    }
}
