package com.vkontakte.miracle.response.auth;

import static com.vkontakte.miracle.network.vkapi.APIMethodsFactory.authMethods;

import com.vkontakte.miracle.network.vkapi.APIConstants;
import com.vkontakte.miracle.network.vkapi.VKResponseUtil;

import org.json.JSONObject;

import retrofit2.Response;

public class ValidatePhoneResponse {

    //joResponseBody {"response":{"type":"general","sid":"2fa_201000682_2274003_c29d7b83d722955877","delay":60,"libverify_support":true,"validation_type":"sms","validation_resend":"sms","external_id":"PUkKGlxRBRIZAg1UDwZZUXJbWUUZFg8DMAIKE2pdD0R8SUtaF1ETEiMZBxdZaw8DMAIKE2pdD0R8SUtaF0cKDRkdDARGXQQIZFFLVBkWGAM0HQAVUGsMFCkeGVQPbzZKZB4HF0BAAzkvD0tMDARTUHNYWkEEA1xXd19cTwYHXkpkHhoTR2sKASMFHVQPFj0tBwUNBFpdDyc2GzVZDRpaU2taXEQCBUtOBwUNBFpdD0Z3WlJWZnAgRnVbUlZNDF1dZj4HHVtbHAhmKgcSR1sCAmY4LT0VVh4PKh9JEFpGSx5-XVJWR0FQRnRYUERNBV9SdkJLWhdBGAM0NAASFw5ZVndbWUYDDFlKZBgAEhcOSVR2WllGBQJTVBlZW0EBBFtVGQhbT1EDCV51D15EBw1eU35cXlRI"}}

    private final int delay;
    private final String validationType;
    private final String validationResend;

    private ValidatePhoneResponse(int delay, String validationType, String validationResend) {
        this.delay = delay;
        this.validationType = validationType;
        this.validationResend = validationResend;
    }

    public static ValidatePhoneResponse call(String validationSid) throws Exception {
        Response<JSONObject> response = authMethods().validatePhone(
                validationSid,
                APIConstants.CLIENT_ID,
                APIConstants.CLIENT_ID,
                APIConstants.CLIENT_SECRET,
                1).execute();

        JSONObject jsonObject = VKResponseUtil.validate(response).getJSONObject("response");

        int delay = jsonObject.getInt("delay");
        String validationType = jsonObject.getString("validation_type");
        String validationResend = jsonObject.getString("validation_resend");

        return new ValidatePhoneResponse(delay, validationType, validationResend);
    }

    public int getDelay() {
        return delay;
    }

    public String getValidationType() {
        return validationType;
    }

    public String getValidationResend() {
        return validationResend;
    }
}
