package com.vkontakte.miracle.network.client;

import com.vkontakte.miracle.network.converter.JSONConverterFactory;
import com.vkontakte.miracle.network.interceptor.AuthInterceptor;
import com.vkontakte.miracle.network.interceptor.VKAPIInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class ClientFactory {

    private static OkHttpClient.Builder createBuilder(){
        return new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);
    }

    public static OkHttpClient createAuthClient(){
        return createBuilder().addInterceptor(new AuthInterceptor()).build();
    }

    public static OkHttpClient createVKAPIClient(){
        return createBuilder().addInterceptor(new VKAPIInterceptor()).build();
    }

    public static Retrofit createAuthRetro(){
        return new Retrofit.Builder()
                .baseUrl("https://oauth.vk.com/")
                .addConverterFactory(JSONConverterFactory.create())
                .client(createAuthClient())
                .build();
    }

    public static Retrofit createAuthMethodRetro(){
        return new Retrofit.Builder()
                .baseUrl("https://api.vk.com/method/")
                .addConverterFactory(JSONConverterFactory.create())
                .client(createAuthClient())
                .build();
    }

    public static Retrofit createVKAPIRetro(){
        return new Retrofit.Builder()
                .baseUrl("https://api.vk.com/")
                .addConverterFactory(JSONConverterFactory.create())
                .client(createVKAPIClient())
                .build();
    }

    public static Retrofit createVKAPIMethodRetro(){
        return new Retrofit.Builder()
                .baseUrl("https://api.vk.com/method/")
                .addConverterFactory(JSONConverterFactory.create())
                .client(createVKAPIClient())
                .build();
    }

    public static Retrofit createLongPollRetro(){
        return new Retrofit.Builder()
                .baseUrl("https://api.vk.com/")
                .addConverterFactory(JSONConverterFactory.create())
                .client(createAuthClient())
                .build();
    }


}
