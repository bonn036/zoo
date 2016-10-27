package com.mmnn.zoo.service.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dz on 2016/10/27.
 */
public class MyGift implements Parcelable {
    public static final Creator<MyGift> CREATOR = new Creator<MyGift>() {
        @Override
        public MyGift createFromParcel(Parcel in) {
            return new MyGift(in);
        }

        @Override
        public MyGift[] newArray(int size) {
            return new MyGift[size];
        }
    };
    protected int mPid;
    protected String mName;

    public MyGift(int pid, String name) {
        mPid = pid;
        mName = name;
    }

    protected MyGift(Parcel in) {
        mPid = in.readInt();
        mName = in.readString();
    }

    public int getPid() {
        return mPid;
    }

    public String getmName() {
        return mName;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mPid);
        dest.writeString(mName);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
