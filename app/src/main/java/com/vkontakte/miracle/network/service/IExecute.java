package com.vkontakte.miracle.network.service;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IExecute {

    @FormUrlEncoded
    @POST("execute.getNewsfeedSmart")
    Call<JSONObject> getNewsfeedSmart(@Field("start_from") String start_from,
                                      @Field("count") int count,
                                      @Field("filters") String filters,
                                      @Field("extended") int extended,
                                      @Field("fields") String fields);

}
