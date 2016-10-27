package com.mmnn.zoo.net;

import java.util.ArrayList;
import java.util.List;

public final class NetRequest {

    private final String mMethod;

    private final String mProtocol;
    private final String mAddress;
    private final int mPort;
    private final String mPath;
    private final String mBody;
    // Parameters should never be null
    private List<NameValuePair> mParams = new ArrayList<>();
    private String mCookie;

    private NetRequest(Builder builder) {
        mMethod = builder.method;
        mProtocol = builder.protocol;
        mAddress = builder.address;
        mPort = builder.port;
        mPath = builder.path;
        mBody = builder.body;
    }

    public String getMethod() {
        return mMethod;
    }

    public String getProtocol() {
        return mProtocol;
    }

    public String getAddress() {
        return mAddress;
    }

    public int getPort() {
        return mPort;
    }

    public String getPath() {
        return mPath;
    }

    public List<NameValuePair> getParamters() {
        return mParams;
    }

    public String getBody() {
        return mBody;
    }

    public String getCookie() {
        return mCookie;
    }

    public void setCookie(String cookie) {
        mCookie = cookie;
    }

    /**
     * add NameValuePair list into request.
     *
     * @param pairs null and empty list will be ignored
     */
    public void addParameters(List<NameValuePair> pairs) {
        if (null != pairs && pairs.size() > 0) {
            mParams.addAll(pairs);
        }
    }

    /**
     * add (key, value) pair into request.
     *
     * @param key   null and "" will be ignored
     * @param value null and "" will be ignored
     */
    public void addParameter(String key, String value) {
        if (null != key && key.length() > 0 && null != value
                && value.length() > 0) {
            NameValuePair pair = new NameValuePair(key, value);
            mParams.add(pair);
        }
    }

    public void addParameter(String key, int value) {
        addParameter(key, String.valueOf(value));
    }

    public void addParameter(String key, long value) {
        addParameter(key, String.valueOf(value));
    }

    public void addParameter(String key, float value) {
        addParameter(key, String.valueOf(value));
    }

    public void addParameter(String key, double value) {
        addParameter(key, String.valueOf(value));
    }

    /**
     * remove pair with key. If not included, do nothing.
     *
     * @param key
     */
    public void removeParameter(String key) {
        NameValuePair target = findParameter(key);
        if (target != null) {
            mParams.remove(target);
        }
    }

    public NameValuePair findParameter(String key) {
        if (null != key && key.length() > 0) {
            for (NameValuePair param : mParams) {
                if (param.getName() != null && param.getName().equals(key)) {
                    return param;
                }
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof NetRequest)) {
            return false;
        }
        NetRequest request = (NetRequest) o;
        if (mPort != request.mPort) {
            return false;
        }
        if (!(mMethod == null ? request.mMethod == null : mMethod
                .equals(request.mMethod))) {
            return false;
        }
        if (!(mProtocol == null ? request.mProtocol == null : mProtocol
                .equals(request.mProtocol))) {
            return false;
        }
        if (!(mAddress == null ? request.mAddress == null : mAddress
                .equals(request.mAddress))) {
            return false;
        }
        if (!(mPath == null ? request.mPath == null : mPath
                .equals(request.mPath))) {
            return false;
        }
        if (mParams.size() != request.mParams.size()) {
            return false;
        }
        final int number = mParams.size();
        for (int i = 0; i < number; i++) {
            NameValuePair here = mParams.get(i);
            NameValuePair there = request.mParams.get(i);
            if (!here.equals(there)) {
                return false;
            }
        }
        return true;
    }

    private int getHashCode(Object o) {
        return o == null ? 0 : o.hashCode();
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + getHashCode(mMethod);
        result = 31 * result + getHashCode(mProtocol);
        result = 31 * result + getHashCode(mAddress);
        result = 31 * result + mPort;
        result = 31 * result + getHashCode(mPath);
        for (NameValuePair param : mParams) {
            result = 31 * result + getHashCode(param);
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(mMethod).append(" - ");
        sb.append(mProtocol).append("://");
        sb.append(mAddress).append(":").append(mPort);
        sb.append(mPath).append("?");
        for (NameValuePair param : mParams) {
            sb.append(param.getName()).append("=").append(param.getValue()).append("&");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public String getUrlString() {
        StringBuffer sb = new StringBuffer();
        sb.append(mProtocol).append("://");
        sb.append(mAddress);
        if (mPort > 0) {
            sb.append(":").append(mPort);
        }
        sb.append(mPath).append("?");
        for (NameValuePair param : mParams) {
            sb.append(param.getName()).append("=").append(param.getValue()).append("&");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static class Builder {
        private final String address;
        private final String path;

        private String method = "GET";
        private String protocol = "http";
        private int port = 80;
        private String body = "";

        public Builder(String address, String path) {
            this.address = address;
            this.path = path;
        }

        public Builder protocol(String protocol, int port) {
            this.protocol = protocol;
            this.port = port;
            return this;
        }

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public NetRequest build() {
            return new NetRequest(this);
        }
    }

    public class NameValuePair {

        private String name;
        private String value;

        public NameValuePair(String name, String value) {

        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }

}
