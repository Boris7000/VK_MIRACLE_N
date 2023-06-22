package com.vkontakte.miracle.model.general;

import static com.vkontakte.miracle.network.vkapi.VKResponseUtil.intAsBoolean;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Cover implements JSONable{

    private final boolean enabled;
    private final ArrayList<Image> images;

    public Cover(JSONObject jsonObject)throws JSONException {

        enabled = intAsBoolean(jsonObject.optInt("enabled"));

        if (jsonObject.has("images")){
            images = new ArrayList<>();
            JSONArray jaImages = jsonObject.getJSONArray("images");
            for(int i=0; i<jaImages.length(); i++){
                images.add(new Image(jaImages.getJSONObject(i)));
            }
        } else {
            images = null;
        }

    }

    @Nullable
    public ArrayList<Image> getImages() {
        return images;
    }

    public boolean getEnabled() {
        return enabled;
    }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("enabled", enabled?1:0);

        if(images!=null){
            JSONArray jaImages = new JSONArray();
            for (Image image:images) {
                jaImages.put(image.toJSONObject());
            }
            jsonObject.put("images", jaImages);
        }

        return jsonObject;
    }
}
