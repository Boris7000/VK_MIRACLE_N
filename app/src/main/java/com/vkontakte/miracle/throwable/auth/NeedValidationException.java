package com.vkontakte.miracle.throwable.auth;

public class NeedValidationException extends Exception{

    private final String validationSid;
    private final String validationType;
    private final String phoneMask;

    public NeedValidationException(String message, String validationSid, String validationType, String phoneMask) {
        super(message);
        this.validationSid = validationSid;
        this.validationType = validationType;
        this.phoneMask = phoneMask;
    }

    public String getValidationSid() {
        return validationSid;
    }

    public String getValidationType() {
        return validationType;
    }

    public String getPhoneMask() {
        return phoneMask;
    }
}
