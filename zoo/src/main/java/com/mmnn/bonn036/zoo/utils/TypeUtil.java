package com.mmnn.bonn036.zoo.utils;

import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TypeUtil {
	private static final String TAG = "TypeUtil";
	public static String byteArrayToString(byte[] bytes) {
		String result = new String(bytes);
		return result;
	}
	public static int getIntIP(String address) {
		try {
			InetAddress addr = InetAddress.getByName(address);
			Log.d(TAG, "address int: "+inetAddress2Int(addr));
			return inetAddress2Int(addr);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static InetAddress int2InetAddress(int i) {
		InetAddress inetAddress;
		try {
			inetAddress = InetAddress.getByAddress(int2bytes(i));
		} catch (Exception ex) {
			inetAddress = null;
			Log.e(TAG, "Exception: " + ex.toString());
		}
		return inetAddress;
	}
	
	public static int inetAddress2Int(InetAddress inetAddress) {
		byte[] data = inetAddress.getAddress();
		return bytes2int(data);
	}
	
	public static short bytes2short(byte[] data) {
		short i = 0;
	    i |= (int)((data[0] & 0x000000FF)<< 8);
        i |= (int)((data[1] & 0x000000FF)    );
		return i;
	}
	
	public static byte[] short2bytes(short i) {
		byte[] data = new byte[2];
		data[0] = (byte)((i>> 8) & 0x000000FF);
	    data[1] = (byte)((i    ) & 0x000000FF);
		return data;
	}
	public static int bytes2int(byte[] data) {
        int i = 0;
        i |= (data[0] & 0x000000FF)<<24;
        i |= (data[1] & 0x000000FF)<<16;
        i |= (data[2] & 0x000000FF)<< 8;
        i |= (data[3] & 0x000000FF);
		return i;
	}
	
	public static byte[] int2bytes(int i) {
	    byte[] data = new byte[4];
	    data[0] = (byte)((i>>24) & 0x000000FF);
	    data[1] = (byte)((i>>16) & 0x000000FF);
	    data[2] = (byte)((i>> 8) & 0x000000FF);
	    data[3] = (byte)((i    ) & 0x000000FF);
	    return data;
	}
	
	public static byte[] long2bytes(long i) {
	    byte[] data = new byte[8];
	    data[0] = (byte)((i>>56) & 0x000000FF);
	    data[1] = (byte)((i>>48) & 0x000000FF);
	    data[2] = (byte)((i>>40) & 0x000000FF);
	    data[3] = (byte)((i>>32) & 0x000000FF);
	    data[4] = (byte)((i>>24) & 0x000000FF);
	    data[5] = (byte)((i>>16) & 0x000000FF);
	    data[6] = (byte)((i>> 8) & 0x000000FF);
	    data[7] = (byte)((i    ) & 0x000000FF);
	    return data;
	}
	
	public static long bytes2long(byte[] data) {
        long i = 0;
        i |= (data[0] & 0x000000FF)<<56;
        i |= (data[1] & 0x000000FF)<<48;
        i |= (data[2] & 0x000000FF)<<40;
        i |= (data[3] & 0x000000FF)<<32;
        i |= (data[4] & 0x000000FF)<<24;
        i |= (data[5] & 0x000000FF)<<16;
        i |= (data[6] & 0x000000FF)<< 8;
        i |= (data[7] & 0x000000FF);
		return i;
	}
}
