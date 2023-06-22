package com.vkontakte.miracle.response.auth;

import static com.vkontakte.miracle.network.vkapi.APIMethodsFactory.auth;
import static com.vkontakte.miracle.network.vkapi.VKResponseUtil.getJSONStringIfHas;

import com.vkontakte.miracle.network.vkapi.APIConstants;
import com.vkontakte.miracle.throwable.auth.InvalidClientException;
import com.vkontakte.miracle.throwable.auth.NeedCaptchaException;
import com.vkontakte.miracle.throwable.auth.NeedValidationException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class AuthResponse {

    //joErrorBody {"error":"need_captcha","captcha_sid":"304628972584","captcha_img":"https:\/\/api.vk.com\/captcha.php?sid=304628972584","captcha_ts":1.682695583522E9,"captcha_attempt":1}
    //joErrorBody {"error":"invalid_client","error_description":"Неправильный логин или пароль","error_type":"username_or_password_is_incorrect"}
    //joErrorBody {"error":"need_validation","error_description":"use libverify","validation_type":"2fa_libverify","validation_sid":"2fa_201000682_2274003_6e43b44c49309b2926","phone_mask":"+7 *** *** ** 92","redirect_uri":"https:\/\/m.vk.com\/login?act=authcheck&api_hash=915939a77f0e9c6c60","validation_resend":"","validation_external_id":"PUkKGlxRBRIZAg1UDwZZUXJbWUUZFg8DMAIKE2pdD0R8SUtaF1ETEiMZBxdZaw8DMAIKE2pdD0R8SUtaF0cKDRkdDARGXQQIZFFLVBkWGAM0HQAVUGsMFCkeGVQPbzZKZB4HF0BAAzkvD0tMDARcX3BYXkMDDF5fflpfQAMFX0pkHhoTR2sKASMFHVQPFj0tBwUNBFpdDyc2GzVZDRpaU2taXEQCBUtOBwUNBFpdD0Z3WlJWZnAgRnVbUlZNDF1dZj4HHVtbHAhmKgcSR1sCAmY4LT0VVh4PKh9JEFpGSx5-XVJWR0FQRnRYUERNBV9SdkJLWhdBGAM0NAASFw5ZVndbWUYDDFlKZBgAEhcOSUQ7"}
    //joResponseBody {"access_token":"vk1.a.ML0DrzMYVTx7NFEVGyW2HwPY7o2Eu7CemWef1iHoZsMf9fo94ADG4-nBghwQ6yQ7RhEYqJqW8F5dKxLlYxM0i_u8O8rz5c1MDAMPJ9b_yVwSLW6t4Xj3GGhiGcF1nuy9n7d-ZUGJT9Gj9tXrbgEjhUYjz37bEqOQFSwQOb0hiAmUM1p-0A1ZIub1yO6FgbTD","expires_in":0,"user_id":201000682,"trusted_hash":"vk1.a.Kk89yjOOTpWHdjGuRkUQiz3PMDm-cHSCbDbllKokU2w18oagYqKOdAI1MwwGTjfESYNH8sUzq6VXCRleO89OVkMXl-nczK9I8Pbl2b1Ko0Yl-nu8pPZsqDU_wVrUlhZUix8h9xF7yS6uLje1gQQ44ZMSp-Z9DPSeuaWW-cTCWIVs5S4lgClTjLX4_bC6JOQ0"}

    private final String token;
    private final String userId;

    private AuthResponse(String userId, String token){
        this.userId = userId;
        this.token = token;
    }

    public static AuthResponse call(String login, String password, HashMap<String,Object> authFields) throws Exception {
        Response<JSONObject> response = auth().token(
                login,
                password,
                "password",
                APIConstants.CLIENT_ID,
                APIConstants.CLIENT_SECRET,
                "all",
                null,
                1,
                1,
                authFields).execute();

        try (ResponseBody errorBody = response.errorBody()) {
            if (errorBody != null) {

                JSONObject joErrorBody = null;

                try {
                    joErrorBody =  new JSONObject(errorBody.string());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(joErrorBody!=null) {
                    String error = joErrorBody.getString("error");
                    String errorDescription = getJSONStringIfHas(joErrorBody, "error_description", error);

                    switch (error) {
                        case "need_captcha": {
                            String captchaSid = joErrorBody.getString("captcha_sid");
                            String captchaImg = joErrorBody.getString("captcha_img");
                            throw new NeedCaptchaException(errorDescription, captchaSid, captchaImg);
                        }
                        case "need_validation": {
                            String validationSid = joErrorBody.getString("validation_sid");
                            String validationType = joErrorBody.getString("validation_type");
                            String phoneMask = joErrorBody.getString("phone_mask");
                            throw new NeedValidationException(errorDescription, validationSid, validationType, phoneMask);
                        }

                        case "invalid_client": {
                            String errorType =  getJSONStringIfHas(joErrorBody, "error_type", error);
                            throw new InvalidClientException(errorDescription, errorType);
                        }

                        default: {
                            throw new Exception(errorDescription);
                        }
                    }
                } else {
                    throw new Exception("Unknown error");
                }
            } else {
                JSONObject joResponseBody = response.body();
                if(joResponseBody!=null) {
                    String token = joResponseBody.getString("access_token");
                    String userId = joResponseBody.getString("user_id");
                    return new AuthResponse(userId, token);
                } else {
                    throw new Exception("Unknown error");
                }
            }
        }
    }


    public String getToken() {
        return token;
    }

    public String getUserId() {
        return userId;
    }
}
