package com.vkontakte.miracle.model.general;

import androidx.annotation.Nullable;

import com.miracle.engine.recyclerview.asymmetricgrid.GridHelper;
import com.vkontakte.miracle.model.photos.Photo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Attachments {

    private final ArrayList<Photo> photos = new ArrayList<>();
    private final GridHelper.GridBundle gridBundle;

    private Sticker sticker;

    private Gift gift;

    public Attachments(JSONArray jsonArray) throws JSONException {
        this(jsonArray, false);
    }

    public Attachments(JSONArray jsonArray, boolean generateGrid) throws JSONException {
        for (int i=0; i<jsonArray.length();i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String type = jsonObject.getString("type");
            switch (type){
                case "sticker":{
                    sticker = new Sticker(jsonObject.getJSONObject("sticker"));
                    break;
                }
                case "gift":{
                    gift = new Gift(jsonObject.getJSONObject("gift"));
                    break;
                }
                case "photo":{
                    Photo photo = new Photo(jsonObject.getJSONObject("photo"));
                    photos.add(photo);
                    break;
                }
                /*
                case "link":{
                    links.add(new LinkItem(jsonObject.getJSONObject("link")));
                    break;
                }
                case "audio":{
                    AudioItem audioItem = new AudioItem(jsonObject.getJSONObject("audio"));
                    audios.add(new AudioItemWF().create(audioItem, this));
                    break;
                }

                 */
            }
        }

        if(generateGrid) {
            ArrayList<GridHelper.SizeItem> sizeItemsAL = new ArrayList<>();
            if (!photos.isEmpty()) {
                for (Photo photo:photos) {
                    Size size = Size.getSizeForWidth(604, photo.getSizes(),false);
                    if(size!=null){
                        sizeItemsAL.add(new GridHelper.SizeItem(size.getWidth(),size.getHeight()));
                    }
                }
            }

            GridHelper.SizeItem[] sizeItems = new GridHelper.SizeItem[sizeItemsAL.size()];
            for (int i = 0; i < sizeItems.length; i++) {
                sizeItems[i] = sizeItemsAL.get(i);
            }

            gridBundle = GridHelper.GridBundle.calculate(sizeItems);
        } else {
            gridBundle = null;
        }

    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    @Nullable
    public GridHelper.GridBundle getGridBundle() {
        return gridBundle;
    }

    @Nullable
    public Sticker getSticker() {
        return sticker;
    }

    @Nullable
    public Gift getGift() {
        return gift;
    }
}
