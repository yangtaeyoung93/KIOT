package com.airguard.exception;

public class TokenAuthException extends AuthException {

    public static final int TOKEN_AUTH_EXCEPTION = 820;
    public static final int TOKEN_AUTH_DUPLICATION_EXCEPTION = 821;
    public static final int TOKEN_AUTH_NONE_EXCEPTION = 822;

    public TokenAuthException(int errorCode) {
        super(errorCode);
    }

}
