package com.airguard.exception;

public class AuthException extends CommonException {

    public static final int AUTH_EXCEPTION = 800;
    public static final int PREMIUM_AUTH_EXCEPTION = 809;

    public AuthException(int errorCode) {
        super(errorCode);
    }

}
