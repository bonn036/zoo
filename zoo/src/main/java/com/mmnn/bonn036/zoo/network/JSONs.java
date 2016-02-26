// Copyright 2013 Mobvoi Inc. All Rights Reserved.
package com.mmnn.bonn036.zoo.network;

import com.alibaba.fastjson.JSON;

public class JSONs {
    /**
     * @param t data object
     * @return JSON format string,will return empty string when occur exception.
     */
    public final static <T> String toJSONString(T t) {
        try {
            return JSON.toJSONString(t);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * @param jsonString
     * @param clazz      the class type of target object
     * @return target object which convert by parameter JSON string, will return
     * null when occur parse exception.
     */
    public final static <T> T parseObject(String jsonString, Class<T> clazz) {
        try {
            return JSON.parseObject(jsonString, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param data
     * @param clazz the class type of target object
     * @return target object which convert by parameter data, will return null
     * when occur parse exception.
     */
    public final static <T> T parseObject(byte[] data, Class<T> clazz) {
        try {
            return JSON.parseObject(new String(data, "UTF-8"), clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
