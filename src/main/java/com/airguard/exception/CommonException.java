package com.airguard.exception;

public class CommonException extends Exception {

    private int errorCode;

    public CommonException(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
