package com.mmnn.bonn036.zoo.utils;

import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SignatureUtil {
	private static final String TAG = SignatureUtil.class.getSimpleName();

	public static String key = "u^(&%@nu";
	private static byte[] iv = {1, 2, 3, 4, 5, 6, 7, 8};

	public static String getSignature(byte[] data, byte[] key)
			throws InvalidKeyException, NoSuchAlgorithmException {
		SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA1");
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(signingKey);
		byte[] rawHmac = mac.doFinal(data);
		return IOUtil.byteArray2HexString(rawHmac);
	}

	public static String encryptAES(String sSrc, String sKey) throws Exception {
		if (sSrc == null || sKey == null) {
			return null;
		}
		byte[] raw = Base64.decode(sKey.getBytes("UTF-8"), Base64.DEFAULT);
//		byte[] raw = Base64.decodeBase64(sKey.getBytes("UTF-8"));
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(sSrc.getBytes("UTF-8"));
//		return new String(Base64.encodeBase64(encrypted), "UTF-8");
		return Base64.encodeToString(encrypted, Base64.DEFAULT);
	}

	/**
	 * @param base64Data base64-encoded bytes
	 * @param rawKey     non-encoded bytes
	 * @return non-encoded bytes
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidAlgorithmParameterException
	 */
	public static byte[] decryptAES(byte[] base64Data, byte[] rawKey)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, InvalidAlgorithmParameterException {
		if (null == base64Data || null == rawKey) {
			return null;
		}
		Log.d(TAG, "decrypt key lenght: " + rawKey.length);
		Key k = new SecretKeySpec(rawKey, "AES/CBC/NoPadding");
		Cipher c = Cipher.getInstance("AES/CBC/NoPadding");
		c.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(new byte[16]));
		byte[] decodedValue = Base64.decode(base64Data, Base64.DEFAULT);
		return c.doFinal(decodedValue);
	}

	/**
	 * @param encrypted
	 * @param key
	 * @return
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws UnsupportedEncodingException
	 * @throws InvalidAlgorithmParameterException
	 */
	public static String decryptAES(String encrypted, String key)
			throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, UnsupportedEncodingException,
			InvalidAlgorithmParameterException {
		if (null == encrypted || null == key) {
			return null;
		}
		byte[] data = decryptAES(encrypted.getBytes(IOUtil.CHARSET_NAME_UTF_8),
				key.getBytes(IOUtil.CHARSET_NAME_UTF_8));
		return new String(data, IOUtil.CHARSET_NAME_UTF_8);
	}

	public static String getMD5(String message) {
		if (message == null) {
			return "";
		}
		return getMD5(message.getBytes());
	}

	public static String getMD5(File file) {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file.getAbsolutePath());
			byte[] buffer = new byte[1024];
			MessageDigest digest = MessageDigest.getInstance("MD5");
			int numRead = 0;
			while (numRead != -1) {
				numRead = inputStream.read(buffer);
				if (numRead > 0)
					digest.update(buffer, 0, numRead);
			}
			byte[] md5Bytes = digest.digest();
			return IOUtil.byteArray2HexString(md5Bytes);
		} catch (Exception e) {
			return null;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Exception e) {
				}
			}
		}
	}

	public static String getMD5(byte[] bytes) {
		if (bytes == null) {
			return "";
		}
		try {
			MessageDigest algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			algorithm.update(bytes);
			return IOUtil.byteArray2HexString(algorithm.digest());
		} catch (Exception e) {
		}
		return "";
	}

	public static byte[] xorByteByByte(byte[] data, byte[] key) {
		if (null == data || null == key) {
			return null;
		}
		int keyIndex = 0;
		for (int dataIndex = 0; dataIndex < data.length; dataIndex++) {
			keyIndex = dataIndex % key.length;
			data[dataIndex] ^= key[keyIndex];
		}
		return data;
	}

	public static String xorByteByByte(String data, String key)
			throws UnsupportedEncodingException {
		if (null == data || null == key) {
			return "";
		}
		byte[] result = xorByteByByte(data.getBytes(IOUtil.CHARSET_NAME_UTF_8),
				key.getBytes(IOUtil.CHARSET_NAME_UTF_8));
		return new String(result, IOUtil.CHARSET_NAME_UTF_8);
	}

	/**
	 * 加密
	 *
	 * @param encryptString
	 * @param encryptKey
	 * @return
	 * @throws Exception
	 */
	public static String encryptDES(String encryptString, String encryptKey) throws Exception {
		IvParameterSpec zeroIv = new IvParameterSpec(iv);
		SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
		byte[] encryptedData = cipher.doFinal(encryptString.getBytes());
		return Base64.encodeToString(encryptedData, Base64.DEFAULT);
	}

	/**
	 * 解密
	 *
	 * @param decryptString
	 * @param decryptKey
	 * @return
	 * @throws Exception
	 */
	public static String decryptDES(String decryptString, String decryptKey) throws Exception {
		byte[] byteMi = Base64.decode(decryptString, Base64.DEFAULT);
		IvParameterSpec zeroIv = new IvParameterSpec(iv);
		SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
		byte decryptedData[] = cipher.doFinal(byteMi);

		return new String(decryptedData);
	}
}
