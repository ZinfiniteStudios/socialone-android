package com.socialone.android.fragment.fivehund;

import java.util.ArrayList;

import retrofit.http.GET;

/**
 * Created by david.hodge on 2/24/14.
 */
public interface FiveHundPopularService {
    @GET("/v1/photos?feature=popular&sort=rating&image_size=5&rpp=40")
    PhotosResponse getPopularPhotos();

    static class PhotosResponse {
        ArrayList<Photo> photos;
    }

    static class Photo {
        int id;
        String image_url;
        String name;
        String description;
        User user;
    }

    static class User {
        String fullname;
        String username;
    }
}
