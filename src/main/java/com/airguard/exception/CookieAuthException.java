package com.airguard.exception;

public class CookieAuthException extends AuthException {

    public static final int COOKIE_AUTH_EXCEPTION = 810;
    public static final int COOKIE_AUTH_DUPLICATION_EXCEPTION = 811;
    public static final int COOKIE_AUTH_NONE_EXCEPTION = 812;
    public static final int COOKIE_AUTH_ADMIN_EXCEPTION = 813;
    public static final int COOKIE_AUTH_MEMBER_EXCEPTION = 814;

    public CookieAuthException(int errorCode) {
        super(errorCode);
    }

}
