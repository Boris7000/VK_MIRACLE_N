package com.vkontakte.miracle.response.wall;

import static com.vkontakte.miracle.network.vkapi.APIMethodsFactory.execute;
import static com.vkontakte.miracle.network.vkapi.VKResponseUtil.getJSONStringIfHas;

import com.vkontakte.miracle.model.general.ExtendedArrays;
import com.vkontakte.miracle.model.wall.Post;
import com.vkontakte.miracle.network.vkapi.VKResponseUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class FeedResponse {

    private final List<Post> posts;
    private final String nextFrom;
    private final ExtendedArrays extendedArrays;

    private FeedResponse(List<Post> posts, String nextFrom, ExtendedArrays extendedArrays){
        this.posts = posts;
        this.nextFrom = nextFrom;
        this.extendedArrays = extendedArrays;
    }

    public static FeedResponse call(String startFrom, int step) throws Exception {

        Response<JSONObject> response = execute()
                .getNewsfeedSmart(startFrom, step,"post",
                        1,"photo_100,photo_200,verified,sex").execute();

        JSONObject joResponse = VKResponseUtil.validate(response).getJSONObject("response");

        ExtendedArrays extendedArrays = new ExtendedArrays(joResponse);

        JSONArray items = joResponse.getJSONArray("items");

        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            JSONObject postObject = items.getJSONObject(i);
            if(postObject.has("post_type")){
                posts.add(new Post(postObject));
            }
        }

        String nextFrom = joResponse.optString("next_from");

        return new FeedResponse(posts, nextFrom, extendedArrays);
    }

    public List<Post> getPosts() {
        return posts;
    }

    public String getNextFrom() {
        return nextFrom;
    }

    public ExtendedArrays getExtendedArrays() {
        return extendedArrays;
    }
}
