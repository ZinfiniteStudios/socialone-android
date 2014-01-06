package com.socialone.android.api.facebook;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by david.hodge on 1/5/14.
 */
public class FacebookPost {

    public static final String STATUS_TYPE_ADDED_PHOTOS = "added_photos";
    public static final String STATUS_TYPE_MOBILE_STATUS_UPDATE = "mobile_status_update";
    public static final String STATUS_TYPE_STORY = "shared_story";
    public static final String STATUS_TYPE_APP_STORY = "app_created_story";
    public static final String STATUS = "status";
    public static final String TYPE_PHOTO = "photo";
    public static final String MIXHIBIT_TEXT = "text";
    public static final String FACEBOOK_PICTURE = "picture";

    @SerializedName("id") public String id;
    @SerializedName("object_id") public String objectId;
    @SerializedName("story") public String story;
    @SerializedName("picture") public String picture;
    @SerializedName("status_type") public String statusType;
    @SerializedName("privacy") public PrivacyObject privacyObject;
    @SerializedName("from") public From from;
    @SerializedName("to") public String to;
    @SerializedName("created_time") public String createdTime;
    @SerializedName("type") public String type;
    @SerializedName("updated_time") public String updatedTime;
    @SerializedName("source") public String imageSource;
    @SerializedName("message") public String pictureMessage;
    @SerializedName("place") public Place place;
//    @SerializedName("shares") public Integer shares;
    @SerializedName("images") public List<Image> images;

    public static class Place {
        @SerializedName("id")
        public String id;
        //        @SerializedName("name")
        public String name;
        public String getName() {
            return name;
        }
    }

    public static class PrivacyObject {

        @SerializedName("value")
        public String value;
    }

    public static class From {

        @SerializedName("id")
        public String id;
        @SerializedName("name")
        public String name;
    }

    public static class Image {
        @SerializedName("source") public String source;
    }

}
