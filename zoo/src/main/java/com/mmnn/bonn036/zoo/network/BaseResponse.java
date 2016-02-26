package com.mmnn.bonn036.zoo.network;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;

@SuppressWarnings("serial")
public class BaseResponse implements Serializable {
    public String oper;
    public boolean success;
    public long time;
    public String msg;
    public boolean tokenStatus = true;

    public static <E> E parseResponse(byte[] response, Class<E> clazz) {
        if (response != null && response.length > 0) {
            E res = JSONs.parseObject(new String(response), clazz);
            if (res instanceof BaseResponse) {
                ((BaseResponse) res).afterParse();
            }
            return res;
        }
        return null;
    }

    public static <E> boolean isEmpty(E e) {
        if (e == null) {
            return true;
        }
        if (e instanceof Collection<?>) {
            return ((Collection<?>) e).isEmpty();
        } else if (e.getClass().isArray()) {
            return Array.getLength(e) == 0;
        }
        return false;
    }

    public static <E> int getCount(E e) {
        if (e == null) {
            return 0;
        }
        if (e instanceof Collection<?>) {
            return ((Collection<?>) e).size();
        } else if (e.getClass().isArray()) {
            return Array.getLength(e);
        }
        return 0;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isTokenValid() {
        return tokenStatus;
    }

    protected void afterParse() {
    }

    public static class ResponseMap extends HashMap<String, Object> {

        public Integer getInt(String name) {
            return (Integer) get(name);
        }

        public Long getLong(String name) {
            return (Long) get(name);
        }

        public Boolean getBoolean(String name) {
            return (Boolean) get(name);
        }

        public String getString(String name) {
            return (String) get(name);
        }

        public String getMsg() {
            return getString("msg");
        }

        public String getOper() {
            return getString("oper");
        }

        public boolean isSuccess() {
            return getBoolean("success");
        }

        public boolean isTokenValid() {
            Boolean bool = getBoolean("tokenStatus");
            return bool == null ? true : bool.booleanValue();
        }
    }
}
