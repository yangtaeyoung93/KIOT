package com.airguard.exception;

public class ExternalApiException extends CommonException {

    public static final int EXTERNAL_API_CALL_EXCEPTION = 991;

    public ExternalApiException(int errorCode) {
        super(errorCode);
    }
}
