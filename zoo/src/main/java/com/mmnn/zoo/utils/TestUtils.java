package com.mmnn.zoo.utils;

import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class TestUtils {
    public static void createFile(String filePathAndName, String fileContent) {

        try {
            Log.e("ddD: ", "file path :" + filePathAndName);
            String filePath = filePathAndName;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            if (!myFilePath.exists()) {
                myFilePath.createNewFile();
            }
            FileWriter resultFile = new FileWriter(myFilePath);
            PrintWriter myFile = new PrintWriter(resultFile);
            String strContent = fileContent;
            myFile.println(strContent);
            myFile.close();
            resultFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
