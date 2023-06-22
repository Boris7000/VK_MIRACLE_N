package com.vkontakte.miracle.model.photos;

import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;

import com.miracle.engine.annotation.MayBeEmpty;
import com.miracle.engine.recyclerview.TypedData;
import com.vkontakte.miracle.model.general.MediaItem;
import com.vkontakte.miracle.model.general.Size;
import com.vkontakte.miracle.util.constants.TypedDataConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Photo extends MediaItem implements TypedData {

    private final String albumId;
    private final String text;
    private final ArrayMap<String,Size> sizes;

    public Photo(JSONObject jsonObject) throws JSONException {
        super(jsonObject);

        albumId = jsonObject.getString("album_id");

        text = jsonObject.optString("text");

        if(jsonObject.has("sizes")){
            sizes = new ArrayMap<>();
            JSONArray jaSizes = jsonObject.getJSONArray("sizes");
            for(int i=0;i<jaSizes.length();i++){
                Size size = new Size(jaSizes.getJSONObject(i));
                sizes.put(size.getType(),size);
            }
        } else {
            sizes = null;
        }

    }

    public String getAlbumId() {
        return albumId;
    }

    @MayBeEmpty
    public String getText() {
        return text;
    }

    @Nullable
    public ArrayMap<String, Size> getSizes() {
        return sizes;
    }

    @Override
    public int getDataType() {
        return TypedDataConstants.TYPE_PHOTO;
    }
}
