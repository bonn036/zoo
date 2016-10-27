package com.mmnn.zoo.service;
import com.mmnn.zoo.service.model.MyGift;
// Declare any non-default types here with import statements

interface IMyDo {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    MyGift onRead();
    boolean onWrite(in MyGift myGift);
}
