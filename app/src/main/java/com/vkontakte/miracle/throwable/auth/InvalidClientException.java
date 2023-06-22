package com.vkontakte.miracle.throwable.auth;

public class InvalidClientException extends Exception {

    private final String errorType;

    public InvalidClientException(String message,String errorType) {
        super(message);
        this.errorType = errorType;
    }

    public String getErrorType() {
        return errorType;
    }
}
