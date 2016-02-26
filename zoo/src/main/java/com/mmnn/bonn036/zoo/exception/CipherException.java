package com.mmnn.bonn036.zoo.exception;


public class CipherException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public CipherException(String msg) {
        super(msg);
    }

    public CipherException(String message, Throwable cause) {
        super(message, cause);
    }
}
