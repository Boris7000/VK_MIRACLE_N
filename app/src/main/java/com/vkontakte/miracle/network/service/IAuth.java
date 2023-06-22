package com.vkontakte.miracle.network.service;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IAuth {

    @FormUrlEncoded
    @POST("token")
    Call<JSONObject> token(@Field("username") String username,
                           @Field("password") String password,
                           @Field("grant_type") String grantType,
                           @Field("client_id") int clientId,
                           @Field("client_secret") String clientSecret,
                           @Field("scope") String scope,
                           @Field("device_id") String deviceId,
                           @Field("2fa_supported") int twoFaSupported,
                           @Field("libverify_support") int libverifySupport,
                           @FieldMap Map<String,Object> fields);

    @FormUrlEncoded
    @POST("auth.validatePhone")
    Call<JSONObject> validatePhone(@Field("sid") String sid,
                                   @Field("api_id") int apiId,
                                   @Field("client_id") int clientId,
                                   @Field("client_secret") String clientSecret,
                                   @Field("libverify_support") int libverifySupport);
}
