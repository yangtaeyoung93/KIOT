package com.airguard.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ErrorMessageHandler {

    @Autowired
    private CommonErrorMessage commonErrorMessage;

    public String getMessage(Exception e) {
        if (e instanceof CommonException) {
            int error_code = ((CommonException) e).getErrorCode();
            return commonErrorMessage.getMessage(error_code);
        } else {
            return commonErrorMessage.getMessage(-1);
        }
    }

}
