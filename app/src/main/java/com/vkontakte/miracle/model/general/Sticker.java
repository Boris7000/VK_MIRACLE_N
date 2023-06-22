package com.vkontakte.miracle.model.general;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Sticker {

    private final String id;
    private final String productId;
    private final ArrayList<Image> images;
    private final ArrayList<Image> imagesWithBackground;
    private String animationUrl;
    private String animationWithBackgroundUrl;
    private final boolean isAllowed;

    public Sticker(JSONObject jsonObject) throws JSONException {

        id = jsonObject.getString("sticker_id");
        productId = jsonObject.getString("product_id");

        images = new ArrayList<>();
        JSONArray jaImages = jsonObject.getJSONArray("images");
        for(int i=0; i<jaImages.length(); i++){
            images.add(new Image(jaImages.getJSONObject(i)));
        }

        imagesWithBackground = new ArrayList<>();
        jaImages = jsonObject.getJSONArray("images_with_background");
        for(int i=0; i<jaImages.length(); i++){
            imagesWithBackground.add(new Image(jaImages.getJSONObject(i)));
        }

        if(jsonObject.has("animations")){
            JSONArray animations = jsonObject.getJSONArray("animations");
            JSONObject animation = animations.getJSONObject(0);
            String type = animation.getString("type");
            if(type.equals("light")){
                animationUrl = animation.getString("url");
                animation = animations.getJSONObject(1);
                animationWithBackgroundUrl = animation.getString("url");
            } else if (type.equals("dark")){
                animationWithBackgroundUrl = animation.getString("url");
                animation = animations.getJSONObject(1);
                animationUrl = animation.getString("url");
            }
        }
        isAllowed = jsonObject.optBoolean("is_allowed");
    }

    public String getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public ArrayList<Image> getImagesWithBackground() {
        return imagesWithBackground;
    }

    public String getAnimationUrl() {
        return animationUrl;
    }

    public String getAnimationWithBackgroundUrl() {
        return animationWithBackgroundUrl;
    }

    public boolean isAllowed() {
        return isAllowed;
    }
}
