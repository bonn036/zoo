package com.mmnn.zoo.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by dz on 2016/3/10.
 */
public class SdcardHelper {

    public static boolean isSdcardMount() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static String getSdcardRoot() {
        if (isSdcardMount()) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return "";
    }

    public static boolean isFileExists(String path) {
        return new File(path).exists();
    }

    public static boolean makeDir(String subPath) {
        File file = new File(getSdcardRoot() + File.separator + subPath.trim());
        if (file.exists()) {
            return true;
        }
        return file.mkdirs();
    }
}
