package com.airguard.exception;

public class ExternalApiException extends CommonException {

    public static final int EXTERNAL_API_CALL_EXCEPTION = 991;

    public static final int PLATFORM_API_CALL_EXCEPTION = 888;


    public ExternalApiException(int errorCode) {
        super(errorCode);
    }
}
