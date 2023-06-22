package com.vkontakte.miracle.throwable.auth;

public class NeedCaptchaException extends Exception{

    private final String captchaSid;
    private final String captchaImg;

    public NeedCaptchaException(String message, String captchaSid, String captchaImg) {
        super(message);
        this.captchaSid = captchaSid;
        this.captchaImg = captchaImg;
    }

    public String getCaptchaSid() {
        return captchaSid;
    }

    public String getCaptchaImg() {
        return captchaImg;
    }

}
