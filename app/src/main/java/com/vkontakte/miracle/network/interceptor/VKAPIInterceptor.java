package com.vkontakte.miracle.network.interceptor;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.network.vkapi.APIConstants;
import com.vkontakte.miracle.memory.storage.UsersStorage;
import com.vkontakte.miracle.util.DeviceUtil;

import java.io.IOException;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VKAPIInterceptor implements Interceptor {
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {

        Request request = chain.request();
        RequestBody requestBody = request.body();

        FormBody.Builder formBodyBuilder = new FormBody.Builder();

        boolean hasApiVersion = false;
        boolean hasAuthorization = false;
        boolean hasLang = false;

        if(requestBody instanceof FormBody){
            FormBody formBody = (FormBody) requestBody;
            for (int i=0; i<formBody.size();i++){
                String parameter = formBody.name(i);
                formBodyBuilder.add(parameter, formBody.value(i));
                if(parameter.equals("v")) {
                    hasApiVersion = true;
                    continue;
                }
                if(parameter.equals("access_token")) {
                    hasAuthorization = true;
                    continue;
                }
                if(parameter.equals("lang")) {
                    hasLang = true;
                }
            }
        }

        if(!hasApiVersion) {
            formBodyBuilder.add("v", APIConstants.API_VERSION);
        }
        if(!hasLang) {
            formBodyBuilder.add("lang", Locale.getDefault().getLanguage());
        }

        String userAgent = DeviceUtil.getUserAgent();

        Request.Builder requestBuilder =  request.newBuilder();

        if (!hasAuthorization){
            String token = String.format("Bearer %s", UsersStorage.get().requireCurrentUser().getAccessToken());
            requestBuilder.addHeader("Authorization", token);
        }

        Request newRequest = requestBuilder
                .addHeader("X-VK-Android-Client", "new")
                .addHeader("User-Agent", userAgent)
                .addHeader("Content-type", "application/json")
                .post(formBodyBuilder.build())
                .build();

        return chain.proceed(newRequest);
    }
}
