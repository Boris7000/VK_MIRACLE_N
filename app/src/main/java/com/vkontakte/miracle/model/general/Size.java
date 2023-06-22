package com.vkontakte.miracle.model.general;

import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;

import com.miracle.engine.annotation.MayBeEmpty;

import org.json.JSONException;
import org.json.JSONObject;

public class Size {

    private final int width;
    private final int height;
    private final String type;
    private final String url;

    public Size(JSONObject jsonObject) throws JSONException {
        this(jsonObject,false);
    }

    public Size(JSONObject jsonObject, boolean forDocument) throws JSONException {

        url = jsonObject.optString("url",jsonObject.optString("src"));

        type = jsonObject.optString("type","s");

        int width = jsonObject.optInt("width");

        int height = jsonObject.optInt("height");

        if(width==0||height==0){
            switch (type){
                default:
                case "s":{
                    int size = forDocument?100:75;
                    width = size;
                    height = size;
                    break;
                }
                case "m":{
                    int size = 130;
                    width = size;
                    height = size;
                    break;
                }
                case "x":{
                    int size = 604;
                    width = size;
                    height = size;
                    break;
                }
            }
        }

        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getType() {
        return type;
    }

    @MayBeEmpty
    public String getUrl() {
        return url;
    }

    @Nullable
    public static Size getSizeForWidth(int width, ArrayMap<String,Size> sizes, boolean need3x2){
        if(width<=75){
            return sizes.get("s");
        } else {
            if(width<=130){
                if(need3x2){
                    return findFor("o", sizes, 75, true);
                } else {
                    return findFor("m", sizes, 75, false);
                }
            } else {
                if(need3x2){
                    if(width<=200){
                        return findFor("p", sizes, 130, true);
                    } else {
                        if(width<=320){
                            return findFor("q", sizes, 200, true);
                        } else {
                            if(width<=510){
                                return findFor("r", sizes, 320, true);
                            }
                        }
                    }
                } else {
                    if(width<=604){
                        return findFor("x", sizes, 130, false);
                    } else {
                        if(width<=807){
                            return findFor("y", sizes, 604, false);
                        } else {
                            if(width<=1024){
                                return findFor("z", sizes, 807, false);
                            }else {
                                return findFor("w", sizes, 1024, false);
                            }
                        }
                    }
                }
            }
        }
        return sizes.get("s");
    }

    private static Size findFor(String type, ArrayMap<String,Size> sizes, int reserveWidth, boolean need3x2){
        Size size = sizes.get(type);
        if(size==null){
            return getSizeForWidth(reserveWidth, sizes, need3x2);
        } else {
            return size;
        }
    }

    /* Для фотографии
        s — пропорциональная копия изображения с максимальной стороной 75px;
        m — пропорциональная копия изображения с максимальной стороной 130px;
        x — пропорциональная копия изображения с максимальной стороной 604px;
        o — если соотношение "ширина/высота" исходного изображения меньше или равно 3:2, то пропорциональная копия с максимальной шириной 130px.
        Если соотношение "ширина/высота" больше 3:2, то копия обрезанного слева изображения с максимальной шириной 130px и соотношением сторон 3:2.
        p — если соотношение "ширина/высота" исходного изображения меньше или равно 3:2, то пропорциональная копия с максимальной шириной 200px.
        Если соотношение "ширина/высота" больше 3:2, то копия обрезанного слева и справа изображения с максимальной шириной 200px и соотношением сторон 3:2.
        q — если соотношение "ширина/высота" исходного изображения меньше или равно 3:2, то пропорциональная копия с максимальной шириной 320px.
        Если соотношение "ширина/высота" больше 3:2, то копия обрезанного слева и справа изображения с максимальной шириной 320px и соотношением сторон 3:2.
        r — если соотношение "ширина/высота" исходного изображения меньше или равно 3:2, то пропорциональная копия с максимальной шириной 510px.
        Если соотношение "ширина/высота" больше 3:2, то копия обрезанного слева и справа изображения с максимальной шириной 510px и соотношением сторон 3:2
        y — пропорциональная копия изображения с максимальной стороной 807px;
        z — пропорциональная копия изображения с максимальным размером 1080x1024;
        w — пропорциональная копия изображения с максимальным размером 2560x2048px.
     */

    /*
    s — Пропорциональная копия изображения с максимальной стороной 100px;
    m — Пропорциональная копия изображения с максимальной стороной 130px;
    x — Пропорциональная копия изображения с максимальной стороной 604px;
    y — Пропорциональная копия изображения с максимальной стороной 807px;
    z — Пропорциональная копия изображения с максимальным размером 1080x1024px;
    o — Копия изображения с размерами оригинала.
    */

}
