package com.socialone.android.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by david.hodge on 12/24/13.
 */
public class Constants {

    public static final String TWITTER_CALLBACK = "https://social.zinfinitestudios.com/v1/callback/";
    public static final String INSTAGRAM_CALLBACK = "http://social.zinfinitestudios.com/v1/callback/";
    public static final String FOURSQUARE_CALLBACK = "http://social.zinfinitestudios.com/v1/callback/";
    public static final String TUMBLR_CALLBACK = "oauth://socialone";
    public static final String APPNET_CALLBACK = "http://social.zinfinitestudios.com/v1/callback/";
    public static final String LINKEDIN_CALLBACK = "http://social.zinfinitestudios.com/v1/callback/";
    public static final String FLICKR_CALLBACK = "http://www.flickr.com/auth-72157639298400395";
    public static final String FIVEHUNDRED_CALLBACK = "http://social.zinfinitestudios.com/v1/callback/";
    public static final String PLUS_CALLBACK = "http://social.zinfinitestudios.com/v1/callback/";

    public static final String TUMBLR_CONSUMER_KEY = "oXkVuOgUZZJ4FK68vEPXk7kH8zhhnZiUIAQJ8p94jO3GUCG6Sg";
    public static final String TUMBLR_CONSUMER_SECRET = "pWRZdsQPlSm9vVTFiDXCKJTtA2VCiZTpjypThAsmXyNWMERV9O";
    public static final String TUMBLR_ACCESS = "tumblr_access";
    public static final String TUMBLR_SECRET = "tumblr_secret";

    public static final String FLICKR_KEY = "87d0ccf46bf73b10709e03d78edb0d25";
    public static final String FLICKR_SECRET = "5d1ee0b6ac5c67e9";

    public static final String INSTAGRAM_KEY = "96908692759242ba8c563dca55eb1b14";
    public static final String INSTAGRAM_SECRET = "cf28648ceb1e452dadc734fb935576e7";

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

    public static final String TWITTER_TOKEN = " ";
    public static final String TWITTER_SECRET = " ";

    public static final String TWIT_CONSUMER_KEY = "ifls6s19VWz7xJjtlILo0Q";
    public static final String TWIT_CONSUMER_SECRET = "AXiyrJ2oB6iKTO2zYIHkAhkKUtsNzJbO5kEAJjcwoSc";

//    public static final String TWIT_CONSUMER_KEY = "DSR9G9DlPj3P0dMIL32dbA";
//    public static final String TWIT_CONSUMER_SECRET = "sBcmiuIbR2Sl7waNZ2wqFGzpjINjzzUWKuyuRNHqqU";
    public static final String TWIT_ACCESS_TOKEN = "twit.token";
    public static final String TWIT_ACCESS_SECRET = "twit.secret";

    public static final String GOOGLE_KEY = "AIzaSyDOYgDEO6DPDeQxFF-zqjwAarG8Kna5j2g";

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


    /* Important: set to false when doing release builds */
    public static final boolean DEBUG = false;

    public static final boolean PRO_VERSION = false;

    public static final String ERR_CODE_FLICKR_UNAVAILABLE = "105";

    /* Global app prefs */
    public static final String PREFS_NAME = "glimmr_prefs";

    /* General use SharedPreferences keys */
    public static final String KEY_IS_FIRST_RUN = "glimmr_is_first_run";
    public static final String KEY_OAUTH_TOKEN = "glimmr_oauthtoken";
    public static final String KEY_TOKEN_SECRET = "glimmr_tokensecret";
    public static final String KEY_ACCOUNT_USER_NAME = "glimmr_acc_user_name";
    public static final String KEY_ACCOUNT_USER_ID = "glimmr_acc_user_id";

    /* Attributes to fetch for a photo */
    public static final Set<String> EXTRAS = new HashSet<String>();
    static {
        EXTRAS.add("owner_name");
        EXTRAS.add("url_q");  /* large square 150x150 */
        EXTRAS.add("url_m");  /* small, 240 on longest side */
        EXTRAS.add("url_l");
        EXTRAS.add("views");
        EXTRAS.add("description");
        EXTRAS.add("dates");
        EXTRAS.add("taken_dates");
        EXTRAS.add("tags");
    }

    /* Global preferences keys */
    public static final String KEY_INTERVALS_LIST_PREFERENCE
            = "notificationIntervals";
    public static final String KEY_INITIAL_TAB_LIST_PREFERENCE
            = "initialTab";
    public static final String KEY_ENABLE_NOTIFICATIONS
            = "enableNotifications";
    public static final String KEY_ENABLE_CONTACTS_NOTIFICATIONS
            = "enableContactsNotifications";
    public static final String KEY_ENABLE_ACTIVITY_NOTIFICATIONS
            = "enableActivityNotifications";
    public static final String KEY_ENABLE_USAGE_TIPS
            = "enableUsageTips";
    public static final String KEY_SLIDESHOW_INTERVAL
            = "slideshowInterval";
    public static final String KEY_HIGH_QUALITY_THUMBNAILS
            = "highQualityThumbnails";

    /* Number of items to fetch per page for calls that support pagination */
    public static final int FETCH_PER_PAGE = 20;

    /* Notification ids */
    public static final int NOTIFICATION_NEW_CONTACTS_PHOTOS = 0;
    public static final int NOTIFICATION_NEW_ACTIVITY = 1;
    public static final int NOTIFICATION_PHOTOS_UPLOADING = 2;

    /* Tape managed task queues */
    public static final String PHOTOSET_QUEUE = "photoset_task_queue.json";
    public static final String GROUP_QUEUE = "group_task_queue.json";
    public static final String UPLOAD_QUEUE = "upload_task_queue.json";

    public static final String PRO_MARKET_LINK =
            "market://details?id=com.bourke.glimmrpro";

    public static final String DHODGE_URL = "http://about.me/davidhodgejr";
    public static final String TWITTER_URL = "https://twitter.com/davidhodge229";
    public static final String APP_NET_URL = "https://alpha.app.net/david_hodge";
    public static final String PLUS_URL = "https://plus.google.com/u/0/+DavidHodge229/posts";
    public static final String EMAIL_URL = "dhodge.jr229@gmail.com";
    public static final String APP_RATE_URL = "https://play.google.com/store/apps/details?id=com.socialone.android";
    public static final String OTHER_APPS_URL = "https://play.google.com/store/apps/developer?id=Vapr-Ware";
}
