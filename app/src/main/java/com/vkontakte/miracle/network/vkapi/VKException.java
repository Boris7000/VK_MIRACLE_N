package com.vkontakte.miracle.network.vkapi;

public class VKException extends Exception {

    private final int errorCode;

    public VKException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
