package com.mmnn.bonn036.zoo.utils;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;

import org.apache.http.protocol.HTTP;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CloudCoder {

    private static final String RC4_ALGORITHM_NAME = "RC4";

    /**
     * Helper class to instantiate an AES cipher with Xiaomi-specified format.
     *
     * @param aesKey AES key
     * @param opMode {@link Cipher#ENCRYPT_MODE} or {@link Cipher#DECRYPT_MODE}
     * @return the result cipher or null if failed.
     * @see Cipher
     */
    public static Cipher newAESCipher(String aesKey, int opMode) {
        byte[] keyRaw = Base64.decode(aesKey, Base64.NO_WRAP);
        Cipher cipher;
        SecretKeySpec keySpec = new SecretKeySpec(keyRaw, "AES");
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(
                    "0102030405060708".getBytes());
            cipher.init(opMode, keySpec, iv);
            return cipher;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * More generic helper class to instantiate an AES cipher.
     *
     * @param raw    used to init AES Key, its lenght must be multiple of 16,
     *               otherwise IllegalStateException will be thrown out
     * @param opMode {@link Cipher#ENCRYPT_MODE} or {@link Cipher#DECRYPT_MODE}
     * @return Cipher object, must not be null
     */
    public static Cipher newAESCipher(byte[] raw, int opMode) {
        SecretKeySpec keySpec = new SecretKeySpec(raw, "AES");
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(opMode, keySpec);
            return cipher;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("failed to init AES cipher");
    }

    /**
     * Helper class to instantiate an AES cipher. The key string is hashed with
     * MD5 algorithm, making its size be multiple of 16.
     *
     * @param keyStr arbitrary string, must not be null or empty
     * @param opMode {@link Cipher#ENCRYPT_MODE} or {@link Cipher#DECRYPT_MODE}
     * @return Cipher object, must not be null
     */
    public static Cipher newMD5AESCipher(String keyStr, int opMode) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            // ignore
        }
        if (md == null) {
            // should never be reached
            throw new IllegalStateException("failed to init MD5");
        }
        // length of AES key must be a multiple of 16
        byte[] rawData = md.digest(keyStr.getBytes());
        return newAESCipher(rawData, opMode);
    }

    public static Cipher newRC4Cipher(byte[] rc4Key, int opMode) {
        Cipher cipher;
        SecretKeySpec keySpec = new SecretKeySpec(rc4Key, RC4_ALGORITHM_NAME);
        try {
            cipher = Cipher.getInstance(RC4_ALGORITHM_NAME);
            cipher.init(opMode, keySpec);
            return cipher;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Compute SHA1 hash value for the string
     *
     * @param plain plain text. It will be encoded to BASE64 before hash
     * @return BASE64 encoded hash value
     */
    public static String hash4SHA1(String plain) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            return Base64.encodeToString(md.digest(plain.getBytes("UTF-8")),
                    Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // this should never be reached
        throw new IllegalStateException("failed to SHA1");
    }

    /**
     * Generate signature for the request.
     *
     * @param method     http request method. GET or POST
     * @param requestUrl the full request url. e.g.: http://api.xiaomi.com/getUser?id=123321
     * @param params     request params. This should be a TreeMap because the
     *                   parameters are required to be in lexicographic order.
     * @param security   AES secret key. Must NOT be null.
     * @return hash value for the values provided
     */
    public static String generateSignature(String method, String requestUrl,
                                           Map<String, String> params, String security) {
        if (TextUtils.isEmpty(security)) {
            throw new InvalidParameterException("security is not nullable");
        }
        List<String> exps = new ArrayList<String>();
        if (method != null) {
            exps.add(method.toUpperCase());
        }
        if (requestUrl != null) {
            Uri uri = Uri.parse(requestUrl);
            exps.add(uri.getEncodedPath());
        }
        if (params != null && !params.isEmpty()) {
            final TreeMap<String, String> sortedParams
                    = new TreeMap<String, String>(params);
            Set<Map.Entry<String, String>> entries = sortedParams.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                exps.add(String.format("%s=%s", entry.getKey(),
                        entry.getValue()));
            }
        }
        exps.add(security);
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (String s : exps) {
            if (!first) {
                sb.append('&');
            }
            sb.append(s);
            first = false;
        }
        return hash4SHA1(sb.toString());
    }

    public static String hashDeviceInfo(String plain) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            return Base64.encodeToString(md.digest(plain.getBytes()),
                    Base64.URL_SAFE).substring(0, 16);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("failed to init SHA1 digest");
        }
    }

    /**
     * decrypt the base64 encoded string with a base64 encoded key. It is the
     * reverse operation to {@link #encodeString(String, String, String)}
     *
     * @param security base64 encode AES key.
     * @param data     base64 encoded cipher text
     * @param charSet  charset use to encode the data
     * @return plain text
     */
    public static String decodeString(String security, String data, String charSet)
            throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        Cipher cipher = CloudCoder.newAESCipher(security, Cipher.DECRYPT_MODE);
        return decodeString(cipher, data, charSet);
    }

    /**
     * decrypt the base64 encoded string with the specified string. It is the
     * reverse operation to {@link #encodeToBase64(String, String)}
     *
     * @param keyStr     arbitrary string, used to generate AES key, must not be
     *                   null or empty
     * @param cipherText base64 encode cipher text
     * @return plain text
     */
    public static String decodeToString(String keyStr,
                                        String cipherText)
            throws IllegalBlockSizeException, BadPaddingException,
            UnsupportedEncodingException {
        Cipher cipher = newMD5AESCipher(keyStr, Cipher.DECRYPT_MODE);
        return decodeString(cipher, cipherText, null);
    }

    public static String decodeString(Cipher cipher, String data,
                                      String charSet)
            throws IllegalBlockSizeException, BadPaddingException,
            UnsupportedEncodingException {
        charSet = charSet == null ? HTTP.UTF_8 : charSet;
        byte[] decoded = Base64.decode(data.getBytes(charSet), Base64.NO_WRAP);
        return new String(cipher.doFinal(decoded), charSet);
    }

    /**
     * encrypt the plain text with a base64 encoded key, and encode thre result
     * with base66. It is the reverse operation to
     * {@link #decodeString(String, String, String)}
     *
     * @param security base64 encoded AES key
     * @param data     plain text
     * @param charset  charset of data
     * @return base64 encoded cipher text
     */
    public static String encodeString(String security, String data, String charset)
            throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        Cipher cipher = CloudCoder.newAESCipher(security, Cipher.ENCRYPT_MODE);
        return encodeString(cipher, data, charset);
    }

    /**
     * encrypt the plain text with the specified string, and encode the result
     * with base64. It is the reverse operation to
     * {@link #decodeToString(String, String)}
     *
     * @param keyStr arbitrary string, used to generate AES key, must not be
     *               null or empty
     * @param plain  plain text to encrypt
     * @return base64 encoded cipher text
     */
    public static String encodeToBase64(String keyStr, String plain)
            throws IllegalBlockSizeException, BadPaddingException,
            UnsupportedEncodingException {
        Cipher cipher = newMD5AESCipher(keyStr, Cipher.ENCRYPT_MODE);
        return encodeString(cipher, plain, null);
    }

    public static String encodeString(Cipher cipher, String data,
                                      String charset)
            throws IllegalBlockSizeException, BadPaddingException,
            UnsupportedEncodingException {
        return Base64.encodeToString(cipher.doFinal(
                        data.getBytes(charset == null ? HTTP.UTF_8 : charset)),
                Base64.NO_WRAP);
    }

    public static String getHexString(byte[] b) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            int c = (b[i] & 0xf0) >> 4;
            builder.append((char) ((c >= 0 && c <= 9) ? '0' + c : 'a' + c - 10));
            c = (b[i] & 0xf);
            builder.append((char) ((c >= 0 && c <= 9) ? '0' + c : 'a' + c - 10));
        }
        return builder.toString();
    }

    private static byte[] randomRc4Key128(String ckeyHint) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(ckeyHint.getBytes());
            return md.digest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] encodeStream(String ckeyHint, byte[] data)
            throws IllegalBlockSizeException, BadPaddingException, IOException {
        CipherInputStream stream = null;
        byte[] encodedData = null;
        try {
            Cipher coder = CloudCoder.newRC4Cipher(randomRc4Key128(ckeyHint),
                    Cipher.ENCRYPT_MODE);
            stream = new CipherInputStream(new ByteArrayInputStream(data), coder);
            encodedData = new byte[data.length];
            if (data.length != stream.read(encodedData)) {
                throw new IOException(
                        "The encoded data length is not the same with original data");
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return encodedData;
    }

    @SuppressWarnings("finally")
    public static String getFileSha1Digest(String filePath) {
        MessageDigest md = null;
        FileInputStream inStream = null;
        try {
            md = MessageDigest.getInstance("SHA1");
            File file = new File(filePath);
            inStream = new FileInputStream(file);
            byte[] buffer = new byte[1024 * 4];// Calculate digest per 4K
            int readCount = 0;
            while ((readCount = inStream.read(buffer)) != -1) {
                md.update(buffer, 0, readCount);
            }
        } catch (Exception e) {
            e.printStackTrace();
            md = null;
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (md != null) {
                return getHexString(md.digest());
            }
            return null;
        }
    }

    public static String getDataSha1Digest(final byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }

        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            md.update(data);
            return getHexString(md.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDataMd5Digest(final byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data);
            return getHexString(md.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public enum CIPHER_MODE {
        ENCRYPT,
        DECRYPT
    }
}