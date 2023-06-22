package com.vkontakte.miracle.network.vkapi;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class VKResponseUtil {

    private static final String unknownError = "Unknown error";

    public static JSONObject validate(Response<JSONObject> response, boolean checkErrorInResponse) throws Exception{
        try (ResponseBody errorBody = response.errorBody()) {
            if (errorBody != null) {

                JSONObject joErrorBody = null;

                try {
                    joErrorBody =  new JSONObject(errorBody.string());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(joErrorBody!=null) {
                    if (joErrorBody.has("error_description")) {
                        throw new Exception(joErrorBody.getString("error_description"));
                    } else {
                        throw new Exception(unknownError);
                    }
                } else {
                    throw new Exception(unknownError);
                }
            } else {
                JSONObject joResponseBody = response.body();
                if(joResponseBody!=null){
                    if (checkErrorInResponse&&joResponseBody.has("error")) {
                        if (joResponseBody.get("error") instanceof JSONObject) {
                            JSONObject joError = joResponseBody.getJSONObject("error");
                            String errorMessage = getJSONStringIfHas(joError, "error_msg", unknownError);
                            int errorCode = getJSONIntIfHas(joError, "error_code", 0);
                            throw new VKException(errorMessage, errorCode);
                        } else {
                            throw new Exception(unknownError);
                        }
                    } else {
                        if (joResponseBody.has("response")) {
                            if (joResponseBody.get("response") instanceof JSONObject) {
                                JSONObject joResponse = joResponseBody.getJSONObject("response");
                                if (checkErrorInResponse&&joResponse.has("error")) {
                                    if (joResponse.has("error_description")) {
                                        throw new Exception(joResponse.getString("error_description"));
                                    } else {
                                        throw new Exception(unknownError);
                                    }
                                }
                            }
                        }
                        return joResponseBody;
                    }
                } else {
                    throw new Exception(unknownError);
                }
            }
        }
    }

    public static JSONObject validate(Response<JSONObject> response) throws Exception{
        return validate(response, false);
    }

    public static boolean intAsBoolean(int integer){
        return integer==1;
    }

    public static boolean getJSONIntAsBoolean(JSONObject jsonObject, String name) throws JSONException{
        return jsonObject.getInt(name)==1;
    }

    public static boolean getJSONIntAsBooleanIfHas(JSONObject jsonObject, String name, boolean otherwise) throws JSONException{
        if (jsonObject.has(name)){
            return jsonObject.getInt(name)==1;
        } else {
            return otherwise;
        }
    }

    public static double getJSONDoubleIfHas(JSONObject jsonObject, String name, double otherwise) throws JSONException{
        if (jsonObject.has(name)){
            return jsonObject.getDouble(name);
        } else {
            return otherwise;
        }
    }

    public static long getJSONLongIfHas(JSONObject jsonObject, String name, long otherwise) throws JSONException{
        if (jsonObject.has(name)){
            return jsonObject.getLong(name);
        } else {
            return otherwise;
        }
    }

    public static boolean getJSONBooleanIfHas(JSONObject jsonObject, String name, boolean otherwise) throws JSONException{
        if (jsonObject.has(name)){
            return jsonObject.getBoolean(name);
        } else {
            return otherwise;
        }
    }

    public static int getJSONIntIfHas(JSONObject jsonObject, String name, int otherwise) throws JSONException{
        if (jsonObject.has(name)){
            return jsonObject.getInt(name);
        } else {
            return otherwise;
        }
    }

    public static String getJSONStringIfHas(JSONObject jsonObject, String name, String otherwise) throws JSONException{
        if (jsonObject.has(name)){
            return jsonObject.getString(name);
        } else {
            return otherwise;
        }
    }

}
