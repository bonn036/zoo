package com.mmnn.bonn036.zoo.utils;

import android.util.Log;


public class Utils {

    private final static String TAG = Utils.class.getSimpleName();

    public static int[] jointIntArrays(final int[] arrayStart, final int[] arrayEnd) {
        if (arrayStart == null || arrayEnd == null) {
            return null;
        }
        int[] result = new int[arrayStart.length + arrayEnd.length];
        System.arraycopy(arrayStart, 0, result, 0, arrayStart.length);
        System.arraycopy(arrayEnd, 0, result, arrayStart.length, arrayEnd.length);
        return result;
    }

    public static void printArray(int[] data) {
        if (data == null) {
            Log.e(TAG, "data null");
            return;
        }
        Log.i(TAG, "data len: " + data.length);
        String dataStr = "data: ";
        for (int j = 0; j < data.length; j++) {
            dataStr += data[j] + ", ";
        }
        Log.i(TAG, dataStr);
    }
}
