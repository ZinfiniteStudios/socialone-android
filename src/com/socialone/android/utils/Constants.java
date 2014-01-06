package com.socialone.android.utils;

/**
 * Created by david.hodge on 12/24/13.
 */
public class Constants {

    public static final String TWITTER_CALLBACK = "http://social.zinfinitestudios.com/v1/callback/";
    public static final String INSTAGRAM_CALLBACK = "http://social.zinfinitestudios.com/v1/callback/";
    public static final String FOURSQUARE_CALLBACK = "http://social.zinfinitestudios.com/v1/callback/";
    public static final String TUMBLR_CALLBACK = "http://social.zinfinitestudios.com/v1/callback/";
    public static final String APPNET_CALLBACK = "http://social.zinfinitestudios.com/v1/callback/";
    public static final String LINKEDIN_CALLBACK = "http://social.zinfinitestudios.com/v1/callback/";
    public static final String FLICKR_CALLBACK = "http://www.flickr.com/auth-72157639298400395";
    public static final String FIVEHUNDRED_CALLBACK = "http://social.zinfinitestudios.com/v1/callback/";
    public static final String PLUS_CALLBACK = "http://social.zinfinitestudios.com/v1/callback/";

    public static final String TUMBLR_CONSUMER_KEY = "oXkVuOgUZZJ4FK68vEPXk7kH8zhhnZiUIAQJ8p94jO3GUCG6Sg";
    public static final String TUMBLR_CONSUMER_SECRET = "pWRZdsQPlSm9vVTFiDXCKJTtA2VCiZTpjypThAsmXyNWMERV9O";

    public static final String REQUEST_URL = "http://www.tumblr.com/oauth/request_token";
    public static final String ACCESS_URL = "http://www.tumblr.com/oauth/access_token";
    public static final String AUTHORIZE_URL = "http://www.tumblr.com/oauth/authorize";

    public static final String OAUTH_CALLBACK_SCHEME = "oauthflow-tumblr";
    public static final String OAUTH_CALLBACK_HOST = "callback";
    public static final String OAUTH_CALLBACK_URL = OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;

    public static final String APP_NET_ID = "zza9bRtZ63UGNAxG965a3Kr2uUkmXAqr";
    public static final String APP_NET_SECRET = "LV6NbFgtJpCmhh6VADnkcWXGHF4tj5eq";

    public static final String FACEBOOK_GRAPH = "http://graph.facebook.com/";

    //500px shit
    public static final String PREF_TOKEN_SECRET = "Troubled.tokenSecret";
    public static final String PREF_ACCES_TOKEN = "Troubled.accesToken";
    public static final String SHARED_PREFERENCES = "TroubledSharedPreferences";

    public static final String TWIT_CONSUMER_KEY = "ifls6s19VWz7xJjtlILo0Q";
    public static final String TWIT_CONSUMER_SECRET = "AXiyrJ2oB6iKTO2zYIHkAhkKUtsNzJbO5kEAJjcwoSc";
    public static final String TWIT_ACCESS_TOKEN = "twit.token";
    public static final String TWIT_ACCESS_SECRET = "twit.secret";

    String permissions[] = {
            "user_about_me",
            "user_activities",
            "user_birthday",
            "user_checkins",
            "user_education_history",
            "user_events",
            "user_groups",
            "user_hometown",
            "user_interests",
            "user_likes",
            "user_location",
            "user_notes",
            "user_online_presence",
            "user_photo_video_tags",
            "user_photos",
            "user_relationships",
            "user_relationship_details",
            "user_religion_politics",
            "user_status",
            "user_videos",
            "user_website",
            "user_work_history",
            "email",

            "read_friendlists",
            "read_insights",
            "read_mailbox",
            "read_requests",
            "read_stream",
            "xmpp_login",
            "ads_management",
            "create_event",
            "manage_friendlists",
            "manage_notifications",
            "offline_access",
            "publish_checkins",
            "publish_stream",
            "rsvp_event",
            "sms",
            //"publish_actions",

            "manage_pages"

    };
}
