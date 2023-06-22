package com.vkontakte.miracle.network.service;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IMessages {

    @FormUrlEncoded
    @POST("messages.getConversations")
    Call<JSONObject> getConversations(@Field("offset") int offset,
                                      @Field("count") int count,
                                      @Field("start_message_id") String start_message_id,
                                      @Field("filter") String filter,
                                      @Field("extended") int extended,
                                      @Field("fields") String fields);

    @FormUrlEncoded
    @POST("messages.getConversationsById")
    Call<JSONObject> getConversationsById(@Field("peer_ids") String peerIds,
                                          @Field("extended") int extended,
                                          @Field("fields") String fields);

    @FormUrlEncoded
    @POST("messages.getConversationMembers")
    Call<JSONObject> getConversationMembers(@Field("peer_id") String peerId,
                                            @Field("offset") int offset,
                                            @Field("count") int count,
                                            @Field("extended") int extended,
                                            @Field("fields") String fields);

    @FormUrlEncoded
    @POST("messages.getHistory")
    Call<JSONObject> getHistory(@Field("peer_id") String peerId,
                                @Field("offset") int offset,
                                @Field("count") int count,
                                @Field("start_message_id") String startMessageId,
                                @Field("extended") int extended,
                                @Field("fields") String fields);

    @FormUrlEncoded
    @POST("messages.getById")
    Call<JSONObject> getById(@Field("message_ids") String messageIds,
                             @Field("extended") int extended,
                             @Field("fields") String fields);

    @FormUrlEncoded
    @POST("messages.getLongPollServer")
    Call<JSONObject> getLongPollServer(@Field("need_pts") int needPts,
                                       @Field("lp_version") int lpVersion);



}
