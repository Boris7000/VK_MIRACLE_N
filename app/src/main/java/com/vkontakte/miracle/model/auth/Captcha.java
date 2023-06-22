package com.vkontakte.miracle.model.auth;

import com.vkontakte.miracle.throwable.auth.NeedCaptchaException;

public class Captcha {

    private String captchaSid;
    private String captchaImg;

    public String getCaptchaSid() {
        return captchaSid;
    }

    public String getCaptchaImg() {
        return captchaImg;
    }

    public static Captcha fromException(NeedCaptchaException e) {
        Captcha captcha = new Captcha();
        captcha.captchaSid = e.getCaptchaSid();
        captcha.captchaImg = e.getCaptchaImg();
        return captcha;
    }

}
