package com.vkontakte.miracle.model.auth;

import com.vkontakte.miracle.throwable.auth.NeedValidationException;

public class Validation {

    private String validationSid;
    private String validationType;
    private String phoneMask;

    public String getValidationSid() {
        return validationSid;
    }

    public String getValidationType() {
        return validationType;
    }

    public String getPhoneMask() {
        return phoneMask;
    }

    public static Validation fromException(NeedValidationException e) {
        Validation validation = new Validation();
        validation.validationSid = e.getValidationSid();
        validation.validationType = e.getValidationType();
        validation.phoneMask = e.getPhoneMask();
        return validation;
    }


}
