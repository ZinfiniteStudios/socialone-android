package com.socialone.android.fragment.fivehund;

import java.util.ArrayList;

import retrofit.http.GET;

/**
 * Created by david.hodge on 2/24/14.
 */
interface FiveHundEditorService {
    @GET("/v1/photos?feature=editors&sort=rating&image_size=5&rpp=40")
    PhotosResponse getPopularPhotos();

    static class PhotosResponse {
       ArrayList<Photo> photos;
    }

    static class Photo {
        int id;
        String image_url;
        String name;
        User user;
    }

    static class User {
        String fullname;
    }
}
